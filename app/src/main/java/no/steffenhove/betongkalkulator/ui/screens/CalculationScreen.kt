package no.steffenhove.betongkalkulator.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import no.steffenhove.betongkalkulator.ui.components.InputField
import no.steffenhove.betongkalkulator.ui.components.Unit
import no.steffenhove.betongkalkulator.ui.components.UnitSelector
import no.steffenhove.betongkalkulator.ui.viewmodel.CalculationViewModel

@Composable
fun CalculationScreen(viewModel: CalculationViewModel = viewModel()) {
    var dimensions by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    val result by viewModel.result.collectAsState()
    val selectedUnit by viewModel.selectedUnit.collectAsState()
    val selectedWeightUnit by viewModel.selectedWeightUnit.collectAsState()

    Column(modifier = Modifier.padding(16.dp)) {
        UnitSelector(selectedUnit, onUnitChange = { viewModel.setSelectedUnit(it) })
        Spacer(modifier = Modifier.height(16.dp))

        InputField(value = dimensions, onValueChange = { dimensions = it }, label = "Dimensjoner", modifier = Modifier.fillMaxWidth().height(56.dp))
        Spacer(modifier = Modifier.height(8.dp))

        InputField(value = weight, onValueChange = { weight = it }, label = "Vekt", modifier = Modifier.fillMaxWidth().height(56.dp))
        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            viewModel.calculate(dimensions.toFloat(), weight.toFloat())
        }) {
            Text("Beregn")
        }
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Resultat: %.2f ${selectedWeightUnit}".format(result),
            modifier = Modifier.padding(top = 16.dp)
        )
    }
}