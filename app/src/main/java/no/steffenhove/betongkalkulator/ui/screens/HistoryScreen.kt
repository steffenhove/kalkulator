@file:OptIn(ExperimentalMaterial3Api::class)

package no.steffenhove.betongkalkulator.ui.screens

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import no.steffenhove.betongkalkulator.ui.model.AppDatabase
import no.steffenhove.betongkalkulator.ui.model.CalculationEntity
import no.steffenhove.betongkalkulator.ui.repository.CalculationRepository
import no.steffenhove.betongkalkulator.ui.utils.formatWeight
import no.steffenhove.betongkalkulator.ui.utils.getDimensionsText
import no.steffenhove.betongkalkulator.ui.utils.toggleSelection
import no.steffenhove.betongkalkulator.ui.viewmodel.HistoryViewModel
import no.steffenhove.betongkalkulator.ui.viewmodel.HistoryViewModelFactory
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@Composable
fun HistoryScreen(context: Context) {
    val navContext = LocalContext.current
    val calculationDao = AppDatabase.getDatabase(context).calculationDao()
    val repository = CalculationRepository(calculationDao)
    val factory = HistoryViewModelFactory(repository)
    val historyViewModel: HistoryViewModel = viewModel(factory = factory)
    val calculations by historyViewModel.allCalculations.observeAsState(initial = emptyList())
    val selectedCalculations = remember { mutableStateListOf<CalculationEntity>() }
    val scope = rememberCoroutineScope()
    var showDialog by remember { mutableStateOf(false) }
    var showSumDialog by remember { mutableStateOf(false) }
    var totalWeight by remember { mutableStateOf(0.0) }

    Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Column(modifier = Modifier.fillMaxSize()) {
            Text(text = "Historikk", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(modifier = Modifier.weight(1f)) {
                items(calculations) { calculation ->
                    val isSelected = selectedCalculations.contains(calculation)
                    val backgroundColor = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f) else Color.Transparent

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(backgroundColor)
                            .clickable {
                                toggleSelection(calculation, selectedCalculations)
                            }
                            .pointerInput(Unit) {
                                detectTapGestures(
                                    onLongPress = {
                                        toggleSelection(calculation, selectedCalculations)
                                    }
                                )
                            }
                            .padding(8.dp)
                    ) {
                        SelectionContainer {
                            Text(
                                text = "${calculation.form} - ${formatWeight(calculation.result, calculation.resultUnit)}",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                        Text(
                            text = getDimensionsText(
                                calculation.form,
                                calculation.dimensions,
                                calculation.thickness.toDoubleOrNull() ?: 0.0,
                                calculation.unit
                            ),
                            style = MaterialTheme.typography.bodyMedium
                        )
                        val date = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()).format(Date(calculation.timestamp))
                        Text(
                            text = date,
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Light
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(onClick = {
                    scope.launch {
                        selectedCalculations.forEach { historyViewModel.delete(it) }
                        selectedCalculations.clear()
                    }
                }) {
                    Text("Slett valgte")
                }

                Button(onClick = { showDialog = true }) {
                    Text("Slett alle")
                }

                Button(onClick = {
                    totalWeight = historyViewModel.calculateTotalWeight(selectedCalculations)
                    showSumDialog = true
                }) {
                    Text("Summer vekt")
                }

                if (selectedCalculations.isNotEmpty()) {
                    Button(onClick = {
                        val message = selectedCalculations.joinToString("\n\n") { calc ->
                            val formatted = DecimalFormat("#,###.##").format(calc.result)
                            val tons = if (calc.resultUnit == "kg" && calc.result >= 1000)
                                " / ${DecimalFormat("#,###.##").format(calc.result / 1000)} t" else ""
                            """
                            ${calc.concreteType}
                            ${getDimensionsText(calc.form, calc.dimensions, calc.thickness.toDoubleOrNull() ?: 0.0, calc.unit)}
                            $formatted ${calc.resultUnit}$tons
                            """.trimIndent()
                        } +
                                "\n\nTotal vekt: ${DecimalFormat("#,###.##").format(selectedCalculations.sumOf { it.result })} kg"

                        val intent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, message)
                        }
                        navContext.startActivity(Intent.createChooser(intent, "Del kalkulasjoner via"))
                    }) {
                        Text("Del valgte")
                    }
                }
            }
        }

        if (showDialog) {
            ConfirmDeleteAllDialog(
                onConfirm = {
                    scope.launch { historyViewModel.deleteAll() }
                    showDialog = false
                },
                onDismiss = { showDialog = false }
            )
        }

        if (showSumDialog) {
            AlertDialog(
                onDismissRequest = { showSumDialog = false },
                title = { Text("Total vekt") },
                text = { Text("Total vekt: ${formatWeight(totalWeight, "kg")}") },
                confirmButton = {
                    Button(onClick = { showSumDialog = false }) {
                        Text("OK")
                    }
                }
            )
        }
    }
}

@Composable
fun ConfirmDeleteAllDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Bekreft sletting") },
        text = { Text("Er du sikker på at du vil slette alle beregninger?") },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("Ja")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Nei")
            }
        }
    )
}
