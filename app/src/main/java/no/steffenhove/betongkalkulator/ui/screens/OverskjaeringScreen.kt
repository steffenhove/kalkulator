package no.steffenhove.betongkalkulator.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
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
import no.steffenhove.betongkalkulator.ui.utils.getUnitSystemPreference
import no.steffenhove.betongkalkulator.ui.utils.convertToMeters
import no.steffenhove.betongkalkulator.ui.viewmodel.OverskjaeringViewModel

data class Blade(val diameterMm: Int, val metricLabel: String, val imperialLabel: String)

@Composable
fun OverskjaeringScreen(viewModel: OverskjaeringViewModel = viewModel()) {
    val context = LocalContext.current
    val keyboard = LocalSoftwareKeyboardController.current
    val result by viewModel.result.collectAsState()
    val infoMessage by viewModel.infoMessage.collectAsState()

    val thickness = remember { mutableStateOf(TextFieldValue()) }
    val focus1 = remember { FocusRequester() }

    val availableBlades = remember {
        listOf(
            Blade(600, "600 mm", "24\""),
            Blade(700, "700 mm", "28\""),
            Blade(750, "750 mm", "30\""),
            Blade(800, "800 mm", "32\""),
            Blade(900, "900 mm", "36\""),
            Blade(1000, "1000 mm", "40\""),
            Blade(1200, "1200 mm", "48\""),
            Blade(1500, "1500 mm", "60\""),
            Blade(1600, "1600 mm", "64\"")
        )
    }

    var selectedBladeMm by rememberSaveable { mutableStateOf(800) }
    val unitSystem = getUnitSystemPreference(context)
    var selectedUnit by rememberSaveable { mutableStateOf("cm") }

    val bladeDisplayOptions = if (unitSystem == "Imperialsk") {
        availableBlades.map { it.imperialLabel }
    } else {
        availableBlades.map { it.metricLabel }
    }

    val unitOptions = if (unitSystem == "Imperialsk") listOf("inch", "foot") else listOf("mm", "cm", "m")

    LaunchedEffect(unitSystem) {
        if (selectedUnit !in unitOptions) {
            selectedUnit = unitOptions.first()
        }
    }

    fun performCalculation() {
        val thicknessNormalized = thickness.value.text.trim().replace(',', '.')
        if (thicknessNormalized.isBlank()) {
            Toast.makeText(context, "Tykkelse kan ikke være tom", Toast.LENGTH_SHORT).show()
            return
        }
        val tykkelseCm = convertToMeters(thicknessNormalized, selectedUnit)?.let { (it * 100).toInt() }
        if (tykkelseCm == null) {
            Toast.makeText(context, "Ugyldig tykkelse eller enhet", Toast.LENGTH_SHORT).show()
            return
        }
        viewModel.calculate(selectedBladeMm, tykkelseCm)
        keyboard?.hide()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        val selectedBladeLabel = availableBlades.find { it.diameterMm == selectedBladeMm }?.let {
            if (unitSystem == "Imperialsk") it.imperialLabel else it.metricLabel
        } ?: ""

        AppDropdown(
            label = "Bladdiameter",
            options = bladeDisplayOptions,
            selectedOption = selectedBladeLabel,
            onOptionSelected = { selectedLabel ->
                val newSelectedBlade = availableBlades.find {
                    if (unitSystem == "Imperialsk") it.imperialLabel == selectedLabel else it.metricLabel == selectedLabel
                }
                if (newSelectedBlade != null) {
                    selectedBladeMm = newSelectedBlade.diameterMm
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = thickness.value,
            onValueChange = { thickness.value = it },
            label = { Text("Betongtykkelse") },
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focus1),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { performCalculation() }
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        AppDropdown(
            label = "Enhet",
            options = unitOptions,
            selectedOption = selectedUnit,
            onOptionSelected = { selectedUnit = it }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { performCalculation() },
            modifier = Modifier.align(Alignment.End)
        ) { Text("Beregn") }

        Spacer(modifier = Modifier.height(24.dp))

        if (infoMessage != null) {
            Text(
                text = infoMessage ?: "",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.error
            )
        } else {
            result?.let { res ->
                Text(
                    text = res.toString(),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}
