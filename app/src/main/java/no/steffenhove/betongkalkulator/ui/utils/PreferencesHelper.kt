package no.steffenhove.betongkalkulator.ui.utils

import android.content.Context

fun saveUnitSystemPreference(context: Context, unitSystem: String) {
    val sharedPreferences = context.getSharedPreferences("preferences", Context.MODE_PRIVATE)
    with(sharedPreferences.edit()) {
        putString("unitSystem", unitSystem)
        apply()
    }
}

fun getUnitSystemPreference(context: Context): String {
    val sharedPreferences = context.getSharedPreferences("preferences", Context.MODE_PRIVATE)
    return sharedPreferences.getString("unitSystem", "Metrisk") ?: "Metrisk"
}