package no.steffenhove.betongkalkulator.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import no.steffenhove.betongkalkulator.ui.model.HistoryDao

class HistoryViewModelFactory(private val historyDao: HistoryDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HistoryViewModel::class.java)) {
            return HistoryViewModel(historyDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}