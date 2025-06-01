@file:OptIn(ExperimentalMaterial3Api::class)

package no.steffenhove.betongkalkulator.ui.screens

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import no.steffenhove.betongkalkulator.ui.model.ConcreteType
import no.steffenhove.betongkalkulator.ui.utils.getConcreteTypesPreference
import no.steffenhove.betongkalkulator.ui.utils.getUnitSystemPreference
import no.steffenhove.betongkalkulator.ui.utils.getWeightUnitPreference
import no.steffenhove.betongkalkulator.ui.utils.resetToDefaultPreferences
import no.steffenhove.betongkalkulator.ui.utils.saveConcreteTypesPreference
import no.steffenhove.betongkalkulator.ui.utils.saveUnitSystemPreference
import no.steffenhove.betongkalkulator.ui.utils.saveWeightUnitPreference

@Composable
fun SettingsScreen(context: Context) {
    var unitSystem by remember { mutableStateOf(getUnitSystemPreference(context)) }
    var weightUnit by remember { mutableStateOf(getWeightUnitPreference(context)) }

    val defaultConcreteTypes = getConcreteTypesPreference(context).let {
        if (it.none { t -> t.name == "Asfalt" }) it + ConcreteType("Asfalt", 2300.0) else it
    }

    var concreteTypes by remember { mutableStateOf(defaultConcreteTypes.sortedBy { it.name }) }
    var selectedConcreteType by remember { mutableStateOf(concreteTypes[0]) }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Velg enhetssystem:", style = MaterialTheme.typography.titleLarge)
        SettingsDropdown(
            label = "Enhetssystem",
            options = listOf("Metrisk", "Imperialsk"),
            selected = unitSystem
        ) { selectedValue ->
            unitSystem = selectedValue
            saveUnitSystemPreference(context, selectedValue)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Velg vektenhet:", style = MaterialTheme.typography.titleLarge)
        SettingsDropdown(
            label = "Vektenhet",
            options = listOf("kg", "lbs"),
            selected = weightUnit
        ) { selectedValue ->
            weightUnit = selectedValue
            saveWeightUnitPreference(context, selectedValue)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Endre betongtetthet:", style = MaterialTheme.typography.titleLarge)
        SettingsDropdown(
            label = "Betongtype",
            options = concreteTypes.map { it.name },
            selected = selectedConcreteType.name
        ) { selectedTypeName ->
            selectedConcreteType = concreteTypes.first { it.name == selectedTypeName }
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = selectedConcreteType.density.toString(),
            onValueChange = { newDensityStr ->
                val newDensity = newDensityStr.toDoubleOrNull()
                if (newDensity != null) {
                    val updatedTypes = concreteTypes.map { type ->
                        if (type.name == selectedConcreteType.name) type.copy(density = newDensity) else type
                    }
                    concreteTypes = updatedTypes.sortedBy { it.name }
                    selectedConcreteType = updatedTypes.first { it.name == selectedConcreteType.name }
                    saveConcreteTypesPreference(context, updatedTypes)
                }
            },
            label = { Text("Tetthet (kg/m³)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            resetToDefaultPreferences(context)
            unitSystem = getUnitSystemPreference(context)
            weightUnit = getWeightUnitPreference(context)
            concreteTypes = getConcreteTypesPreference(context)
            selectedConcreteType = concreteTypes[0]
        }) {
            Text("Nullstill til standardverdier")
        }
    }
}

@Composable
fun SettingsDropdown(
    label: String,
    options: List<String>,
    selected: String,
    onSelect: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text(label)
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true }
                .padding(8.dp),
            tonalElevation = 1.dp,
            shape = MaterialTheme.shapes.small
        ) {
            Text(text = selected, modifier = Modifier.padding(8.dp))
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(text = { Text(option) }, onClick = {
                    onSelect(option)
                    expanded = false
                })
            }
        }
    }
}
