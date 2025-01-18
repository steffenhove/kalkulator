package no.steffenhove.betongkalkulator.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import no.steffenhove.betongkalkulator.ui.model.HistoryDao
import no.steffenhove.betongkalkulator.ui.model.HistoryEntity

class HistoryViewModel(private val historyDao: HistoryDao) : ViewModel() {
    private val _historyList = MutableStateFlow<List<HistoryEntity>>(emptyList())
    val historyList: StateFlow<List<HistoryEntity>> get() = _historyList

    init {
        viewModelScope.launch {
            _historyList.value = historyDao.getAllHistory()
        }
    }

    fun toggleSelection(historyItem: HistoryEntity) {
        viewModelScope.launch {
            val updatedList = _historyList.value.map {
                if (it.id == historyItem.id) it.copy(isSelected = !it.isSelected) else it
            }
            _historyList.value = updatedList
        }
    }

    fun deleteSelectedHistory() {
        viewModelScope.launch {
            historyDao.deleteSelectedHistory()
            _historyList.value = historyDao.getAllHistory()
        }
    }
}