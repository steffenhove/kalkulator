package no.steffenhove.betongkalkulator.ui.viewmodel

import androidx.lifecycle.*
import kotlinx.coroutines.launch
import no.steffenhove.betongkalkulator.ui.model.CalculationEntity
import no.steffenhove.betongkalkulator.ui.repository.CalculationRepository

class HistoryViewModel(private val repository: CalculationRepository) : ViewModel() {

        val allCalculations: LiveData<List<CalculationEntity>> = repository.allCalculations.asLiveData()

    fun insert(calculation: CalculationEntity) = viewModelScope.launch {
        repository.insert(calculation)
    }

    fun delete(calculation: CalculationEntity) = viewModelScope.launch {
        repository.delete(calculation)
    }

    fun deleteAll() = viewModelScope.launch {
        repository.deleteAll()
    }

    fun calculateTotalWeight(calculations: List<CalculationEntity>): Double {
        return calculations.sumOf { it.result }
    }
}