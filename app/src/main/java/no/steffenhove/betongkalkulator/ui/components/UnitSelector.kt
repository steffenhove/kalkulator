package no.steffenhove.betongkalkulator.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnitSelector(selectedUnit: Unit, onUnitChange: (Unit) -> Unit) {
    val units = listOf(Unit.MM, Unit.CM, Unit.M, Unit.FT, Unit.INCH, Unit.YD)
    val expanded = remember { mutableStateOf(false) }

    Box {
        OutlinedTextField(
            value = selectedUnit.name,
            onValueChange = {},
            label = { Text("Unit") },
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded.value = true }
        )
        DropdownMenu(
            expanded = expanded.value,
            onDismissRequest = { expanded.value = false }
        ) {
            units.forEach { unit ->
                DropdownMenuItem(
                    text = { Text(text = unit.name) },
                    onClick = {
                        onUnitChange(unit)
                        expanded.value = false
                    }
                )
            }
        }
    }
}