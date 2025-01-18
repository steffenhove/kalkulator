package no.steffenhove.betongkalkulator.ui.utils

import android.content.Context
import android.content.SharedPreferences

class PreferencesHelper(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("BetongKalkulatorPrefs", Context.MODE_PRIVATE)

    fun saveDensity(density: Float) {
        sharedPreferences.edit().putFloat("density", density).apply()
    }

    fun getDensity(): Float {
        return sharedPreferences.getFloat("density", 2400f)
    }

    fun resetDensity() {
        saveDensity(2400f)
    }
}