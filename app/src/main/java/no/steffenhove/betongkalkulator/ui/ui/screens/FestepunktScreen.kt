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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.cos

@Composable
fun FestepunktScreen(context: Context = LocalContext.current) {
    var festeAvstand by remember { mutableStateOf("") }
    var vinkel by remember { mutableStateOf("") }
    var resultat by remember { mutableStateOf("") }

    val focusRequester1 = remember { FocusRequester() }
    val focusRequester2 = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(Unit) {
        focusRequester1.requestFocus()
    }

    Column(Modifier.padding(16.dp)) {
        Text("Festepunkt ved vinkelboring", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = festeAvstand,
            onValueChange = { festeAvstand = it },
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
            value = vinkel,
            onValueChange = { vinkel = it },
            label = { Text("Vinkel (grader)") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    keyboardController?.hide()
                    resultat = kalkulerForskyvning(festeAvstand, vinkel)
                }
            ),
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester2),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            keyboardController?.hide()
            resultat = kalkulerForskyvning(festeAvstand, vinkel)
        }) {
            Text("Beregn")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(resultat)
    }
}

fun kalkulerForskyvning(festeInput: String, vinkelInput: String): String {
    val feste = festeInput.replace(",", ".").toDoubleOrNull()
    val grad = vinkelInput.replace(",", ".").toDoubleOrNull()

    return if (feste != null && grad != null) {
        val radianer = grad * PI / 180
        val nyAvstand = feste / cos(radianer)
        val forskyvning = nyAvstand - feste
        "Nytt festeavstand: %.1f mm\nForskyvning: %.1f mm".format(nyAvstand, forskyvning)
    } else {
        "Ugyldige verdier"
    }
}
