@file:OptIn(ExperimentalMaterial3Api::class)

package no.steffenhove.betongkalkulator.ui.screens

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import no.steffenhove.betongkalkulator.ui.components.AppDropdown
import no.steffenhove.betongkalkulator.ui.components.DimensionField
import no.steffenhove.betongkalkulator.ui.model.AppDatabase
import no.steffenhove.betongkalkulator.ui.model.CalculationEntity
import no.steffenhove.betongkalkulator.ui.model.ConcreteType
import no.steffenhove.betongkalkulator.ui.utils.convertToMeters
import no.steffenhove.betongkalkulator.ui.utils.getConcreteTypesPreference
import no.steffenhove.betongkalkulator.ui.utils.getUnitSystemPreference
import no.steffenhove.betongkalkulator.ui.utils.getWeightUnitPreference
import no.steffenhove.betongkalkulator.ui.utils.loadPreference
import no.steffenhove.betongkalkulator.ui.utils.savePreference
import kotlin.math.pow
import kotlin.math.sqrt

@Composable
fun CalculationScreen(context: Context) {
    val navContext = LocalContext.current
    val unitSystem = getUnitSystemPreference(context)
    val weightUnit = getWeightUnitPreference(context)
    val metricUnits = listOf("mm", "cm", "m")
    val units = if (unitSystem == "Metrisk") metricUnits else listOf("inch", "foot")
    val forms = listOf("Kjerne", "Firkant", "Trekant", "Trapes")

    val concreteTypes = remember {
        val stored = getConcreteTypesPreference(context)
        val updated = buildList {
            addAll(stored)
            if (stored.none { it.name == "Asfalt" }) add(ConcreteType("Asfalt", 2300.0))
            if (stored.none { it.name == "Egendefinert" }) add(ConcreteType("Egendefinert", 0.0))
        }
        updated
    }

    val scope = rememberCoroutineScope()
    val keyboard = LocalSoftwareKeyboardController.current

    var selectedForm by rememberSaveable { mutableStateOf(loadPreference(context, "selected_form", forms[0])) }
    var selectedUnit by rememberSaveable { mutableStateOf(loadPreference(context, "selected_unit", units[0])) }
    var selectedConcreteType by remember {
        val defaultTypeName = concreteTypes[0].name
        val savedTypeName = loadPreference(context, "selected_concrete_type", defaultTypeName)
        mutableStateOf(concreteTypes.find { it.name == savedTypeName } ?: concreteTypes[0])
    }

    val concreteDensity = remember { mutableStateOf(TextFieldValue()) }

    val dim1 = remember { mutableStateOf(TextFieldValue()) }
    val dim2 = remember { mutableStateOf(TextFieldValue()) }
    val dim3 = remember { mutableStateOf(TextFieldValue()) }
    val dim4 = remember { mutableStateOf(TextFieldValue()) }
    val thickness = remember { mutableStateOf(TextFieldValue()) }

    val note = remember { mutableStateOf(TextFieldValue()) }
    var showNoteField by remember { mutableStateOf(false) }

    var result by remember { mutableStateOf(0.0) }
    var error by remember { mutableStateOf("") }

    val f1 = remember { FocusRequester() }
    val f2 = remember { FocusRequester() }
    val f3 = remember { FocusRequester() }
    val f4 = remember { FocusRequester() }
    val f5 = remember { FocusRequester() }
    val f6 = remember { FocusRequester() }

    fun parse(input: TextFieldValue): Double =
        input.text.replace(",", ".").replace("[^\\d.]".toRegex(), "").toDoubleOrNull() ?: 0.0

    fun performCalculation() {
        val d1 = convertToMeters(dim1.value.text, selectedUnit) ?: 0.0
        val d2 = convertToMeters(dim2.value.text, selectedUnit) ?: 0.0
        val d3 = convertToMeters(dim3.value.text, selectedUnit) ?: 0.0
        val d4 = convertToMeters(dim4.value.text, selectedUnit) ?: 0.0
        val t = convertToMeters(thickness.value.text, selectedUnit) ?: 0.0
        val density = if (selectedConcreteType.name == "Egendefinert") parse(concreteDensity.value) else selectedConcreteType.density

        if ((selectedForm == "Kjerne" && d1 == 0.0) || t == 0.0 ||
            (selectedForm == "Firkant" && (d1 == 0.0 || d2 == 0.0)) ||
            (selectedForm == "Trekant" && (d1 == 0.0 || d2 == 0.0 || d3 == 0.0)) ||
            (selectedForm == "Trapes" && (d1 == 0.0 || d2 == 0.0 || d3 == 0.0 || d4 == 0.0))
        ) {
            error = "Fyll inn nødvendige mål."
            return
        }

        result = when (selectedForm) {
            "Kjerne" -> Math.PI * (d1 / 2).pow(2) * t
            "Firkant" -> d1 * d2 * t
            "Trekant" -> 0.5 * d1 * d2 * t
            "Trapes" -> {
                val s = (d1 + d2 + d3 + d4) / 2
                val area = sqrt((s - d1) * (s - d2) * (s - d3) * (s - d4))
                area * t
            }
            else -> 0.0
        }.let { volume ->
            if (weightUnit == "lbs") volume * density * 2.20462 else volume * density
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
    }

    LaunchedEffect(selectedForm) { savePreference(context, "selected_form", selectedForm) }
    LaunchedEffect(selectedUnit) { savePreference(context, "selected_unit", selectedUnit) }
    LaunchedEffect(selectedConcreteType) { savePreference(context, "selected_concrete_type", selectedConcreteType.name) }

    LazyColumn(modifier = Modifier.padding(16.dp)) {
        item {
            AppDropdown("Form", forms, selectedForm) { selectedForm = it }
            AppDropdown("Enhet", units, selectedUnit) { selectedUnit = it }
            AppDropdown("Betongtype", concreteTypes.map { it.name }.sorted(), selectedConcreteType.name) {
                selectedConcreteType = concreteTypes.first { type -> type.name == it }
            }
            Text("Tetthet: ${selectedConcreteType.density} kg/m³", style = MaterialTheme.typography.bodySmall)
            if (selectedConcreteType.name == "Egendefinert") {
                DimensionField(concreteDensity, "Densitet", "kg/m³", f1, f2)
            }
        }
        item {
            when (selectedForm) {
                "Kjerne" -> {
                    DimensionField(dim1, "Diameter", selectedUnit, f2, f3)
                    DimensionField(thickness, "Tykkelse", selectedUnit, f3) { performCalculation() }
                }
                "Firkant" -> {
                    DimensionField(dim1, "Lengde", selectedUnit, f2, f3)
                    DimensionField(dim2, "Bredde", selectedUnit, f3, f4)
                    DimensionField(thickness, "Tykkelse", selectedUnit, f4) { performCalculation() }
                }
                "Trekant" -> {
                    DimensionField(dim1, "A-Side", selectedUnit, f2, f3)
                    DimensionField(dim2, "B-Side", selectedUnit, f3, f4)
                    DimensionField(dim3, "C-Side", selectedUnit, f4, f5)
                    DimensionField(thickness, "Tykkelse", selectedUnit, f5) { performCalculation() }
                }
                "Trapes" -> {
                    DimensionField(dim1, "A-Side", selectedUnit, f2, f3)
                    DimensionField(dim2, "B-Side", selectedUnit, f3, f4)
                    DimensionField(dim3, "C-Side", selectedUnit, f4, f5)
                    DimensionField(dim4, "D-Side", selectedUnit, f5, f6)
                    DimensionField(thickness, "Tykkelse", selectedUnit, f6) { performCalculation() }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { performCalculation() }) { Text("Regn ut") }
                Button(onClick = {
                    showNoteField = true
                }) { Text("Del") }
            }
            if (showNoteField) {
                OutlinedTextField(
                    value = note.value,
                    onValueChange = { note.value = it },
                    label = { Text("Valgfritt notat (f.eks. område, kunde, prosjekt)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = {
                    val dims = listOf(dim1.value.text, dim2.value.text, dim3.value.text, dim4.value.text).filter { it.isNotBlank() }
                    val thicknessText = thickness.value.text
                    val formattedResult = java.text.DecimalFormat("#,###.##").format(result)
                    val tonsFormatted = java.text.DecimalFormat("#,###.##").format(result / 1000)
                    val tons = if (weightUnit == "kg" && result >= 1000) " / $tonsFormatted t" else ""

                    val message = buildString {
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
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, message)
                    }
                    navContext.startActivity(Intent.createChooser(intent, "Del kalkulasjon via"))
                }) {
                    Text("Del kalkulasjon")
                }
            }
            if (result > 0.0) {
                val res = java.text.DecimalFormat("#,###.##").format(result)
                val tonsFormatted = java.text.DecimalFormat("#,###.##").format(result / 1000)
                val tons = if (weightUnit == "kg" && result >= 1000) " / $tonsFormatted t" else ""
                SelectionContainer {
                    Text("Resultat: $res $weightUnit$tons")
                }
            }
            if (error.isNotEmpty()) Text(error, color = MaterialTheme.colorScheme.error)
        }
    }
}
