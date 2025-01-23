package no.steffenhove.betongkalkulator.ui.screens

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import no.steffenhove.betongkalkulator.ui.utils.*

@Composable
fun SettingsScreen(context: Context) {
    var unitSystem by remember { mutableStateOf(getUnitSystemPreference(context)) }
    var unitSystemExpanded by remember { mutableStateOf(false) }

    var weightUnit by remember { mutableStateOf(getWeightUnitPreference(context)) }
    var weightUnitExpanded by remember { mutableStateOf(false) }

    var concreteTypes by remember { mutableStateOf(getConcreteTypesPreference(context)) }
    var selectedConcreteType by remember { mutableStateOf(concreteTypes[0]) }
    var concreteTypeExpanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.padding(16.dp)) {
        // Enhetssystem dropdown
        Text(text = "Velg enhetssystem:", style = MaterialTheme.typography.titleLarge)
        Box {
            TextButton(onClick = { unitSystemExpanded = !unitSystemExpanded }) {
                Text(text = unitSystem)
            }
            DropdownMenu(
                expanded = unitSystemExpanded,
                onDismissRequest = { unitSystemExpanded = false }
            ) {
                listOf("Metrisk", "Imperialsk").forEach { system ->
                    DropdownMenuItem(
                        text = { Text(text = system) },
                        onClick = {
                            unitSystem = system
                            saveUnitSystemPreference(context, system)
                            unitSystemExpanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Vektenhet dropdown
        Text(text = "Velg vekt enhet:", style = MaterialTheme.typography.titleLarge)
        Box {
            TextButton(onClick = { weightUnitExpanded = !weightUnitExpanded }) {
                Text(text = weightUnit)
            }
            DropdownMenu(
                expanded = weightUnitExpanded,
                onDismissRequest = { weightUnitExpanded = false }
            ) {
                listOf("kg", "lbs").forEach { unit ->
                    DropdownMenuItem(
                        text = { Text(text = unit) },
                        onClick = {
                            weightUnit = unit
                            saveWeightUnitPreference(context, unit)
                            weightUnitExpanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Betongtetthet dropdown
        Text(text = "Endre betongtetthet:", style = MaterialTheme.typography.titleLarge)
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

        Spacer(modifier = Modifier.height(16.dp))

        // Tetthet input felt
        OutlinedTextField(
            value = selectedConcreteType.density.toString(),
            onValueChange = {
                val newDensity = it.toDoubleOrNull()
                if (newDensity != null) {
                    val updatedConcreteTypes = concreteTypes.map { type ->
                        if (type.name == selectedConcreteType.name) {
                            type.copy(density = newDensity)
                        } else {
                            type
                        }
                    }
                    concreteTypes = updatedConcreteTypes
                    selectedConcreteType = selectedConcreteType.copy(density = newDensity)
                    saveConcreteTypesPreference(context, updatedConcreteTypes)
                }
            },
            label = { Text("Tetthet (kg/m³)") },
            textStyle = LocalTextStyle.current.copy(color = LocalContentColor.current),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Nullstill til standardverdier knapp
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