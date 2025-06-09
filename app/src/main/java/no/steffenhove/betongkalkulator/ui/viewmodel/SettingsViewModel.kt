package no.steffenhove.betongkalkulator.ui.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SettingsViewModel : ViewModel() {
    private val _isDarkTheme = MutableStateFlow(false)
    val isDarkTheme: StateFlow<Boolean> get() = _isDarkTheme

    private val _unitSystem = MutableStateFlow("Metrisk")
    val unitSystem: StateFlow<String> get() = _unitSystem

    private val _language = MutableStateFlow("Norsk")
    val language: StateFlow<String> get() = _language

    private val _weightUnit = MutableStateFlow("kg")
    val weightUnit: StateFlow<String> get() = _weightUnit

    fun setDarkTheme(isDark: Boolean) {
        _isDarkTheme.value = isDark
    }

    fun setUnitSystem(unit: String) {
        _unitSystem.value = unit
    }

    fun setLanguage(lang: String) {
        _language.value = lang
    }

    fun setWeightUnit(unit: String) {
        _weightUnit.value = unit
    }
}