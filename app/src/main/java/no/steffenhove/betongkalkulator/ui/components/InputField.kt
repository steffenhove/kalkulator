package no.steffenhove.betongkalkulator.ui.components

import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun InputField(value: String, onValueChange: (String) -> Unit, modifier: Modifier = Modifier, label: String = "Label") {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = modifier
    )
}