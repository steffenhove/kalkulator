package no.steffenhove.betongkalkulator.ui.screens

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
import no.steffenhove.betongkalkulator.ui.components.AppDropdown
// VIKTIG: Endre importen til å bruke de samme hjelpefunksjonene som de andre skjermene
import no.steffenhove.betongkalkulator.ui.utils.convertToMeters
import no.steffenhove.betongkalkulator.ui.utils.getUnitSystemPreference // Riktig import
import no.steffenhove.betongkalkulator.ui.viewmodel.OverskjaeringViewModel

@Composable
fun OverskjaeringScreen(viewModel: OverskjaeringViewModel = viewModel()) {
    val context = LocalContext.current
    val result by viewModel.result.collectAsState()
    val infoMessage by viewModel.infoMessage.collectAsState()

    var thicknessInputTfv by rememberSaveable(stateSaver = TextFieldValue.Saver) { mutableStateOf(TextFieldValue("")) }
    var selectedBlade by rememberSaveable { mutableStateOf("800") }

    val blades = listOf("600", "700", "750", "800", "900", "1000", "1200", "1500", "1600")

    // --- HER ER FIKSEN ---
    // Leser innstillingen direkte, akkurat som i CalculationScreen.
    // Denne vil nå kjøres på nytt når skjermen tegnes opp etter navigasjon.
    val unitSystem = getUnitSystemPreference(context)
    val unitOptions = if (unitSystem == "Imperialsk") listOf("inch", "foot") else listOf("mm", "cm", "m")

    var selectedUnit by rememberSaveable { mutableStateOf("cm") }

    // Sørger for at valgt enhet er gyldig hvis enhetssystemet endres
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

        viewModel.calculate(selectedBlade.toInt(), tykkelseCm)
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // UI er uendret, men vil nå basere seg på den korrekt innleste 'unitSystem'-verdien
        AppDropdown(label = "Bladdiameter", options = blades, selectedOption = selectedBlade, onOptionSelected = { selectedBlade = it })
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

        if (infoMessage != null) {
            Text(text = infoMessage!!, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.error)
        } else {
            result?.let { res ->
                if (unitSystem == "Imperialsk") {
                    val minSkjaeringInch = res.minSkjaeringCm / 2.54f
                    val maksSkjaeringInch = res.maksSkjaeringCm / 2.54f
                    val minBorehullInch = res.minBorehullMm / 25.4f
                    Text("Min. skjæring: ${"%.1f".format(minSkjaeringInch)} inch")
                    Text("Maks. skjæring: ${"%.1f".format(maksSkjaeringInch)} inch")
                    Text("Min. borehull: ${"%.1f".format(minBorehullInch)} inch")
                } else {
                    when (selectedUnit) {
                        "mm" -> {
                            Text("Min. skjæring: ${"%.0f".format(res.minSkjaeringCm * 10)} mm")
                            Text("Maks. skjæring: ${"%.0f".format(res.maksSkjaeringCm * 10)} mm")
                        }
                        "cm" -> {
                            Text("Min. skjæring: ${"%.1f".format(res.minSkjaeringCm)} cm")
                            Text("Maks. skjæring: ${"%.1f".format(res.maksSkjaeringCm)} cm")
                        }
                        "m" -> {
                            Text("Min. skjæring: ${"%.2f".format(res.minSkjaeringCm / 100)} m")
                            Text("Maks. skjæring: ${"%.2f".format(res.maksSkjaeringCm / 100)} m")
                        }
                    }
                    Text("Min. borehull: ${"%.0f".format(res.minBorehullMm)} mm")
                }
            }
        }
    }
}