@file:OptIn(ExperimentalMaterial3Api::class)

package no.steffenhove.betongkalkulator.ui.screens

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import no.steffenhove.betongkalkulator.ui.components.AppDropdown
import no.steffenhove.betongkalkulator.ui.components.AppScaffold
import no.steffenhove.betongkalkulator.ui.components.InputField
import no.steffenhove.betongkalkulator.ui.model.ConcreteType
import no.steffenhove.betongkalkulator.ui.utils.*

@Composable
fun SettingsScreen(context: Context = LocalContext.current) {
    val keyboardController = LocalSoftwareKeyboardController.current

    // Disse to skal IKKE nullstilles lenger
    var unitSystem by remember { mutableStateOf(getUnitSystemPreference(context)) }
    var weightUnit by remember { mutableStateOf(getWeightUnitPreference(context)) }

    // Standardverdier for tetthet
    val defaultDensities = mapOf(
        "Betong" to 2400.0,
        "Lettbetong" to 1800.0,
        "Leca" to 800.0,
        "Asfalt" to 2300.0,
        "Egendefinert" to 0.0
    )

    // Hent lagrede typer
    val initialTypes = getConcreteTypesPreference(context).let { stored ->
        var list = stored
        if (list.none { it.name == "Asfalt" }) {
            list = list + ConcreteType("Asfalt", 2300.0)
        }
        list.sortedBy { it.name }
    }

    var concreteTypes by remember { mutableStateOf(initialTypes) }
    var selectedConcreteType by remember { mutableStateOf(concreteTypes.first()) }

    val densityTfv = rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(selectedConcreteType.density.toString()))
    }

    val densityFocus = remember { FocusRequester() }

    AppScaffold(title = "Innstillinger") { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {

            // ENHETSSYSTEM
            Text("Velg enhetssystem:", style = MaterialTheme.typography.titleLarge)
            AppDropdown(
                label = "Enhetssystem",
                options = listOf("Metrisk", "Imperialsk"),
                selectedOption = unitSystem,
                onOptionSelected = {
                    unitSystem = it
                    saveUnitSystemPreference(context, it)
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // VEKTENHET
            Text("Velg vektenhet:", style = MaterialTheme.typography.titleLarge)
            AppDropdown(
                label = "Vektenhet",
                options = listOf("kg", "lbs"),
                selectedOption = weightUnit,
                onOptionSelected = {
                    weightUnit = it
                    saveWeightUnitPreference(context, it)
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // TETTHET
            Text("Endre betongtetthet:", style = MaterialTheme.typography.titleLarge)
            AppDropdown(
                label = "Betongtype",
                options = concreteTypes.map { it.name },
                selectedOption = selectedConcreteType.name,
                onOptionSelected = { name ->
                    selectedConcreteType = concreteTypes.first { it.name == name }
                    densityTfv.value = TextFieldValue(selectedConcreteType.density.toString())
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            InputField(
                label = "Tetthet",
                state = densityTfv,
                unit = "kg/m³",
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done,
                focus = densityFocus,
                onDone = {
                    val newDensity = densityTfv.value.text.replace(",", ".").toDoubleOrNull()
                    if (newDensity != null && newDensity > 0.0) {

                        val updatedTypes = concreteTypes.map { type ->
                            if (type.name == selectedConcreteType.name)
                                type.copy(density = newDensity)
                            else type
                        }.sortedBy { it.name }

                        concreteTypes = updatedTypes
                        selectedConcreteType =
                            updatedTypes.first { it.name == selectedConcreteType.name }

                        saveConcreteTypesPreference(context, updatedTypes)
                        keyboardController?.hide()
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // NULLSTILL KUN TETTHET
            Button(onClick = {
                val reset = concreteTypes.map { type ->
                    val default = defaultDensities[type.name] ?: type.density
                    type.copy(density = default)
                }.sortedBy { it.name }

                concreteTypes = reset
                selectedConcreteType = reset.first()
                densityTfv.value = TextFieldValue(selectedConcreteType.density.toString())

                saveConcreteTypesPreference(context, reset)

            }) {
                Text("Nullstill tetthet")
            }
        }
    }
}
