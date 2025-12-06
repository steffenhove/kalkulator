package no.steffenhove.betongkalkulator.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import no.steffenhove.betongkalkulator.ui.components.AppDropdown
import no.steffenhove.betongkalkulator.ui.components.AppScaffold
import no.steffenhove.betongkalkulator.ui.viewmodel.OverskjaeringResult
import no.steffenhove.betongkalkulator.ui.viewmodel.OverskjaeringViewModel

@Composable
fun OverskjaeringScreen(
    navigateBack: () -> Unit, // vi tar den inn for å matche AppNavigation, men bruker den ikke nå
    viewModel: OverskjaeringViewModel = viewModel()
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val scrollState = rememberScrollState()

    val result: OverskjaeringResult? = viewModel.result
    val errorMessage: String? = viewModel.errorMessage

    // Tilgjengelige blad – union av verdier fra tabellene
    val bladeOptions = listOf(
        500, 600, 650, 750, 800, 825, 900, 1000, 1025, 1200, 1500, 1600
    )

    val unitOptions = listOf("mm", "cm")

    AppScaffold(
        title = "Overskjæring"
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.Top
        ) {

            // ---------- Bladdiameter ----------
            Text(
                text = "Bladdiameter",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            AppDropdown(
                label = "",
                options = bladeOptions.map { "Ø${it} mm" },
                selectedOption = "Ø${viewModel.selectedBladeDiameter} mm",
                onOptionSelected = { chosen ->
                    val value = bladeOptions.firstOrNull { "Ø${it} mm" == chosen }
                    if (value != null) {
                        viewModel.onBladeSelected(value)
                    }
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // ---------- Betongtykkelse ----------
            Text(
                text = "Betongtykkelse",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = viewModel.thicknessInput,
                    onValueChange = { viewModel.onThicknessChanged(it) },
                    label = { Text("Tykkelse") },
                    singleLine = true,
                    modifier = Modifier
                        .weight(1f),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            keyboardController?.hide()
                            viewModel.calculate()
                        }
                    )
                )

                Spacer(modifier = Modifier.width(8.dp))

                AppDropdown(
                    label = "",
                    options = unitOptions,
                    selectedOption = viewModel.selectedThicknessUnit,
                    onOptionSelected = { unit ->
                        viewModel.onUnitSelected(unit)
                    }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ---------- Beregn-knapp ----------
            Button(
                onClick = {
                    keyboardController?.hide()
                    viewModel.calculate()
                },
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(text = "Beregn")
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ---------- Feilmelding ----------
            if (errorMessage != null) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // ---------- Resultat ----------
            Text(
                text = "Resultat",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (result != null) {
                ResultCard(result = result)
            } else {
                Text(
                    text = "Ingen beregning ennå.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
private fun ResultCard(result: OverskjaeringResult) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
            .padding(8.dp)
    ) {
        Text(
            text = "Blad: Ø${result.bladeDiameterMm} mm",
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            text = "Tykkelse: ${
                String.format(
                    "%.1f",
                    result.wallThicknessValue
                )
            } ${result.wallThicknessUnit}",
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Overkutt",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        val unit = result.overcutUnit

        val minText = result.minOvercutValue?.let {
            String.format("%.1f %s", it, unit)
        } ?: "–"

        val maxText = result.maxOvercutValue?.let {
            String.format("%.1f %s", it, unit)
        } ?: "–"

        Text(
            text = "Min overkutt (blad fullt inn): $minText",
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = "Maks overkutt (blad min. innmating): $maxText",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(12.dp))

        val holeText = result.recommendedHoleMm?.let { "Ø${it} mm" } ?: "–"

        Text(
            text = "Anbefalt borehull: $holeText",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}
