package no.steffenhove.betongkalkulator.ui.screens

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable // Import for rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue // Import for TextFieldValue
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.cos

@Composable
fun FestepunktScreen(context: Context = LocalContext.current) {
    // Bruker rememberSaveable for å beholde tilstand ved rotasjon
    // For TextFieldValue, bruk TextFieldValue.Saver
    var festeAvstandTfv by rememberSaveable(stateSaver = TextFieldValue.Saver) { mutableStateOf(TextFieldValue("")) }
    var vinkelTfv by rememberSaveable(stateSaver = TextFieldValue.Saver) { mutableStateOf(TextFieldValue("")) }
    var resultat by rememberSaveable { mutableStateOf("") } // Resultat kan også lagres hvis ønskelig

    val focusRequester1 = remember { FocusRequester() }
    val focusRequester2 = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(Unit) {
        focusRequester1.requestFocus()
    }

    fun calculate() { // Flyttet kalkuleringslogikk inn for å bruke state-variabler direkte
        resultat = kalkulerForskyvning(festeAvstandTfv.text, vinkelTfv.text)
        keyboardController?.hide()
    }

    Column(Modifier.padding(16.dp)) {
        Text("Festepunkt ved vinkelboring", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = festeAvstandTfv,
            onValueChange = { festeAvstandTfv = it },
            label = { Text("Opprinnelig festeavstand (mm)") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusRequester2.requestFocus() }
            ),
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester1),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = vinkelTfv,
            onValueChange = { vinkelTfv = it },
            label = { Text("Vinkel (grader)") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { calculate() } // Kaller calculate-funksjonen
            ),
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester2),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { calculate() }) { // Kaller calculate-funksjonen
            Text("Beregn")
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (resultat.isNotBlank()) { // Vis kun hvis det er et resultat
            Text(resultat)
        }
    }
}

// kalkulerForskyvning forblir uendret, men tar String som input
fun kalkulerForskyvning(festeInput: String, vinkelInput: String): String {
    val feste = festeInput.replace(",", ".").toDoubleOrNull()
    val grad = vinkelInput.replace(",", ".").toDoubleOrNull()

    return if (feste != null && grad != null && grad >= 0 && grad < 90) { // Lagt til sjekk for gyldig vinkel
        if (feste == 0.0) {
            "Festeavstand kan ikke være null."
        } else {
            val radianer = grad * PI / 180
            val nyAvstand = feste / cos(radianer)
            val forskyvning = nyAvstand - feste
            "Nytt festeavstand: %.1f mm\nForskyvning: %.1f mm".format(nyAvstand, forskyvning)
        }
    } else if (grad != null && (grad < 0 || grad >= 90)) {
        "Vinkel må være mellom 0 og 89 grader."
    }
    else {
        "Ugyldige verdier. Fyll inn tall."
    }
}