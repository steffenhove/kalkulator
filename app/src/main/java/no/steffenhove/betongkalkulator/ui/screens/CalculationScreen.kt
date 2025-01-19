package no.steffenhove.betongkalkulator.ui.screens

import android.content.Context
import android.util.Log
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
    var dimension4 by remember { mutableStateOf(TextFieldValue("")) }
    var thickness by remember { mutableStateOf(TextFieldValue("")) }
    var customDensity by remember { mutableStateOf(TextFieldValue("")) }
    var result by remember { mutableStateOf(0.0) }
    var errorMessage by remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current

    val focusRequester1 = remember { FocusRequester() }
    val focusRequester2 = remember { FocusRequester() }
    val focusRequester3 = remember { FocusRequester() }
    val focusRequester4 = remember { FocusRequester() }
    val focusRequester5 = remember { FocusRequester() }
    val focusRequester6 = remember { FocusRequester() }

    fun performCalculation() {
        val dim1 = dimension1.text.replace("[^\\d.]".toRegex(), "")
        val dim2 = dimension2.text.replace("[^\\d.]".toRegex(), "")
        val dim3 = dimension3.text.replace("[^\\d.]".toRegex(), "")
        val dim4 = dimension4.text.replace("[^\\d.]".toRegex(), "")
        val thick = thickness.text.replace("[^\\d.]".toRegex(), "")

        if (dim1.isEmpty() || thick.isEmpty() ||
            (selectedForm == "Firkant" && dim2.isEmpty()) ||
            (selectedForm == "Trekant" && (dim2.isEmpty() || dim3.isEmpty())) ||
            (selectedForm == "Trapes" && (dim2.isEmpty() || dim3.isEmpty() || dim4.isEmpty()))
        ) {
            errorMessage = "Vennligst fyll inn alle nødvendige mål."
        } else {
            errorMessage = ""
            val density = if (selectedConcreteType.name == "Egendefinert") customDensity.text.toDoubleOrNull() ?: 0.0 else selectedConcreteType.density

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

            // Formater resultatet til maks 2 desimaler før lagring i historikken
            val formattedResult = DecimalFormat("#.##").format(result).replace(",", ".").toDouble()

            scope.launch {
                val calculation = CalculationEntity(
                    form = selectedForm,
                    unit = selectedUnit,
                    concreteType = selectedConcreteType.name,
                    dimensions = "$dim1, $dim2, $dim3, $dim4",
                    thickness = thick,
                    density = density,
                    result = formattedResult
                )
                AppDatabase.getDatabase(context).calculationDao().insert(calculation)
            }
        }
    }

    LazyColumn(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()
    ) {
        item {
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester1),
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

            Text(text = "Fyll inn mål:", style = MaterialTheme.typography.titleLarge)
        }

        item {
            when (selectedForm) {
                "Kjerne" -> {
                    OutlinedTextField(
                        value = dimension1,
                        onValueChange = { dimension1 = it },
                        label = { Text("Diameter (${selectedUnit})") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequester2),
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
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequester3),
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
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequester2),
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
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequester3),
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
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequester4),
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
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequester2),
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
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequester3),
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
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequester4),
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
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequester5),
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
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequester2),
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
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequester3),
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
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequester4),
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
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequester5),
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
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequester6),
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

            Text(text = "Resultat: $displayResult kg", style = MaterialTheme.typography.bodyLarge)
            tonResult?.let {
                Text(text = "Tonn: $it t", style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}

fun calculate(selectedForm: String, selectedUnit: String, dimension1: String, dimension2: String?, dimension3: String?, dimension4: String?, thickness: String, density: Double): Double {
    val dim1 = convertToMeters(dimension1, selectedUnit)
    val dim2 = convertToMeters(dimension2 ?: "0", selectedUnit)
    val dim3 = convertToMeters(dimension3 ?: "0", selectedUnit)
    val dim4 = convertToMeters(dimension4 ?: "0", selectedUnit)
    val thick = convertToMeters(thickness, selectedUnit)

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

    return volume * density
}

fun convertToMeters(value: String, unit: String): Double {
    val doubleValue = value.toDoubleOrNull() ?: return 0.0
    return when (unit) {
        "mm" -> doubleValue / 1000
        "cm" -> doubleValue / 100
        "m" -> doubleValue
        "inch" -> doubleValue * 0.0254
        "foot" -> doubleValue * 0.3048
        else -> doubleValue
    }
}