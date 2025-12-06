@file:OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)

package no.steffenhove.betongkalkulator.ui.screens

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import no.steffenhove.betongkalkulator.ui.model.AppDatabase
import no.steffenhove.betongkalkulator.ui.model.CalculationEntity

@Composable
fun HistoryScreen(
    context: Context = LocalContext.current,
    navigateBack: () -> Unit
) {
    val dao = remember { AppDatabase.getDatabase(context).calculationDao() }
    val calculations by dao.getAllCalculations().collectAsState(initial = emptyList())

    var selectedItems by remember { mutableStateOf(setOf<CalculationEntity>()) }
    val anySelected = selectedItems.isNotEmpty()

    var showSum by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    fun toggleSelection(item: CalculationEntity) {
        selectedItems = if (selectedItems.contains(item)) {
            selectedItems - item
        } else {
            selectedItems + item
        }
    }

    fun clearSelection() {
        selectedItems = emptySet()
    }

    fun currentItemsForSum(): List<CalculationEntity> =
        if (anySelected) selectedItems.toList() else calculations

    fun formatWeight(value: Double, unit: String): String {
        val df = java.text.DecimalFormat("#,###.##")
        val main = df.format(value)
        return if (unit == "kg" && value >= 1000.0) {
            val tons = df.format(value / 1000.0)
            "$main kg ($tons t)"
        } else {
            "$main $unit"
        }
    }

    fun buildShareMessage(): String {
        val itemsToShare = if (anySelected) selectedItems else calculations.toSet()
        if (itemsToShare.isEmpty()) return ""

        val sb = StringBuilder()
        sb.appendLine("Betongkalkulator – historikk")
        sb.appendLine()

        itemsToShare.forEach { calc ->
            sb.appendLine("${calc.form} – ${calc.concreteType}")
            sb.appendLine("Resultat: ${formatWeight(calc.result, calc.resultUnit)}")
            sb.appendLine()
        }

        // Summer per enhet
        val sumsByUnit = itemsToShare
            .groupBy { it.resultUnit }
            .mapValues { entry -> entry.value.sumOf { it.result } }

        if (sumsByUnit.isNotEmpty()) {
            sb.appendLine("Summer:")
            sumsByUnit.forEach { (unit, sum) ->
                sb.appendLine(formatWeight(sum, unit))
            }
        }

        return sb.toString().trim()
    }

    val sumsByUnit: Map<String, Double> by derivedStateOf {
        val list = currentItemsForSum()
        list.groupBy { it.resultUnit }
            .mapValues { entry -> entry.value.sumOf { it.result } }
    }

    // SLETTEDIALOG
    if (showDeleteDialog) {
        val title = if (anySelected) "Slett valgte linjer?" else "Slett alle linjer?"
        val message = if (anySelected) {
            "Er du sikker på at du vil slette de valgte beregningene? Dette kan ikke angres."
        } else {
            "Er du sikker på at du vil slette hele historikken? Dette kan ikke angres."
        }

        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(title) },
            text = { Text(message) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        scope.launch {
                            if (anySelected) {
                                selectedItems.forEach { dao.deleteCalculation(it) }
                            } else {
                                dao.deleteAllCalculations()
                            }
                            clearSelection()
                        }
                    }
                ) {
                    Text("Ja, slett")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Avbryt")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        // Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        ) {
            IconButton(onClick = navigateBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Tilbake"
                )
            }
            Text(
                text = "Historikk",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        // Action-knapper
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        ) {
            Button(
                onClick = {
                    val msg = buildShareMessage()
                    if (msg.isNotBlank()) {
                        val sendIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, msg)
                        }
                        context.startActivity(
                            Intent.createChooser(
                                sendIntent,
                                "Del historikk via"
                            )
                        )
                    }
                },
                enabled = calculations.isNotEmpty()
            ) {
                Text(if (anySelected) "Del valgte" else "Del alle")
            }

            Button(
                onClick = {
                    // I stedet for å slette direkte: vis dialog
                    if (calculations.isNotEmpty()) {
                        showDeleteDialog = true
                    }
                },
                enabled = calculations.isNotEmpty()
            ) {
                Text(if (anySelected) "Slett valgte" else "Slett alle")
            }

            Button(
                onClick = { showSum = !showSum },
                enabled = calculations.isNotEmpty()
            ) {
                Text("Sum")
            }
        }

        if (showSum && sumsByUnit.isNotEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            ) {
                Text(
                    text = if (anySelected) "Sum (valgte linjer):" else "Sum (alle linjer):",
                    style = MaterialTheme.typography.titleMedium
                )
                sumsByUnit.forEach { (unit, sum) ->
                    Text(
                        text = "• ${formatWeight(sum, unit)}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        if (calculations.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Ingen beregninger lagret ennå.")
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(calculations) { calc ->
                    val isSelected = selectedItems.contains(calc)

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .combinedClickable(
                                onClick = {
                                    // Hvis vi allerede er i markeringsmodus, toggle ved klikk
                                    if (anySelected) {
                                        toggleSelection(calc)
                                    }
                                },
                                onLongClick = {
                                    // Long-press starter/utvider markering
                                    toggleSelection(calc)
                                }
                            ),
                        colors = if (isSelected) {
                            CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        } else {
                            CardDefaults.cardColors()
                        }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = "${calc.form} – ${calc.concreteType}",
                                    style = MaterialTheme.typography.titleMedium,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Resultat: ${formatWeight(calc.result, calc.resultUnit)}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Enhet: ${calc.unit}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Filled.Check,
                                    contentDescription = "Markert",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
