package no.steffenhove.betongkalkulator.ui.screens

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import no.steffenhove.betongkalkulator.ui.model.*
import no.steffenhove.betongkalkulator.ui.utils.*
import java.text.DecimalFormat

@Composable
fun CalculationScreen(context: Context) {
    // Hent enhets- og vektpreferanser
    val unitSystem = getUnitSystemPreference(context)
    val weightUnit = getWeightUnitPreference(context)
    val metricUnits = listOf("mm", "cm", "m")
    val units = if (unitSystem == "Metrisk") metricUnits else listOf("inch", "foot")

    // Definer tilgjengelige former
    val forms = listOf("Kjerne", "Firkant", "Trekant", "Trapes")
    var selectedForm by remember { mutableStateOf(forms[0]) }
    var formExpanded by remember { mutableStateOf(false) }

    // Hent betongtyper
    var concreteTypes = getConcreteTypesPreference(context) + listOf(ConcreteType("Egendefinert", 0.0))
    var selectedConcreteType by remember { mutableStateOf(concreteTypes[0]) }
    var concreteTypeExpanded by remember { mutableStateOf(false) }

    // Velg enhet for inntasting
    var selectedUnit by remember { mutableStateOf(units[0]) }
    var unitExpanded by remember { mutableStateOf(false) }

    // Definer tilstandsvariabler for dimensjoner, tykkelse og densitet
    var dimension1 by remember { mutableStateOf(TextFieldValue("")) }
    var dimension2 by remember { mutableStateOf(TextFieldValue("")) }
    var dimension3 by remember { mutableStateOf(TextFieldValue("")) }
    var dimension4 by remember { mutableStateOf(TextFieldValue("")) }
    var thickness by remember { mutableStateOf(TextFieldValue("")) }
    var customDensity by remember { mutableStateOf(TextFieldValue("")) }
    var result by remember { mutableStateOf(0.0) }
    var errorMessage by remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current

    // Fokusrekke for inntastingsfeltene
    val focusRequester1 = remember { FocusRequester() }
    val focusRequester2 = remember { FocusRequester() }
    val focusRequester3 = remember { FocusRequester() }
    val focusRequester4 = remember { FocusRequester() }
    val focusRequester5 = remember { FocusRequester() }
    val focusRequester6 = remember { FocusRequester() }

    fun performCalculation() {
        // Hent og konverter dimensjoner og tykkelse
        val dim1 = dimension1.text.replace(",", ".").replace("[^\\d.]".toRegex(), "").toDoubleOrNull() ?: 0.0
        val dim2 = dimension2.text.replace(",", ".").replace("[^\\d.]".toRegex(), "").toDoubleOrNull() ?: 0.0
        val dim3 = dimension3.text.replace(",", ".").replace("[^\\d.]".toRegex(), "").toDoubleOrNull() ?: 0.0
        val dim4 = dimension4.text.replace(",", ".").replace("[^\\d.]".toRegex(), "").toDoubleOrNull() ?: 0.0
        val thick = thickness.text.replace(",", ".").replace("[^\\d.]".toRegex(), "").toDoubleOrNull() ?: 0.0

        // Sjekk om nødvendige mål er fylt inn
        if (dim1 == 0.0 || thick == 0.0 ||
            (selectedForm == "Firkant" && dim2 == 0.0) ||
            (selectedForm == "Trekant" && (dim2 == 0.0 || dim3 == 0.0)) ||
            (selectedForm == "Trapes" && (dim2 == 0.0 || dim3 == 0.0 || dim4 == 0.0))
        ) {
            errorMessage = "Vennligst fyll inn alle nødvendige mål."
        } else {
            errorMessage = ""
            val density = if (selectedConcreteType.name == "Egendefinert") customDensity.text.replace(",", ".").toDoubleOrNull() ?: 0.0 else selectedConcreteType.density

            // Utfør beregningen
            result = calculate(
                selectedForm,
                selectedUnit,
                dim1,
                dim2,
                dim3,
                dim4,
                thick,
                density
            )

            // Konverter resultatet til lbs hvis nødvendig
            val finalResult = if (weightUnit == "lbs") {
                result * 2.20462  // Konverter kg til lbs
            } else {
                result
            }

            // Formater resultatet til maks 2 desimaler før lagring i historikken
            val formattedResult = DecimalFormat("#.##").format(finalResult).replace(",", ".").toDouble()

            // Lagre beregningen i historikken
            scope.launch {
                val calculation = CalculationEntity(
                    form = selectedForm,
                    unit = selectedUnit,
                    concreteType = selectedConcreteType.name,
                    dimensions = "$dim1, $dim2, $dim3, $dim4",
                    thickness = thick.toString(),
                    density = density,
                    result = formattedResult,
                    resultUnit = weightUnit
                )
                AppDatabase.getDatabase(context).calculationDao().insert(calculation)
            }
        }
    }

    LazyColumn(
        modifier = Modifier.padding(16.dp).fillMaxSize()
    ) {
        item {
            // Velg form
            Text(text = "Velg form:", style = MaterialTheme.typography.titleLarge)
            Box {
                TextButton(onClick = { formExpanded = !formExpanded }) {
                    Text(selectedForm)
                }
                DropdownMenu(
                    expanded = formExpanded,
                    onDismissRequest = { formExpanded = false }
                ) {
                    forms.forEach { form ->
                        DropdownMenuItem(
                            text = { Text(text = form) },
                            onClick = {
                                selectedForm = form
                                formExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Velg enhet for inntasting
            Text(text = "Velg enhet for inntasting:", style = MaterialTheme.typography.titleLarge)
            Box {
                TextButton(onClick = { unitExpanded = !unitExpanded }) {
                    Text(selectedUnit)
                }
                DropdownMenu(
                    expanded = unitExpanded,
                    onDismissRequest = { unitExpanded = false }
                ) {
                    units.forEach { unit ->
                        DropdownMenuItem(
                            text = { Text(text = unit) },
                            onClick = {
                                selectedUnit = unit
                                unitExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Velg betongtype
            Text(text = "Velg betongtype:", style = MaterialTheme.typography.titleLarge)
            Box {
                TextButton(onClick = { concreteTypeExpanded = !concreteTypeExpanded }) {
                    Text(selectedConcreteType.name)
                }
                DropdownMenu(
                    expanded = concreteTypeExpanded,
                    onDismissRequest = { concreteTypeExpanded = false }
                ) {
                    concreteTypes.forEach { type ->
                        DropdownMenuItem(
                            text = { Text(text = type.name) },
                            onClick = {
                                selectedConcreteType = type
                                concreteTypeExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Spesifiser egendefinert densitet hvis valgt
            if (selectedConcreteType.name == "Egendefinert") {
                OutlinedTextField(
                    value = customDensity,
                    onValueChange = { customDensity = it },
                    label = { Text("Egendefinert densitet (kg/m³)") },
                    modifier = Modifier.fillMaxWidth().focusRequester(focusRequester1),
                    textStyle = LocalTextStyle.current.copy(color = LocalContentColor.current),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { focusRequester2.requestFocus() }
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Fyll inn mål
            Text(text = "Fyll inn mål:", style = MaterialTheme.typography.titleLarge)
        }

        item {
            when (selectedForm) {
                "Kjerne" -> {
                    OutlinedTextField(
                        value = dimension1,
                        onValueChange = { dimension1 = it },
                        label = { Text("Diameter (${selectedUnit})") },
                        modifier = Modifier.fillMaxWidth().focusRequester(focusRequester2),
                        textStyle = LocalTextStyle.current.copy(color = LocalContentColor.current),
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusRequester3.requestFocus() }
                        )
                    )
                    OutlinedTextField(
                        value = thickness,
                        onValueChange = { thickness = it },
                        label = { Text("Tykkelse (${selectedUnit})") },
                        modifier = Modifier.fillMaxWidth().focusRequester(focusRequester3),
                        textStyle = LocalTextStyle.current.copy(color = LocalContentColor.current),
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                keyboardController?.hide()
                                performCalculation()
                            }
                        )
                    )
                }
                "Firkant" -> {
                    OutlinedTextField(
                        value = dimension1,
                        onValueChange = { dimension1 = it },
                        label = { Text("Lengde (${selectedUnit})") },
                        modifier = Modifier.fillMaxWidth().focusRequester(focusRequester2),
                        textStyle = LocalTextStyle.current.copy(color = LocalContentColor.current),
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusRequester3.requestFocus() }
                        )
                    )
                    OutlinedTextField(
                        value = dimension2,
                        onValueChange = { dimension2 = it },
                        label = { Text("Bredde (${selectedUnit})") },
                        modifier = Modifier.fillMaxWidth().focusRequester(focusRequester3),
                        textStyle = LocalTextStyle.current.copy(color = LocalContentColor.current),
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusRequester4.requestFocus() }
                        )
                    )
                    OutlinedTextField(
                        value = thickness,
                        onValueChange = { thickness = it },
                        label = { Text("Tykkelse (${selectedUnit})") },
                        modifier = Modifier.fillMaxWidth().focusRequester(focusRequester4),
                        textStyle = LocalTextStyle.current.copy(color = LocalContentColor.current),
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                keyboardController?.hide()
                                performCalculation()
                            }
                        )
                    )
                }
                "Trekant" -> {
                    OutlinedTextField(
                        value = dimension1,
                        onValueChange = { dimension1 = it },
                        label = { Text("A-Side (${selectedUnit})") },
                        modifier = Modifier.fillMaxWidth().focusRequester(focusRequester2),
                        textStyle = LocalTextStyle.current.copy(color = LocalContentColor.current),
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusRequester3.requestFocus() }
                        )
                    )
                    OutlinedTextField(
                        value = dimension2,
                        onValueChange = { dimension2 = it },
                        label = { Text("B-Side (${selectedUnit})") },
                        modifier = Modifier.fillMaxWidth().focusRequester(focusRequester3),
                        textStyle = LocalTextStyle.current.copy(color = LocalContentColor.current),
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusRequester4.requestFocus() }
                        )
                    )
                    OutlinedTextField(
                        value = dimension3,
                        onValueChange = { dimension3 = it },
                        label = { Text("C-Side (${selectedUnit})") },
                        modifier = Modifier.fillMaxWidth().focusRequester(focusRequester4),
                        textStyle = LocalTextStyle.current.copy(color = LocalContentColor.current),
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusRequester5.requestFocus() }
                        )
                    )
                    OutlinedTextField(
                        value = thickness,
                        onValueChange = { thickness = it },
                        label = { Text("Tykkelse (${selectedUnit})") },
                        modifier = Modifier.fillMaxWidth().focusRequester(focusRequester5),
                        textStyle = LocalTextStyle.current.copy(color = LocalContentColor.current),
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                keyboardController?.hide()
                                performCalculation()
                            }
                        )
                    )
                }
                "Trapes" -> {
                    OutlinedTextField(
                        value = dimension1,
                        onValueChange = { dimension1 = it },
                        label = { Text("A-Side (${selectedUnit})") },
                        modifier = Modifier.fillMaxWidth().focusRequester(focusRequester2),
                        textStyle = LocalTextStyle.current.copy(color = LocalContentColor.current),
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusRequester3.requestFocus() }
                        )
                    )
                    OutlinedTextField(
                        value = dimension2,
                        onValueChange = { dimension2 = it },
                        label = { Text("B-Side (${selectedUnit})") },
                        modifier = Modifier.fillMaxWidth().focusRequester(focusRequester3),
                        textStyle = LocalTextStyle.current.copy(color = LocalContentColor.current),
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusRequester4.requestFocus() }
                        )
                    )
                    OutlinedTextField(
                        value = dimension3,
                        onValueChange = { dimension3 = it },
                        label = { Text("C-Side (${selectedUnit})") },
                        modifier = Modifier.fillMaxWidth().focusRequester(focusRequester4),
                        textStyle = LocalTextStyle.current.copy(color = LocalContentColor.current),
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusRequester5.requestFocus() }
                        )
                    )
                    OutlinedTextField(
                        value = dimension4,
                        onValueChange = { dimension4 = it },
                        label = { Text("D-Side (${selectedUnit})") },
                        modifier = Modifier.fillMaxWidth().focusRequester(focusRequester5),
                        textStyle = LocalTextStyle.current.copy(color = LocalContentColor.current),
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusRequester6.requestFocus() }
                        )
                    )
                    OutlinedTextField(
                        value = thickness,
                        onValueChange = { thickness = it },
                        label = { Text("Tykkelse (${selectedUnit})") },
                        modifier = Modifier.fillMaxWidth().focusRequester(focusRequester6),
                        textStyle = LocalTextStyle.current.copy(color = LocalContentColor.current),
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                keyboardController?.hide()
                                performCalculation()
                            }
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (errorMessage.isNotEmpty()) {
                Text(text = errorMessage, color = MaterialTheme.colorScheme.error)
                Spacer(modifier = Modifier.height(8.dp))
            }

            Button(onClick = {
                performCalculation()
            }) {
                Text(text = "Regn ut")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Formater resultatet til maks 2 desimaler for visning
            val displayResult = DecimalFormat("#.##").format(result)
            val tonResult = if (result >= 1000) DecimalFormat("#.##").format(result / 1000) else null

            // Konverter resultatet til lbs hvis nødvendig
            val displayResultWithUnit = if (weightUnit == "lbs") {
                val resultInLbs = result * 2.20462  // Konverter kg til lbs
                DecimalFormat("#.##").format(resultInLbs) + " lbs"
            } else {
                displayResult + " kg"
            }

            val tonResultWithUnit = tonResult?.let {
                if (weightUnit == "lbs") {
                    val tonResultInLbs = it.toDouble() * 2204.62  // Konverter tonn til lbs
                    DecimalFormat("#.##").format(tonResultInLbs) + " lbs"
                } else {
                    it + " t"
                }
            }

            Text(text = "Resultat: $displayResultWithUnit", style = MaterialTheme.typography.bodyLarge)
            tonResultWithUnit?.let {
                Text(text = "Tonn: $it", style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}

fun calculate(
    selectedForm: String,
    selectedUnit: String,
    dimension1: Double,
    dimension2: Double,
    dimension3: Double,
    dimension4: Double,
    thickness: Double,
    density: Double
): Double {
    // Konverter dimensjoner til meter
    val dim1 = convertToMeters(dimension1, selectedUnit)
    val dim2 = convertToMeters(dimension2, selectedUnit)
    val dim3 = convertToMeters(dimension3, selectedUnit)
    val dim4 = convertToMeters(dimension4, selectedUnit)
    val thick = convertToMeters(thickness, selectedUnit)

    // Beregn volum basert på valgt form
    val volume = when (selectedForm) {
        "Kjerne" -> Math.PI * Math.pow(dim1 / 2, 2.0) * thick
        "Firkant" -> dim1 * dim2 * thick
        "Trekant" -> 0.5 * dim1 * dim2 * thick
        "Trapes" -> {
            val semiPerimeter = (dim1 + dim2 + dim3 + dim4) / 2
            val area = Math.sqrt((semiPerimeter - dim1) * (semiPerimeter - dim2) * (semiPerimeter - dim3) * (semiPerimeter - dim4))
            area * thick
        }
        else -> 0.0
    }

    // Returner vekten
    return volume * density
}

fun convertToMeters(value: Double, unit: String): Double {
    return when (unit) {
        "mm" -> value / 1000
        "cm" -> value / 100
        "m" -> value
        "inch" -> value * 0.0254
        "foot" -> value * 0.3048
        else -> value
    }
}

fun formatWeight(weight: Double, unit: String): String {
    val decimalFormat = DecimalFormat("#,###.##")
    val kg = decimalFormat.format(weight)

    return if (unit == "lbs") {
        val lbs = decimalFormat.format(weight * 2.20462)
        "$lbs lbs"
    } else {
        if (weight >= 1000) {
            val tons = decimalFormat.format(weight / 1000)
            "$kg kg / $tons t"
        } else {
            "$kg kg"
        }
    }
}