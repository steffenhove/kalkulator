package no.steffenhove.betongkalkulator.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.*

@Composable
fun ConcreteTypeSelector(selectedType: String, onTypeSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val types = listOf("Betong", "Leca", "Siporex", "Egendefinert")

    Column {
        Text("Betongtype: $selectedType")
        Button(onClick = { expanded = true }) {
            Text("Velg Betongtype")
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            types.forEach { type ->
                DropdownMenuItem(
                    text = { Text(type) },
                    onClick = {
                        onTypeSelected(type)
                        expanded = false
                    }
                )
            }
        }
    }
}