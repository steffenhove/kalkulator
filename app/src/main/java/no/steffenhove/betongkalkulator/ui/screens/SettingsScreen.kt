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
import no.steffenhove.betongkalkulator.ui.components.InputField
import no.steffenhove.betongkalkulator.ui.model.ConcreteType
import no.steffenhove.betongkalkulator.ui.utils.*


@Composable
fun SettingsScreen(context: Context = LocalContext.current) {
    val keyboardController = LocalSoftwareKeyboardController.current

    var unitSystem by remember { mutableStateOf(getUnitSystemPreference(context)) }
    var weightUnit by remember { mutableStateOf(getWeightUnitPreference(context)) }

    val defaultConcreteTypes = getConcreteTypesPreference(context).let {
        if (it.none { t -> t.name == "Asfalt" }) it + ConcreteType("Asfalt", 2300.0) else it
    }

    var concreteTypes by remember { mutableStateOf(defaultConcreteTypes.sortedBy { it.name }) }
    var selectedConcreteType by remember { mutableStateOf(concreteTypes[0]) }

    val densityTfv = rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(selectedConcreteType.density.toString()))
    }

    val densityFocus = remember { FocusRequester() }

    Column(modifier = Modifier.padding(16.dp)) {
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

        Text("Endre betongtetthet:", style = MaterialTheme.typography.titleLarge)
        AppDropdown(
            label = "Betongtype",
            options = concreteTypes.map { it.name },
            selectedOption = selectedConcreteType.name,
            onOptionSelected = {
                selectedConcreteType = concreteTypes.first { type -> type.name == it }
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
                    }
                    concreteTypes = updatedTypes.sortedBy { it.name }
                    selectedConcreteType = updatedTypes.first { it.name == selectedConcreteType.name }
                    saveConcreteTypesPreference(context, updatedTypes)
                    keyboardController?.hide()
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            resetToDefaultPreferences(context)
            unitSystem = getUnitSystemPreference(context)
            weightUnit = getWeightUnitPreference(context)
            concreteTypes = getConcreteTypesPreference(context).sortedBy { it.name }
            selectedConcreteType = concreteTypes[0]
            densityTfv.value = TextFieldValue(selectedConcreteType.density.toString())
        }) {
            Text("Nullstill til standardverdier")
        }
    }
}
