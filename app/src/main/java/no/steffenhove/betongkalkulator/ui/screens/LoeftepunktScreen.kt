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
import no.steffenhove.betongkalkulator.ui.viewmodel.LøftepunktViewModel

@Composable
fun LoeftepunktScreen(viewModel: LøftepunktViewModel = viewModel()) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val scrollState = rememberScrollState()

    val unitSystem = AppPreferenceManager.getUnitSystemPreference(context)
    val unitOptions = if (unitSystem == "Metrisk") listOf("mm", "cm", "m") else listOf("inch", "foot")
    val formOptions = listOf("Kjerne", "Firkant", "Trekant", "Trapes")
    val antallFesterOptions = listOf("1", "2", "3", "4", "6")
    val festetyper = listOf("Innvendig", "Utvendig")

    val (initialForm, initialUnit, initialCount) = AppPreferenceManager.getLastLiftPreferences(context)

    var selectedForm by rememberSaveable { mutableStateOf(initialForm.ifBlank { formOptions.first() }) }
    var selectedUnit by rememberSaveable { mutableStateOf(initialUnit.ifBlank { unitOptions.first() }) }
    var selectedFestetype by rememberSaveable { mutableStateOf(festetyper.first()) }
    var selectedAntallFester by rememberSaveable { mutableStateOf(initialCount.toString()) }

    var aInput by rememberSaveable(stateSaver = TextFieldValue.Saver) { mutableStateOf(TextFieldValue()) }
    var bInput by rememberSaveable(stateSaver = TextFieldValue.Saver) { mutableStateOf(TextFieldValue()) }
    var cInput by rememberSaveable(stateSaver = TextFieldValue.Saver) { mutableStateOf(TextFieldValue()) }
    var dInput by rememberSaveable(stateSaver = TextFieldValue.Saver) { mutableStateOf(TextFieldValue()) }

    var resultText by remember { mutableStateOf<String?>(null) }

    fun performCalculation() {
        keyboardController?.hide()

        AppPreferenceManager.saveLastLiftPreferences(
            context = context,
            form = selectedForm,
            unit = selectedUnit,
            count = selectedAntallFester.toIntOrNull() ?: 4
        )

        resultText = viewModel.beregnFesteplasseringSomTekst(
            form = selectedForm,
            antallFester = selectedAntallFester.toIntOrNull() ?: 4,
            festetype = selectedFestetype,
            a = aInput.text,
            b = bInput.text,
            enhet = selectedUnit,
            c = cInput.text,
            d = dInput.text
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        AppDropdown("Form", formOptions, selectedForm) { selectedForm = it }
        AppDropdown("Festetype", festetyper, selectedFestetype) { selectedFestetype = it }
        AppDropdown("Antall fester", antallFesterOptions, selectedAntallFester) { selectedAntallFester = it }
        AppDropdown("Enhet", unitOptions, selectedUnit) { selectedUnit = it }

        when (selectedForm) {
            "Kjerne" -> {
                InputField("Diameter ($selectedUnit)", aInput, { aInput = it }, ImeAction.Done) {
                    performCalculation()
                }
            }
            "Firkant" -> {
                InputField("Bredde ($selectedUnit)", aInput, { aInput = it }, ImeAction.Next)
                InputField("Høyde ($selectedUnit)", bInput, { bInput = it }, ImeAction.Done) {
                    performCalculation()
                }
            }
            "Trekant" -> {
                InputField("A-side ($selectedUnit)", aInput, { aInput = it }, ImeAction.Next)
                InputField("B-side ($selectedUnit)", bInput, { bInput = it }, ImeAction.Next)
                InputField("C-side ($selectedUnit)", cInput, { cInput = it }, ImeAction.Done) {
                    performCalculation()
                }
            }
            "Trapes" -> {
                InputField("A-side ($selectedUnit)", aInput, { aInput = it }, ImeAction.Next)
                InputField("B-side ($selectedUnit)", bInput, { bInput = it }, ImeAction.Next)
                InputField("C-side ($selectedUnit)", cInput, { cInput = it }, ImeAction.Next)
                InputField("D-side ($selectedUnit)", dInput, { dInput = it }, ImeAction.Done) {
                    performCalculation()
                }
            }
        }

        Button(
            onClick = { performCalculation() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Beregn")
        }

        Spacer(modifier = Modifier.height(16.dp))

        resultText?.let {
            Text(it, style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Composable
private fun InputField(
    label: String,
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    imeAction: ImeAction,
    onDone: (() -> Unit)? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Number,
            imeAction = imeAction
        ),
        keyboardActions = KeyboardActions(
            onDone = { onDone?.invoke() }
        ),
        modifier = Modifier.fillMaxWidth()
    )
}
