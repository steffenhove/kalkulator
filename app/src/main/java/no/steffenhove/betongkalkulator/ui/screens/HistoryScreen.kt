package no.steffenhove.betongkalkulator.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import no.steffenhove.betongkalkulator.ui.viewmodel.HistoryViewModel
import no.steffenhove.betongkalkulator.ui.model.HistoryEntity

@Composable
fun HistoryScreen(viewModel: HistoryViewModel) {
    val historyList by viewModel.historyList.collectAsState()
    var showDialog by remember { mutableStateOf(false) }

    LazyColumn {
        itemsIndexed(historyList) { index, historyItem ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        // Merk linjen ved klikk (trykk)
                        viewModel.toggleSelection(historyItem)
                    }
                    .background(if (historyItem.isSelected) Color.LightGray else Color.Transparent)
                    .padding(16.dp)
            ) {
                Column {
                    Text(text = historyItem.calculationDetails)
                    Text(text = "${historyItem.date} ${historyItem.time}", fontSize = 12.sp, color = Color.Gray)
                }
            }
        }
    }

    Button(onClick = {
        if (historyList.any { it.isSelected }) {
            showDialog = true // Vis bekreftelsesdialog
        }
    }) {
        Text("Slett valgte")
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Bekreft sletting") },
            text = { Text("Er du sikker på at du vil slette de valgte elementene?") },
            confirmButton = {
                Button(onClick = {
                    viewModel.deleteSelectedHistory()
                    showDialog = false
                }) {
                    Text("Ja")
                }
            },
            dismissButton = {
                Button(onClick = { showDialog = false }) {
                    Text("Nei")
                }
            }
        )
    }
}