package no.steffenhove.betongkalkulator.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import no.steffenhove.betongkalkulator.ui.viewmodel.SettingsViewModel

@Composable
fun SettingsScreen(viewModel: SettingsViewModel) {
    val betongDensitet by viewModel.betongDensitet.collectAsState()
    val lecaDensitet by viewModel.lecaDensitet.collectAsState()
    val siporexDensitet by viewModel.siporexDensitet.collectAsState()

    Column(modifier = Modifier.padding(16.dp)) {
        TextField(
            value = betongDensitet.toString(),
            onValueChange = { viewModel.updateBetongDensitet(it.toDoubleOrNull() ?: 2400.0) },
            label = { Text("Betong Densitet") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        TextField(
            value = lecaDensitet.toString(),
            onValueChange = { viewModel.updateLecaDensitet(it.toDoubleOrNull() ?: 800.0) },
            label = { Text("Leca Densitet") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        TextField(
            value = siporexDensitet.toString(),
            onValueChange = { viewModel.updateSiporexDensitet(it.toDoubleOrNull() ?: 600.0) },
            label = { Text("Siporex Densitet") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        Button(onClick = {
            viewModel.resetToDefaultDensiteter()
        }) {
            Text("Tilbakestill til standard")
        }
    }
}