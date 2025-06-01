package no.steffenhove.betongkalkulator.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import no.steffenhove.betongkalkulator.ui.components.AppDropdown
import no.steffenhove.betongkalkulator.ui.components.InputField
import no.steffenhove.betongkalkulator.ui.viewmodel.OverskjaeringViewModel
import no.steffenhove.betongkalkulator.ui.utils.getUnitSystem

@Composable
fun OverskjaeringScreen(viewModel: OverskjaeringViewModel = viewModel()) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    val unitSystem = getUnitSystem(context)
    val unitOptions = if (unitSystem == "Imperialsk") listOf("inch", "foot") else listOf("mm", "cm", "m")
    val bladeOptions = listOf("600", "700", "750", "800", "900", "1000", "1200", "1500", "1600")

    var selectedUnit by remember { mutableStateOf(unitOptions.first()) }
    var selectedBlade by remember { mutableStateOf(bladeOptions.first()) }
    var concreteThickness by remember { mutableStateOf("") }

    val thicknessFocusRequester = remember { FocusRequester() }
    val result by viewModel.result.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AppDropdown(
            label = "Enhet",
            options = unitOptions,
            selectedOption = selectedUnit,
            onOptionSelected = { selectedUnit = it }
        )

        AppDropdown(
            label = "Bladdiameter",
            options = bladeOptions,
            selectedOption = selectedBlade,
            onOptionSelected = { selectedBlade = it }
        )

        InputField(
            label = "Betongtykkelse",
            value = concreteThickness,
            onValueChange = { concreteThickness = it },
            keyboardType = KeyboardType.Number,
            unitSystem = selectedUnit,
            imeAction = ImeAction.Done,
            onImeAction = {
                focusManager.clearFocus()
                if (concreteThickness.isBlank()) {
                    Toast.makeText(context, "Ugyldig verdi", Toast.LENGTH_SHORT).show()
                } else {
                    viewModel.calculateOverskjaering(selectedBlade, concreteThickness, selectedUnit)
                }
            },
            focusRequester = thicknessFocusRequester
        )

        Button(onClick = {
            if (concreteThickness.isBlank()) {
                Toast.makeText(context, "Ugyldig verdi", Toast.LENGTH_SHORT).show()
                return@Button
            }
            viewModel.calculateOverskjaering(selectedBlade, concreteThickness, selectedUnit)
        }) {
            Text("Beregn")
        }

        result?.let {
            Text("Min. skjæring: ${it.minCut} m")
            Text("Maks. skjæring: ${it.maxCut} m")
            Text("Min. borehull: ${it.minCoreHole} m")
        }
    }
}
