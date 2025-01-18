package no.steffenhove.betongkalkulator.ui.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SettingsViewModel : ViewModel() {
    private val _betongDensitet = MutableStateFlow(2400.0)
    val betongDensitet: StateFlow<Double> get() = _betongDensitet

    private val _lecaDensitet = MutableStateFlow(800.0)
    val lecaDensitet: StateFlow<Double> get() = _lecaDensitet

    private val _siporexDensitet = MutableStateFlow(600.0)
    val siporexDensitet: StateFlow<Double> get() = _siporexDensitet

    fun updateBetongDensitet(densitet: Double) {
        _betongDensitet.value = densitet
    }

    fun updateLecaDensitet(densitet: Double) {
        _lecaDensitet.value = densitet
    }

    fun updateSiporexDensitet(densitet: Double) {
        _siporexDensitet.value = densitet
    }

    fun resetToDefaultDensiteter() {
        _betongDensitet.value = 2400.0
        _lecaDensitet.value = 800.0
        _siporexDensitet.value = 600.0
    }
}