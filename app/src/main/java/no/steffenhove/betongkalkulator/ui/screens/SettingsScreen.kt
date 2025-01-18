package no.steffenhove.betongkalkulator.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import no.steffenhove.betongkalkulator.ui.viewmodel.SettingsViewModel

@Composable
fun SettingsScreen(viewModel: SettingsViewModel = viewModel()) {
    val density by viewModel.density.collectAsState()

    Column(modifier = Modifier.padding(16.dp)) {
        TextField(
            value = density.toString(),
            onValueChange = { viewModel.setDensity(it.toFloatOrNull() ?: 2400f) },
            label = { Text("Betong densitet (kg/m³)") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = { viewModel.resetDensity() }) {
            Text("Nullstill til standard")
        }
    }
}