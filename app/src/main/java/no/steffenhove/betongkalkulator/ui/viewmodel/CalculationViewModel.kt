package no.steffenhove.betongkalkulator.ui.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import no.steffenhove.betongkalkulator.ui.components.Unit

class CalculationViewModel : ViewModel() {
    private val _result = MutableStateFlow(0f)
    val result: StateFlow<Float> get() = _result

    private val _selectedUnit = MutableStateFlow(Unit.MM)
    val selectedUnit: StateFlow<Unit> get() = _selectedUnit

    private val _selectedWeightUnit = MutableStateFlow(Unit.KG)
    val selectedWeightUnit: StateFlow<Unit> get() = _selectedWeightUnit

    fun setSelectedUnit(unit: Unit) {
        _selectedUnit.value = unit
    }

    fun calculate(dimensions: Float, weight: Float) {
        // Implementer den faktiske beregningslogikken her
        // Eksempelberegning (erstatt med din egen logikk)
        _result.value = dimensions * weight
    }
}