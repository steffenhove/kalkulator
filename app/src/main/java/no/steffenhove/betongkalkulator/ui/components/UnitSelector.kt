package no.steffenhove.betongkalkulator.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.*

@Composable
fun UnitSelector(selectedUnit: String, onUnitSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val units = listOf("mm", "cm", "m", "fot", "tommer")

    Column {
        Text("Enhet: $selectedUnit")
        Button(onClick = { expanded = true }) {
            Text("Velg enhet")
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            units.forEach { unit ->
                DropdownMenuItem(
                    text = { Text(unit) },
                    onClick = {
                        onUnitSelected(unit)
                        expanded = false
                    }
                )
            }
        }
    }
}