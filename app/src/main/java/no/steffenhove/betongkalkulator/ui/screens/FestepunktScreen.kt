package no.steffenhove.betongkalkulator.ui.screens

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
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
import no.steffenhove.betongkalkulator.ui.components.AppDropdown
import no.steffenhove.betongkalkulator.ui.components.DimensionField
import no.steffenhove.betongkalkulator.ui.utils.convertToMeters
import no.steffenhove.betongkalkulator.ui.utils.getUnitSystemPreference
import kotlin.math.PI
import kotlin.math.cos

@Composable
fun FestepunktScreen(context: Context = LocalContext.current) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val unitSystem = getUnitSystemPreference(context)

    val metricUnits = listOf("mm", "cm", "m")
    val imperialUnits = listOf("inch", "foot")
    val unitOptions = if (unitSystem == "Imperialsk") imperialUnits else metricUnits

    var selectedUnit by rememberSaveable { mutableStateOf(unitOptions.first()) }

    val festeTfv = rememberSaveable(stateSaver = TextFieldValue.Saver) { mutableStateOf(TextFieldValue("")) }
    val vinkelTfv = rememberSaveable(stateSaver = TextFieldValue.Saver) { mutableStateOf(TextFieldValue("")) }
    var resultat by rememberSaveable { mutableStateOf("") }

    val focusFeste = remember { FocusRequester() }
    val focusVinkel = remember { FocusRequester() }

    LaunchedEffect(unitSystem) {
        if (selectedUnit !in unitOptions) {
            selectedUnit = unitOptions.first()
        }
    }

    fun kalkulerForskyvning(): String {
        val festeMm = convertToMeters(festeTfv.value.text, selectedUnit)?.times(1000)  // til mm
        val grader = vinkelTfv.value.text.replace(",", ".").toDoubleOrNull()

        return if (festeMm != null && grader != null && grader in 0.0..<90.0) {
            val radianer = grader * PI / 180
            val nyAvstand = festeMm / cos(radianer)
            val forskyvning = nyAvstand - festeMm
            "Ny festeavstand: %.1f mm\nForskyvning: %.1f mm".format(nyAvstand, forskyvning)
        } else if (grader != null && grader !in 0.0..<90.0) {
            "Vinkel må være mellom 0 og 89 grader."
        } else {
            "Ugyldige verdier. Skriv inn gyldige tall."
        }
    }

    Column(modifier = Modifier
        .padding(16.dp)
        .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Festepunkt ved vinkelboring", style = MaterialTheme.typography.headlineSmall)

        DimensionField(
            state = festeTfv,
            label = "Festeavstand",
            unit = selectedUnit,
            focus = focusFeste,
            nextFocus = focusVinkel
        )

        OutlinedTextField(
            value = vinkelTfv.value,
            onValueChange = { vinkelTfv.value = it },
            label = { Text("Vinkel (grader)") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusVinkel),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    resultat = kalkulerForskyvning()
                    keyboardController?.hide()
                }
            )
        )

        AppDropdown(
            label = "Enhet",
            options = unitOptions,
            selectedOption = selectedUnit,
            onOptionSelected = { selectedUnit = it }
        )

        Button(onClick = {
            resultat = kalkulerForskyvning()
            keyboardController?.hide()
        }) {
            Text("Beregn")
        }

        if (resultat.isNotBlank()) {
            Text(resultat, style = MaterialTheme.typography.bodyLarge)
        }
    }
}
