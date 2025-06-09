// Denne filen er LoeftepunktScreen.kt med komplett logikk og visning
// for form, antall fester, innvendig/utvendig og resultat basert på senter festepunkt

package no.steffenhove.betongkalkulator.ui.screens

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import no.steffenhove.betongkalkulator.ui.components.InputField
import no.steffenhove.betongkalkulator.ui.viewmodel.LøftepunktViewModel
import no.steffenhove.betongkalkulator.ui.utils.getUnitSystemPreference
import no.steffenhove.betongkalkulator.ui.utils.loadLiftPreferences
import no.steffenhove.betongkalkulator.ui.utils.saveLiftPreferences

fun getUnitOptions(system: String): List<String> {
    return if (system == "Imperialsk") listOf("inch", "foot") else listOf("mm", "cm", "m")
}

@Composable
fun LoeftepunktScreen(viewModel: LøftepunktViewModel = viewModel()) {
    val context = LocalContext.current
    val keyboard = LocalSoftwareKeyboardController.current
    val unitSystem = getUnitSystemPreference(context)
    val unitOptions = getUnitOptions(unitSystem)

    val (lastForm, lastUnit, lastFester) = loadLiftPreferences(context)

    var selectedForm by remember { mutableStateOf(lastForm) }
    var selectedUnit by remember { mutableStateOf(lastUnit) }
    var selectedAntallFester by remember { mutableStateOf(lastFester.toString()) }
    var selectedFestetype by remember { mutableStateOf("Innvendig") }

    val formOptions = listOf("Kjerne", "Firkant", "Trekant", "Trapes")
    val antallFesterOptions = listOf("1", "2", "3", "4", "6")
    val festetypeOptions = listOf("Innvendig", "Utvendig")

    val a = remember { mutableStateOf(TextFieldValue()) }
    val b = remember { mutableStateOf(TextFieldValue()) }
    val c = remember { mutableStateOf(TextFieldValue()) }
    val d = remember { mutableStateOf(TextFieldValue()) }

    val resultat = remember { mutableStateOf("") }

    val f1 = remember { FocusRequester() }
    val f2 = remember { FocusRequester() }
    val f3 = remember { FocusRequester() }
    val f4 = remember { FocusRequester() }
    val f5 = remember { FocusRequester() }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        AppDropdown("Form", formOptions, selectedForm) {
            selectedForm = it
        }

        AppDropdown("Enhet", unitOptions, selectedUnit) {
            selectedUnit = it
        }

        AppDropdown("Antall fester", antallFesterOptions, selectedAntallFester) {
            selectedAntallFester = it
        }

        AppDropdown("Festetype", festetypeOptions, selectedFestetype) {
            selectedFestetype = it
        }

        InputField(
            label = "Lengde A",
            state = a,
            unit = selectedUnit,
            focus = f1,
            nextFocus = f2
        )

        InputField(
            label = "Lengde B",
            state = b,
            unit = selectedUnit,
            focus = f2,
            nextFocus = if (selectedForm == "Trekant" || selectedForm == "Trapes") f3 else f5
        )

        if (selectedForm == "Trekant" || selectedForm == "Trapes") {
            InputField(
                label = if (selectedForm == "Trekant") "Lengde C" else "Lengde C (topp)",
                state = c,
                unit = selectedUnit,
                focus = f3,
                nextFocus = if (selectedForm == "Trapes") f4 else f5
            )
        }

        if (selectedForm == "Trapes") {
            InputField(
                label = "Lengde D",
                state = d,
                unit = selectedUnit,
                focus = f4,
                nextFocus = f5
            )
        }

        Button(
            onClick = {
                resultat.value = viewModel.beregnFesteplasseringSomTekst(
                    form = selectedForm,
                    antallFester = selectedAntallFester.toIntOrNull() ?: 2,
                    festetype = selectedFestetype,
                    a = a.value.text,
                    b = b.value.text,
                    enhet = selectedUnit,
                    c = c.value.text,
                    d = d.value.text
                )
                saveLiftPreferences(context, selectedForm, selectedUnit, selectedAntallFester.toIntOrNull() ?: 2)
                keyboard?.hide()
            },
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(f5)
        ) {
            Text("Beregn")
        }

        if (resultat.value.isNotBlank()) {
            Text(
                text = resultat.value,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            )
        }
    }
}
