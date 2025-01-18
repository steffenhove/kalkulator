package no.steffenhove.betongkalkulator.ui.screens

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import no.steffenhove.betongkalkulator.ui.model.AppDatabase
import no.steffenhove.betongkalkulator.ui.model.CalculationEntity
import no.steffenhove.betongkalkulator.ui.repository.CalculationRepository
import no.steffenhove.betongkalkulator.ui.viewmodel.HistoryViewModel
import no.steffenhove.betongkalkulator.ui.viewmodel.HistoryViewModelFactory
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

    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Historikk", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(calculations) { calculation ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date(calculation.timestamp))
                    Text(text = "${calculation.form} - ${calculation.result} kg - $date")
                    Checkbox(
                        checked = selectedCalculations.contains(calculation),
                        onCheckedChange = { isChecked ->
                            if (isChecked) {
                                selectedCalculations.add(calculation)
                            } else {
                                selectedCalculations.remove(calculation)
                            }
                        }
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
                text = { Text("Total vekt: $totalWeight kg") },
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