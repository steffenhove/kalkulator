package no.steffenhove.betongkalkulator.ui.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import no.steffenhove.betongkalkulator.ui.components.AppDropdown
import no.steffenhove.betongkalkulator.ui.utils.getUnitSystemPreference
import no.steffenhove.betongkalkulator.ui.utils.convertToMeters
import no.steffenhove.betongkalkulator.ui.viewmodel.OverskjaeringViewModel

// --- FIKS 1: Dataklasse for å holde på blad-informasjon ---
// Denne kan ligge her eller i en egen fil i 'model'-pakken
data class Blade(val diameterMm: Int, val metricLabel: String, val imperialLabel: String)
// -----------------------------------------------------------

@Composable
fun OverskjaeringScreen(viewModel: OverskjaeringViewModel = viewModel()) {
    val context = LocalContext.current
    val result by viewModel.result.collectAsState()
    val infoMessage by viewModel.infoMessage.collectAsState()

    var thicknessInputTfv by rememberSaveable(stateSaver = TextFieldValue.Saver) { mutableStateOf(TextFieldValue("")) }

    // --- FIKS 2: Lag en komplett liste med blad-objekter ---
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
    // ----------------------------------------------------

    // State for å holde på den valgte bladdiameteren i MM (for beregning)
    var selectedBladeMm by rememberSaveable { mutableStateOf(800) }

    val unitSystem = getUnitSystemPreference(context)
    var selectedUnit by rememberSaveable { mutableStateOf("cm") }

    // --- FIKS 3: Tilpass listene for nedtrekksmenyene basert på enhetssystem ---
    val bladeDisplayOptions = if (unitSystem == "Imperialsk") {
        availableBlades.map { it.imperialLabel }
    } else {
        availableBlades.map { it.metricLabel }
    }

    val unitOptions = if (unitSystem == "Imperialsk") listOf("inch", "foot") else listOf("mm", "cm", "m")
    // --------------------------------------------------------------------------

    // Sørger for at valgt enhet er gyldig hvis systemet endres
    LaunchedEffect(unitSystem) {
        if (selectedUnit !in unitOptions) {
            selectedUnit = unitOptions.first()
        }
    }

    fun performCalculation() {
        val thicknessNormalized = thicknessInputTfv.text.trim().replace(',', '.')
        if (thicknessNormalized.isBlank()) {
            Toast.makeText(context, "Tykkelse kan ikke være tom", Toast.LENGTH_SHORT).show()
            return
        }
        val tykkelseCm = convertToMeters(thicknessNormalized, selectedUnit)?.let { (it * 100).toInt() }
        if (tykkelseCm == null) {
            Toast.makeText(context, "Ugyldig tykkelse eller enhet", Toast.LENGTH_SHORT).show()
            return
        }
        // Sender alltid millimeter til ViewModel
        viewModel.calculate(selectedBladeMm, tykkelseCm)
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {

        // --- FIKS 4: Oppdatert logikk for AppDropdown for blad ---
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
        // -----------------------------------------------------

        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = thicknessInputTfv,
            onValueChange = { thicknessInputTfv = it },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            label = { Text("Betongtykkelse") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(8.dp))
        AppDropdown(label = "Enhet", options = unitOptions, selectedOption = selectedUnit, onOptionSelected = { selectedUnit = it })
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { performCalculation() },
            modifier = Modifier.align(Alignment.End)
        ) { Text("Beregn") }
        Spacer(modifier = Modifier.height(24.dp))

        // Resultatvisningen (denne er allerede tilpasset for enhetssystem)
        if (infoMessage != null) {
            Text(text = infoMessage!!, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.error)
        } else {
            result?.let { res ->
                // ... (den eksisterende logikken din for å vise resultat i riktig enhet)
            }
        }
    }
}