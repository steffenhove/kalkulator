package no.steffenhove.betongkalkulator.ui.utils

import android.content.Context
import android.preference.PreferenceManager

fun getUnitSystem(context: Context): String {
    val prefs = PreferenceManager.getDefaultSharedPreferences(context)
    return prefs.getString("unit_system", "Metrisk") ?: "Metrisk"
}
