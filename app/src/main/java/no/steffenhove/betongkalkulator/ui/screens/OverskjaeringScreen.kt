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
import no.steffenhove.betongkalkulator.ui.utils.getUnitSystemPreference
import no.steffenhove.betongkalkulator.ui.utils.getUnitPreference
import no.steffenhove.betongkalkulator.ui.viewmodel.OverskjaeringViewModel
import no.steffenhove.betongkalkulator.ui.utils.getUnitPreference


@Composable
fun OverskjaeringScreen(viewModel: OverskjaeringViewModel = viewModel()) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val scrollState = rememberScrollState()
    val unitSystem = getUnitSystemPreference(context)
    val unitPreference = getUnitPreference(context)

    val bladeSizeInput by viewModel.bladeSizeInput.collectAsState()
    val thicknessInput by viewModel.thicknessInput.collectAsState()
    val minCut by viewModel.minCut.collectAsState()
    val maxCut by viewModel.maxCut.collectAsState()
    val minBore by viewModel.minBoreDiameter.collectAsState()
    val maxBore by viewModel.maxBoreDiameter.collectAsState()

    val bladeState = rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(bladeSizeInput))
    }

    val thicknessState = rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(thicknessInput))
    }

    val focusRequester = remember { FocusRequester() }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Overskjæringskalkulator",
            style = MaterialTheme.typography.headlineSmall
        )

        OutlinedTextField(
            value = bladeState.value,
            onValueChange = {
                bladeState.value = it
                viewModel.setBladeSizeInput(it.text)
                viewModel.calculate(context)
            },
            label = { Text("Bladdiameter (${unitPreference.symbol})") },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = thicknessState.value,
            onValueChange = {
                thicknessState.value = it
                viewModel.setThicknessInput(it.text)
                viewModel.calculate(context)
            },
            label = { Text("Betongtykkelse (${unitPreference.symbol})") },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = {
                keyboardController?.hide()
                viewModel.calculate(context)
            }),
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester)
        )

        Divider()

        if (minCut != null && maxCut != null) {
            Text("Min. gjennomskjæring: ${"%.1f".format(minCut)} cm")
            Text("Maks. gjennomskjæring: ${"%.1f".format(maxCut)} cm")
        }

        if (minBore != null && maxBore != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text("Min. anbefalt borehull: ${"%.1f".format(minBore)} mm")
            Text("Maks. anbefalt borehull: ${"%.1f".format(maxBore)} mm")
        }
    }
}
