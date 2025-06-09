// Fil: AppDropdown.kt (DENNE FILEN FORBLIR UENDRET HVIS DU FØLGER DENNE LØSNINGEN)
package no.steffenhove.betongkalkulator.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.PopupProperties

@OptIn(ExperimentalMaterial3Api::class) // Hvis du bruker TextFieldDefaults.colors
@Composable
fun AppDropdown(
    label: String, // Obligatorisk
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
    // modifier: Modifier = Modifier // Den gamle hadde ikke denne, men det er lurt å ha med
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) { // Du kan legge til mottatt modifier her hvis du legger den til i signaturen
        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = selectedOption,
                onValueChange = { /* Gjør ingenting, da verdien settes av dropdown */ },
                label = { Text(label) }, // Label vises alltid
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Filled.ArrowDropDown,
                        contentDescription = "Dropdown Icon",
                        modifier = Modifier.clickable { expanded = !expanded }
                    )
                },
                // Farger fra din opprinnelige kode
                colors = TextFieldDefaults.colors(
                    disabledTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledContainerColor = MaterialTheme.colorScheme.surface,
                    disabledIndicatorColor = MaterialTheme.colorScheme.outline,
                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    // disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant, // Ikke i bruk
                    disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = { expanded = true }),
                enabled = false, // Din opprinnelige måte for å hindre fokus
                singleLine = true
            )

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.fillMaxWidth(),
                properties = PopupProperties(focusable = true)
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            onOptionSelected(option)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}