package no.steffenhove.betongkalkulator.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import no.steffenhove.betongkalkulator.ui.components.AppDropdown
import no.steffenhove.betongkalkulator.ui.utils.AppPreferenceManager
import no.steffenhove.betongkalkulator.ui.utils.convertToMeters


@Composable
fun OverskjaeringScreen() {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current

    val defaultUnit = AppPreferenceManager.getLastOverskjaeringUnit(context)
    val defaultBlade = AppPreferenceManager.getLastOverskjaeringBlade(context)

    var selectedUnit by rememberSaveable { mutableStateOf(defaultUnit) }
    var selectedBlade by rememberSaveable { mutableIntStateOf(defaultBlade) }
    var thicknessInput by rememberSaveable { mutableStateOf("") }
    var resultText by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(selectedUnit) {
        AppPreferenceManager.saveLastOverskjaeringUnit(context, selectedUnit)
    }
    LaunchedEffect(selectedBlade) {
        AppPreferenceManager.saveLastOverskjaeringBlade(context, selectedBlade)
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {

        Text("Overskjæringskalkulator", style = MaterialTheme.typography.titleLarge)

        Spacer(modifier = Modifier.height(16.dp))

        AppDropdown(
            label = "Enhet",
            options = listOf("mm", "cm", "m", "inch", "foot"),
            selectedOption = selectedUnit,
            onOptionSelected = { selectedUnit = it }
        )

        Spacer(modifier = Modifier.height(16.dp))

        AppDropdown(
            label = "Bladdiameter",
            options = listOf(600, 700, 750, 800, 900, 1000, 1200, 1500, 1600).map { it.toString() },
            selectedOption = selectedBlade.toString(),
            onOptionSelected = { selectedBlade = it.toInt() }
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = thicknessInput,
            onValueChange = { thicknessInput = it },
            label = { Text("Betongtykkelse") },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            val thicknessMeters = convertToMeters(thicknessInput, selectedUnit)
            if (thicknessMeters == null) {
                resultText = "Ugyldig tykkelse"
                return@Button
            }

            val radius = selectedBlade / 2000.0 // diameter / 2 / 1000 for m
            val minCut = radius - 0.144 // 144mm spindel
            val maxCut = radius // teoretisk

            val overskjaerer = minCut > thicknessMeters
            val anbefaltKjerne = if (overskjaerer)
                "Anbefalt å bore hull min. "+"%.0f".format(thicknessMeters * 1000 + 50)+" mm i diameter for å unngå overskjæring."
            else
                "Ingen overskjæring nødvendig."

            resultText = "Min skjæredybde: ${"%.1f".format(minCut)} m\n" +
                    "Maks skjæredybde: ${"%.1f".format(maxCut)} m\n" +
                    anbefaltKjerne

            keyboardController?.hide()
        }) {
            Text("Beregn")
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(resultText, style = MaterialTheme.typography.bodyLarge)
    }
}