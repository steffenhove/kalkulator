package no.steffenhove.betongkalkulator.ui.screens

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import no.steffenhove.betongkalkulator.ui.components.AppDropdown
import no.steffenhove.betongkalkulator.ui.utils.AppPreferenceManager
import no.steffenhove.betongkalkulator.ui.utils.convertToMeters
import no.steffenhove.betongkalkulator.ui.viewmodel.OverskjaeringViewModel

@Composable
fun OverskjaeringScreen(context: Context, viewModel: OverskjaeringViewModel = viewModel()) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val unitSystem = AppPreferenceManager.getUnitSystemPreference(context)
    val scrollState = rememberScrollState()

    val metricUnits = listOf("mm", "cm", "m")
    val imperialUnits = listOf("inch", "foot")
    val unitOptions = if (unitSystem == "Metrisk") metricUnits else imperialUnits

    val bladeOptions = listOf("600", "700", "750", "800", "900", "1000", "1200", "1500", "1600")

    val (initialBlade, initialUnit, _) = AppPreferenceManager.getLastUsedValues(context, "overskjaering")

    var selectedBlade by rememberSaveable { mutableStateOf(initialBlade.ifBlank { "800" }) }
    var selectedUnit by rememberSaveable { mutableStateOf(initialUnit.ifBlank { unitOptions.first() }) }
    var thicknessInput by rememberSaveable(stateSaver = TextFieldValue.Saver) { mutableStateOf(TextFieldValue()) }

    val result by viewModel.result.collectAsState()
    val infoMessage by viewModel.infoMessage.collectAsState()

    fun performCalculation() {
        val thickness = convertToMeters(thicknessInput.text, selectedUnit) ?: 0.0
        val blade = selectedBlade.toIntOrNull() ?: 800

        viewModel.calculate(blade, (thickness * 100).toInt()) // thickness i cm

        AppPreferenceManager.saveLastUsedValues(context, "overskjaering", selectedBlade, selectedUnit)
        keyboardController?.hide()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        AppDropdown("Bladdiameter", bladeOptions, selectedBlade) { selectedBlade = it }
        AppDropdown("Enhet", unitOptions, selectedUnit) { selectedUnit = it }

        OutlinedTextField(
            value = thicknessInput,
            onValueChange = { thicknessInput = it },
            label = { Text("Betongtykkelse ($selectedUnit)") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = { performCalculation() })
        )

        Button(onClick = { performCalculation() }, modifier = Modifier.fillMaxWidth()) {
            Text("Beregn overskjæring")
        }

        Spacer(modifier = Modifier.height(16.dp))

        infoMessage?.let {
            Text(it, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.error)
        }

        result?.let {
            Text("Min. skjæredybde: ${"%.1f".format(it.minSkjaeringCm)} cm")
            Text("Maks. skjæredybde: ${"%.1f".format(it.maksSkjaeringCm)} cm")
            Text("Min. borehull: ${"%.0f".format(it.minBorehullMm)} mm")
        }
    }
}
