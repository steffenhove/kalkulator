package no.steffenhove.betongkalkulator.ui.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class CalculationViewModel : ViewModel() {
    private val _unit = MutableStateFlow("metric")
    val unit: StateFlow<String> get() = _unit

    private val _weightUnit = MutableStateFlow("kg")
    val weightUnit: StateFlow<String> get() = _weightUnit

    fun calculateResult(): Double {
        // Implementer kalkulasjonslogikken her
        return 0.0
    }
}