package no.steffenhove.betongkalkulator.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import no.steffenhove.betongkalkulator.ui.viewmodel.CalculationViewModel

@Composable
fun CalculationScreen(viewModel: CalculationViewModel) {
    var result by remember { mutableStateOf("") }
    val unit by viewModel.unit.collectAsState()
    val weightUnit by viewModel.weightUnit.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Input fields and other UI elements...

        Button(onClick = {
            val calculationResult = viewModel.calculateResult()
            result = String.format("%.2f", calculationResult) + " $weightUnit"
        }) {
            Text("Beregn")
        }

        Text(text = result, style = MaterialTheme.typography.h5)
    }
}