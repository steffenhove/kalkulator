package no.steffenhove.betongkalkulator.ui.screens

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import no.steffenhove.betongkalkulator.ui.utils.getUnitSystemPreference
import no.steffenhove.betongkalkulator.ui.utils.saveUnitSystemPreference

@Composable
fun SettingsScreen(context: Context) {
    var unitSystem by remember { mutableStateOf(getUnitSystemPreference(context)) }
    var unitSystemExpanded by remember { mutableStateOf(false) }

    var weightUnit by remember { mutableStateOf("kg") }
    var weightUnitExpanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.padding(16.dp)) {
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
                            weightUnitExpanded = false
                        }
                    )
                }
            }
        }
    }
}