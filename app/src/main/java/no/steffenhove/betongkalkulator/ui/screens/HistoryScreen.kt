package no.steffenhove.betongkalkulator.ui.screens

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import no.steffenhove.betongkalkulator.ui.model.AppDatabase
import no.steffenhove.betongkalkulator.ui.model.CalculationEntity
import no.steffenhove.betongkalkulator.ui.repository.CalculationRepository
import no.steffenhove.betongkalkulator.ui.viewmodel.HistoryViewModel
import no.steffenhove.betongkalkulator.ui.viewmodel.HistoryViewModelFactory
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HistoryScreen(context: Context) {
    val calculationDao = AppDatabase.getDatabase(context).calculationDao()
    val repository = CalculationRepository(calculationDao)
    val factory = HistoryViewModelFactory(repository)
    val historyViewModel: HistoryViewModel = viewModel(factory = factory)

    // Observe calculations LiveData with a proper initial value
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

            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
                items(calculations) { calculation ->
                    val isSelected = selectedCalculations.contains(calculation)
                    val backgroundColor = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f) else Color.Transparent

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(backgroundColor)
                            .clickable {
                                if (selectedCalculations.isNotEmpty()) {
                                    toggleSelection(calculation, selectedCalculations)
                                }
                            }
                            .pointerInput(Unit) {
                                detectTapGestures(
                                    onLongPress = {
                                        toggleSelection(calculation, selectedCalculations)
                                    }
                                )
                            },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text(text = "${calculation.form} - ${formatWeight(calculation.result, calculation.resultUnit)}", style = MaterialTheme.typography.bodyLarge)
                            Text(
                                text = getDimensionsText(calculation.form, calculation.dimensions, calculation.thickness.toDoubleOrNull() ?: 0.0, calculation.unit),
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
            }
        }

        if (showDialog) {
            ConfirmDeleteAllDialog(
                onConfirm = {
                    scope.launch {
                        historyViewModel.deleteAll()
                    }
                    showDialog = false
                },
                onDismiss = { showDialog = false }
            )
        }

        // Show total weight dialog
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
        title = { Text(text = "Bekreft sletting") },
        text = { Text(text = "Er du sikker på at du vil slette alle beregninger?") },
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

private fun toggleSelection(calculation: CalculationEntity, selectedCalculations: MutableList<CalculationEntity>) {
    if (selectedCalculations.contains(calculation)) {
        selectedCalculations.remove(calculation)
    } else {
        selectedCalculations.add(calculation)
    }
}

private fun getDimensionsText(form: String, dimensions: String, thickness: Double, unit: String): String {
    val dims = dimensions.split(", ").map { it.toDoubleOrNull() ?: 0.0 }
    val formattedDims = dims.map { formatLargeNumber(it) }
    val formattedThickness = formatLargeNumber(thickness)

    return when (form) {
        "Kjerne" -> "${formattedDims[0]}${unit} x ${formattedThickness}${unit}"
        "Firkant" -> "${formattedDims[0]}${unit} x ${formattedDims[1]}${unit} x ${formattedThickness}${unit}"
        "Trekant" -> "${formattedDims[0]}${unit} x ${formattedDims[1]}${unit} x ${formattedDims[2]}${unit} x ${formattedThickness}${unit}"
        "Trapes" -> "${formattedDims[0]}${unit} x ${formattedDims[1]}${unit} x ${formattedDims[2]}${unit} x ${formattedDims[3]}${unit} x ${formattedThickness}${unit}"
        else -> dimensions
    }
}

private fun formatLargeNumber(number: Double): String {
    val decimalFormat = DecimalFormat("#,###")
    decimalFormat.isGroupingUsed = true
    decimalFormat.maximumFractionDigits = 0
    return decimalFormat.format(number)
}