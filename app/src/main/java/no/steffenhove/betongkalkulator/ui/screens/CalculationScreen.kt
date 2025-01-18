package no.steffenhove.betongkalkulator.ui.screens

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.toSize
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import kotlinx.coroutines.launch
import no.steffenhove.betongkalkulator.ui.model.*
import no.steffenhove.betongkalkulator.ui.utils.getUnitSystemPreference
import java.text.DecimalFormat

@Composable
fun CalculationScreen(unitSystem: String, context: Context) {
    val metricUnits = listOf("mm", "cm", "m")
    val imperialUnits = listOf("inch", "foot")
    val units = if (unitSystem == "Metrisk") metricUnits else imperialUnits

    val forms = listOf("Kjerne", "Firkant", "Trekant", "Trapes")
    var selectedForm by remember { mutableStateOf(forms[0]) }
    var formExpanded by remember { mutableStateOf(false) }

    var selectedConcreteType by remember { mutableStateOf(concreteTypes[0]) }
    var concreteTypeExpanded by remember { mutableStateOf(false) }

    var selectedUnit by remember { mutableStateOf(units[0]) }
    var unitExpanded by remember { mutableStateOf(false) }

    var dimension1 by remember { mutableStateOf(TextFieldValue("")) }
    var dimension2 by remember { mutableStateOf(TextFieldValue("")) }
    var dimension3 by remember { mutableStateOf(TextFieldValue("")) }
    var thickness by remember { mutableStateOf(TextFieldValue("")) }
    var customDensity by remember { mutableStateOf(TextFieldValue("")) }
    var result by remember { mutableStateOf(0.0) }

    val scope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()
    ) {
        Text(text = "Velg form:", style = MaterialTheme.typography.titleLarge)
        Box {
            TextButton(onClick = { formExpanded = !formExpanded }) {
                Text(text = selectedForm)
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

        Text(text = "Velg enhet for inntasting:", style = MaterialTheme.typography.titleLarge)
        Box {
            TextButton(onClick = { unitExpanded = !unitExpanded }) {
                Text(text = selectedUnit)
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

        Text(text = "Velg betongtype:", style = MaterialTheme.typography.titleLarge)
        Box {
            TextButton(onClick = { concreteTypeExpanded = !concreteTypeExpanded }) {
                Text(text = selectedConcreteType.name)
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

        if (selectedConcreteType.name == "Egendefinert") {
            OutlinedTextField(
                value = customDensity,
                onValueChange = { customDensity = it },
                label = { Text("Egendefinert densitet (kg/m³)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Fyll inn mål:", style = MaterialTheme.typography.titleLarge)

        when (selectedForm) {
            "Kjerne" -> {
                OutlinedTextField(
                    value = dimension1,
                    onValueChange = { dimension1 = it },
                    label = { Text("Diameter (${selectedUnit})") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
                )
                OutlinedTextField(
                    value = thickness,
                    onValueChange = { thickness = it },
                    label = { Text("Tykkelse (${selectedUnit})") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
                )
            }
            "Firkant" -> {
                OutlinedTextField(
                    value = dimension1,
                    onValueChange = { dimension1 = it },
                    label = { Text("Lengde (${selectedUnit})") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
                )
                OutlinedTextField(
                    value = dimension2,
                    onValueChange = { dimension2 = it },
                    label = { Text("Bredde (${selectedUnit})") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
                )
                OutlinedTextField(
                    value = thickness,
                    onValueChange = { thickness = it },
                    label = { Text("Tykkelse (${selectedUnit})") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
                )
            }
            "Trekant" -> {
                OutlinedTextField(
                    value = dimension1,
                    onValueChange = { dimension1 = it },
                    label = { Text("A-side (${selectedUnit})") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
                )
                OutlinedTextField(
                    value = dimension2,
                    onValueChange = { dimension2 = it },
                    label = { Text("B-side (${selectedUnit})") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
                )
                OutlinedTextField(
                    value = dimension3,
                    onValueChange = { dimension3 = it },
                    label = { Text("C-side (${selectedUnit})") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
                )
                OutlinedTextField(
                    value = thickness,
                    onValueChange = { thickness = it },
                    label = { Text("Tykkelse (${selectedUnit})") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
                )
            }
            "Trapes" -> {
                OutlinedTextField(
                    value = dimension1,
                    onValueChange = { dimension1 = it },
                    label = { Text("Kort base (${selectedUnit})") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
                )
                OutlinedTextField(
                    value = dimension2,
                    onValueChange = { dimension2 = it },
                    label = { Text("Lang base (${selectedUnit})") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
                )
                OutlinedTextField(
                    value = dimension3,
                    onValueChange = { dimension3 = it },
                    label = { Text("Høyde (${selectedUnit})") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
                )
                OutlinedTextField(
                    value = thickness,
                    onValueChange = { thickness = it },
                    label = { Text("Tykkelse (${selectedUnit})") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            val density = if (selectedConcreteType.name == "Egendefinert") customDensity.text.toDoubleOrNull() ?: 0.0 else selectedConcreteType.density
            result = calculate(selectedForm, selectedUnit, dimension1.text, dimension2.text, dimension3.text, thickness.text, density)

            scope.launch {
                val calculation = CalculationEntity(
                    form = selectedForm,
                    unit = selectedUnit,
                    concreteType = selectedConcreteType.name,
                    dimensions = "${dimension1.text}, ${dimension2.text}, ${dimension3.text}",
                    thickness = thickness.text,
                    density = density,
                    result = result
                )
                AppDatabase.getDatabase(context).calculationDao().insert(calculation)
            }
            keyboardController?.hide()
        }) {
            Text(text = "Regn ut")
        }

        Spacer(modifier = Modifier.height(16.dp))

        val formattedResult = DecimalFormat("#.##").format(result)
        val tonResult = if (result >= 1000) DecimalFormat("#.##").format(result / 1000) else null

        Text(text = "Resultat: $formattedResult kg", style = MaterialTheme.typography.bodyLarge)
        tonResult?.let {
            Text(text = "Tonn: $it t", style = MaterialTheme.typography.bodyLarge)
        }
    }
}

fun calculate(selectedForm: String, selectedUnit: String, dimension1: String, dimension2: String?, dimension3: String?, thickness: String, density: Double): Double {
    // Konverter til meter hvis enhetene er i mm eller cm
    val dim1 = convertToMeters(dimension1.toDoubleOrNull() ?: return 0.0, selectedUnit)
    val dim2 = convertToMeters(dimension2?.toDoubleOrNull() ?: 0.0, selectedUnit)
    val dim3 = convertToMeters(dimension3?.toDoubleOrNull() ?: 0.0, selectedUnit)
    val thick = convertToMeters(thickness.toDoubleOrNull() ?: return 0.0, selectedUnit)

    val volume = when (selectedForm) {
        "Kjerne" -> Math.PI * Math.pow(dim1 / 2, 2.0) * thick
        "Firkant" -> dim1 * dim2 * thick
        "Trekant" -> 0.5 * dim1 * dim2 * thick
        "Trapes" -> 0.5 * (dim1 + dim2) * dim3 * thick
        else -> 0.0
    }

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