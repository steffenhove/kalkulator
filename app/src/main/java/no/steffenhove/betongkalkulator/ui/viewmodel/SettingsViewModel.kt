package no.steffenhove.betongkalkulator.ui.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SettingsViewModel : ViewModel() {
    private val _density = MutableStateFlow(2400f)
    val density: StateFlow<Float> get() = _density

    fun setDensity(newDensity: Float) {
        _density.value = newDensity
        // Save to preferences (implement this method in PreferencesHelper)
    }

    fun resetDensity() {
        _density.value = 2400f
        // Save to preferences (implement this method in PreferencesHelper)
    }
}