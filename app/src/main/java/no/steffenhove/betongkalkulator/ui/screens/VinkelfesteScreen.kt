package no.steffenhove.betongkalkulator.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.focus.FocusRequester
import no.steffenhove.betongkalkulator.ui.components.AppDropdown
import no.steffenhove.betongkalkulator.ui.components.AppScaffold
import no.steffenhove.betongkalkulator.ui.components.InputField
import no.steffenhove.betongkalkulator.ui.utils.AppPreferenceManager
import no.steffenhove.betongkalkulator.ui.utils.convertToMeters
import no.steffenhove.betongkalkulator.ui.utils.getUnitSystemPreference
import kotlin.math.PI
import kotlin.math.cos

@Composable
fun VinkelfesteScreen(
    navigateBack: () -> Unit
) {
    val context = LocalContext.current
    val keyboard = LocalSoftwareKeyboardController.current
    val scrollState = rememberScrollState()

    // Enhetssystem fra innstillinger
    val unitSystem = getUnitSystemPreference(context)
    val metricUnits = listOf("mm", "cm", "m")
    val imperialUnits = listOf("inch", "foot")
    val unitOptions = if (unitSystem == "Imperialsk") imperialUnits else metricUnits

    // Sist brukte enhet (fall tilbake til første alternativ om lagret enhet ikke finnes)
    var selectedUnit by rememberSaveable {
        mutableStateOf(
            AppPreferenceManager.getLastFestepunktUnit(context)
                .takeIf { it in unitOptions } ?: unitOptions.first()
        )
    }

    // Input-felt: standard feste ved 90° og borevinkel
    val standardFesteState = rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(
            TextFieldValue(
                AppPreferenceManager.loadPreference(context, "vinkelfeste_standard_feste", "")
            )
        )
    }

    val angleState = rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(
            TextFieldValue(
                AppPreferenceManager.loadPreference(context, "vinkelfeste_vinkel", "")
            )
        )
    }

    // Fokusrekkefølge
    val fFeste = remember { FocusRequester() }
    val fVinkel = remember { FocusRequester() }

    // Resultat / feil
    var resultText by rememberSaveable { mutableStateOf("") }
    var errorText by rememberSaveable { mutableStateOf("") }

    // --- LAGRING AV PREFERANSER VED ENDRING ---

    LaunchedEffect(selectedUnit) {
        AppPreferenceManager.saveLastFestepunktUnit(context, selectedUnit)
    }

    LaunchedEffect(standardFesteState.value.text) {
        AppPreferenceManager.savePreference(
            context,
            "vinkelfeste_standard_feste",
            standardFesteState.value.text
        )
    }

    LaunchedEffect(angleState.value.text) {
        AppPreferenceManager.savePreference(
            context,
            "vinkelfeste_vinkel",
            angleState.value.text
        )
    }

    // --- HJELPEFUNKSJONER ---

    fun parseDouble(text: String): Double? =
        text
            .replace(",", ".")
            .replace("[^0-9.+-]".toRegex(), "")
            .toDoubleOrNull()

    fun fromMeters(meters: Double, unit: String): Double {
        return when (unit) {
            "mm" -> meters * 1000.0
            "cm" -> meters * 100.0
            "m" -> meters
            "inch" -> meters / 0.0254
            "foot" -> meters / 0.3048
            else -> meters
        }
    }

    fun formatLength(meters: Double, unit: String): String {
        val raw = fromMeters(meters, unit)
        return String.format("%.1f %s", raw, unit)
    }

    fun calculate() {
        errorText = ""
        resultText = ""

        val standardFesteMeters = convertToMeters(
            standardFesteState.value.text,
            selectedUnit
        )
        val angleDeg = parseDouble(angleState.value.text)

        if (standardFesteMeters == null || standardFesteMeters <= 0.0) {
            errorText = "Ugyldig standard festeavstand."
            return
        }
        if (angleDeg == null || angleDeg <= 0.0 || angleDeg >= 89.0) {
            errorText = "Vinkel må være mellom 1° og 89°."
            return
        }

        val angleRad = angleDeg * PI / 180.0

        // standard = nyFeste * cos(vinkel)  =>  nyFeste = standard / cos(vinkel)
        val newFesteMeters = standardFesteMeters / cos(angleRad)
        val deltaMeters = newFesteMeters - standardFesteMeters

        val standardFesteFormatted = formatLength(standardFesteMeters, selectedUnit)
        val newFesteFormatted = formatLength(newFesteMeters, selectedUnit)
        val deltaFormatted = formatLength(deltaMeters, selectedUnit)

        resultText = buildString {
            appendLine("Standard feste ved 90°:")
            appendLine("- $standardFesteFormatted")
            appendLine()
            appendLine("Borevinkel: ${String.format("%.1f°", angleDeg)}")
            appendLine()
            appendLine("Nytt feste ved denne vinkelen:")
            appendLine("- $newFesteFormatted")
            appendLine()
            appendLine("Feste forskyves med:")
            appendLine("- $deltaFormatted (ut fra kanten i samme retning som du vinkelborrer)")
        }

        keyboard?.hide()
    }

    AppScaffold(
        title = "Vinkelboring – feste for stativ"
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(scrollState)
        ) {
            Text(
                text = "Regn ut hvor mye du må flytte festet til stativet ved vinkelboring " +
                        "for å beholde samme \"startpunkt\" som ved 90°.",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.padding(top = 16.dp))

            AppDropdown(
                label = "Enhet",
                options = unitOptions,
                selectedOption = selectedUnit,
                onOptionSelected = { selectedUnit = it }
            )

            Spacer(modifier = Modifier.padding(top = 16.dp))

            InputField(
                label = "Standard feste ved 90°",
                state = standardFesteState,
                unit = selectedUnit,
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next,
                focus = fFeste,
                nextFocus = fVinkel
            )

            InputField(
                label = "Borevinkel (grader fra rett på)",
                state = angleState,
                unit = "°",
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done,
                focus = fVinkel,
                onDone = { calculate() }
            )

            Spacer(modifier = Modifier.padding(top = 16.dp))

            Button(
                onClick = { calculate() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Beregn nytt feste")
            }

            Spacer(modifier = Modifier.padding(top = 16.dp))

            if (errorText.isNotEmpty()) {
                Text(
                    text = errorText,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            if (resultText.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Text(
                        text = resultText,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.padding(top = 8.dp))

            Text(
                text = "OBS: Idealiserte beregninger.\n" +
                        "Konsoller, stativgeometri og praktiske forhold kan gi små avvik. " +
                        "Bruk alltid faglig skjønn.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
