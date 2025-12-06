package no.steffenhove.betongkalkulator.ui.screens

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import no.steffenhove.betongkalkulator.ui.components.AppDropdown
import no.steffenhove.betongkalkulator.ui.components.AppScaffold
import no.steffenhove.betongkalkulator.ui.components.InputField
import no.steffenhove.betongkalkulator.ui.model.AppDatabase
import no.steffenhove.betongkalkulator.ui.model.CalculationEntity
import no.steffenhove.betongkalkulator.ui.model.ConcreteType
import no.steffenhove.betongkalkulator.ui.utils.convertToMeters
import no.steffenhove.betongkalkulator.ui.utils.getConcreteTypesPreference
import no.steffenhove.betongkalkulator.ui.utils.getLastCalculatorPreferences
import no.steffenhove.betongkalkulator.ui.utils.getUnitSystemPreference
import no.steffenhove.betongkalkulator.ui.utils.getWeightUnitPreference
import no.steffenhove.betongkalkulator.ui.utils.saveLastCalculatorPreferences
import kotlin.math.pow
import kotlin.math.sqrt

@Composable
fun CalculationScreen(
    context: Context,
    navigateBack: () -> Unit
) {
    val navContext = LocalContext.current
    val unitSystem = getUnitSystemPreference(context)
    val weightUnit = getWeightUnitPreference(context)

    val metricUnits = listOf("mm", "cm", "m")
    val imperialUnits = listOf("inch", "foot")
    val units = if (unitSystem == "Metrisk") metricUnits else imperialUnits

    val forms = listOf("Kjerne", "Firkant", "Trekant", "Trapes")

    val concreteTypes = remember {
        val stored = getConcreteTypesPreference(context)
        buildList {
            addAll(stored)
            if (stored.none { it.name == "Asfalt" }) add(ConcreteType("Asfalt", 2300.0))
            if (stored.none { it.name == "Egendefinert" }) add(ConcreteType("Egendefinert", 0.0))
        }
    }

    val scope = rememberCoroutineScope()
    val keyboard = LocalSoftwareKeyboardController.current
    val clipboard = LocalClipboardManager.current

    // Hent lagret form, enhet og type fra preferanser
    val (initialForm, initialUnit, initialTypeName) = getLastCalculatorPreferences(context)

    var selectedForm by rememberSaveable { mutableStateOf(initialForm) }
    var selectedUnit by rememberSaveable { mutableStateOf(initialUnit) }
    var selectedConcreteType by remember {
        mutableStateOf(concreteTypes.find { it.name == initialTypeName } ?: concreteTypes[0])
    }

    val concreteDensity = remember { mutableStateOf(TextFieldValue()) }

    val dim1 = remember { mutableStateOf(TextFieldValue()) }
    val dim2 = remember { mutableStateOf(TextFieldValue()) }
    val dim3 = remember { mutableStateOf(TextFieldValue()) }
    val dim4 = remember { mutableStateOf(TextFieldValue()) }
    val thickness = remember { mutableStateOf(TextFieldValue()) }
    val note = remember { mutableStateOf(TextFieldValue()) }

    val f1 = remember { FocusRequester() }
    val f2 = remember { FocusRequester() }
    val f3 = remember { FocusRequester() }
    val f4 = remember { FocusRequester() }
    val f5 = remember { FocusRequester() }
    val f6 = remember { FocusRequester() }

    var result by remember { mutableStateOf(0.0) }
    var error by remember { mutableStateOf("") }
    var showNoteField by remember { mutableStateOf(false) }

    fun parse(input: TextFieldValue): Double =
        input.text
            .replace(",", ".")
            .replace("[^\\d.]".toRegex(), "")
            .toDoubleOrNull() ?: 0.0

    fun buildResultMessage(): String {
        val dims = listOf(dim1.value.text, dim2.value.text, dim3.value.text, dim4.value.text)
            .filter { it.isNotBlank() }
        val thicknessText = thickness.value.text
        val formattedResult = java.text.DecimalFormat("#,###.##").format(result)
        val tonsFormatted = java.text.DecimalFormat("#,###.##").format(result / 1000)
        val tons = if (weightUnit == "kg" && result >= 1000) " / $tonsFormatted t" else ""

        return buildString {
            appendLine(selectedConcreteType.name)
            appendLine(
                if (selectedForm == "Kjerne")
                    "Ø${dim1.value.text} $selectedUnit x $thicknessText $selectedUnit"
                else
                    "${dims.joinToString(" x ")} $selectedUnit x $thicknessText $selectedUnit"
            )
            appendLine("$formattedResult $weightUnit$tons")
            if (note.value.text.isNotBlank()) {
                appendLine(note.value.text)
            }
        }
    }

    fun performCalculation() {
        error = ""

        val d1 = convertToMeters(dim1.value.text, selectedUnit) ?: 0.0
        val d2 = convertToMeters(dim2.value.text, selectedUnit) ?: 0.0
        val d3 = convertToMeters(dim3.value.text, selectedUnit) ?: 0.0
        val d4 = convertToMeters(dim4.value.text, selectedUnit) ?: 0.0
        val t = convertToMeters(thickness.value.text, selectedUnit) ?: 0.0
        val density = if (selectedConcreteType.name == "Egendefinert") {
            parse(concreteDensity.value)
        } else {
            selectedConcreteType.density
        }

        if ((selectedForm == "Kjerne" && d1 == 0.0) || t == 0.0 ||
            (selectedForm == "Firkant" && (d1 == 0.0 || d2 == 0.0)) ||
            (selectedForm == "Trekant" && (d1 == 0.0 || d2 == 0.0)) ||
            (selectedForm == "Trapes" && (d1 == 0.0 || d2 == 0.0 || d3 == 0.0 || d4 == 0.0))
        ) {
            error = "Fyll inn nødvendige mål."
            return
        }

        val volume = when (selectedForm) {
            "Kjerne" -> Math.PI * (d1 / 2).pow(2) * t
            "Firkant" -> d1 * d2 * t
            "Trekant" -> 0.5 * d1 * d2 * t
            "Trapes" -> {
                // Herons formel på trapes som generelt firkant – greit nok som tilnærming
                val s = (d1 + d2 + d3 + d4) / 2
                val area = sqrt((s - d1) * (s - d2) * (s - d3) * (s - d4))
                area * t
            }
            else -> 0.0
        }

        result = if (weightUnit == "lbs") {
            volume * density * 2.20462
        } else {
            volume * density
        }

        scope.launch {
            AppDatabase.getDatabase(context).calculationDao().insert(
                CalculationEntity(
                    form = selectedForm,
                    unit = selectedUnit,
                    concreteType = selectedConcreteType.name,
                    dimensions = listOf(d1, d2, d3, d4).joinToString(),
                    thickness = t.toString(),
                    density = density,
                    result = result,
                    resultUnit = weightUnit
                )
            )
        }

        keyboard?.hide()
    }

    // Lagre valg til preferanser
    LaunchedEffect(selectedForm, selectedUnit, selectedConcreteType) {
        saveLastCalculatorPreferences(context, selectedForm, selectedUnit, selectedConcreteType.name)
    }

    // Sikre at enheten er gyldig i forhold til valgt system
    LaunchedEffect(unitSystem) {
        val validUnits = if (unitSystem == "Metrisk") metricUnits else imperialUnits
        if (selectedUnit !in validUnits) {
            selectedUnit = validUnits.first()
        }
    }

    AppScaffold(
        title = "Vektkalkulator",
        showBack = true,
        onBackClick = navigateBack
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            item {
                AppDropdown("Form", forms, selectedForm) { selectedForm = it }
                Spacer(modifier = Modifier.height(8.dp))
                AppDropdown("Enhet", units, selectedUnit) { selectedUnit = it }
                Spacer(modifier = Modifier.height(8.dp))
                AppDropdown(
                    "Betongtype",
                    concreteTypes.map { it.name }.sorted(),
                    selectedConcreteType.name
                ) {
                    selectedConcreteType = concreteTypes.first { type -> type.name == it }
                }

                Text(
                    "Tetthet: ${selectedConcreteType.density} kg/m³",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 4.dp, bottom = 8.dp)
                )

                if (selectedConcreteType.name == "Egendefinert") {
                    InputField(
                        "Densitet",
                        concreteDensity,
                        "kg/m³",
                        KeyboardType.Number,
                        ImeAction.Next,
                        f1,
                        f2
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
            }

            item {
                // Mål – avhengig av form
                when (selectedForm) {
                    "Kjerne" -> {
                        InputField(
                            "Diameter",
                            dim1,
                            selectedUnit,
                            KeyboardType.Number,
                            ImeAction.Next,
                            f2,
                            f3
                        )
                        InputField(
                            "Tykkelse",
                            thickness,
                            selectedUnit,
                            KeyboardType.Number,
                            ImeAction.Done,
                            f3,
                            onDone = { performCalculation() }
                        )
                    }

                    "Firkant" -> {
                        InputField(
                            "Lengde",
                            dim1,
                            selectedUnit,
                            KeyboardType.Number,
                            ImeAction.Next,
                            f2,
                            f3
                        )
                        InputField(
                            "Bredde",
                            dim2,
                            selectedUnit,
                            KeyboardType.Number,
                            ImeAction.Next,
                            f3,
                            f4
                        )
                        InputField(
                            "Tykkelse",
                            thickness,
                            selectedUnit,
                            KeyboardType.Number,
                            ImeAction.Done,
                            f4,
                            onDone = { performCalculation() }
                        )
                    }

                    "Trekant" -> {
                        InputField(
                            "A-Side",
                            dim1,
                            selectedUnit,
                            KeyboardType.Number,
                            ImeAction.Next,
                            f2,
                            f3
                        )
                        InputField(
                            "B-Side",
                            dim2,
                            selectedUnit,
                            KeyboardType.Number,
                            ImeAction.Next,
                            f3,
                            f4
                        )
                        InputField(
                            "Tykkelse",
                            thickness,
                            selectedUnit,
                            KeyboardType.Number,
                            ImeAction.Done,
                            f4,
                            onDone = { performCalculation() }
                        )
                    }

                    "Trapes" -> {
                        InputField(
                            "A-Side",
                            dim1,
                            selectedUnit,
                            KeyboardType.Number,
                            ImeAction.Next,
                            f2,
                            f3
                        )
                        InputField(
                            "B-Side",
                            dim2,
                            selectedUnit,
                            KeyboardType.Number,
                            ImeAction.Next,
                            f3,
                            f4
                        )
                        InputField(
                            "C-Side",
                            dim3,
                            selectedUnit,
                            KeyboardType.Number,
                            ImeAction.Next,
                            f4,
                            f5
                        )
                        InputField(
                            "D-Side",
                            dim4,
                            selectedUnit,
                            KeyboardType.Number,
                            ImeAction.Next,
                            f5,
                            f6
                        )
                        InputField(
                            "Tykkelse",
                            thickness,
                            selectedUnit,
                            KeyboardType.Number,
                            ImeAction.Done,
                            f6,
                            onDone = { performCalculation() }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(onClick = { performCalculation() }) {
                        Text("Regn ut")
                    }
                    Button(onClick = {
                        if (result > 0.0) {
                            clipboard.setText(AnnotatedString(buildResultMessage()))
                        } else {
                            performCalculation()
                            if (result > 0.0) {
                                clipboard.setText(AnnotatedString(buildResultMessage()))
                            }
                        }
                    }) {
                        Text("Kopier")
                    }
                    Button(onClick = { showNoteField = true }) {
                        Text("Del")
                    }
                }

                if (showNoteField) {
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = note.value,
                        onValueChange = { note.value = it },
                        label = { Text("Valgfritt notat (f.eks. område, kunde, prosjekt)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = {
                        if (result <= 0.0) {
                            performCalculation()
                        }
                        if (result > 0.0) {
                            val message = buildResultMessage()
                            val intent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, message)
                            }
                            navContext.startActivity(
                                Intent.createChooser(
                                    intent,
                                    "Del kalkulasjon via"
                                )
                            )
                        }
                    }) {
                        Text("Del kalkulasjon")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (result > 0.0) {
                    val res = java.text.DecimalFormat("#,###.##").format(result)
                    val tonsFormatted = java.text.DecimalFormat("#,###.##").format(result / 1000)
                    val tons = if (weightUnit == "kg" && result >= 1000) " / $tonsFormatted t" else ""
                    SelectionContainer {
                        Text("Resultat: $res $weightUnit$tons")
                    }
                }

                if (error.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(error, color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}
