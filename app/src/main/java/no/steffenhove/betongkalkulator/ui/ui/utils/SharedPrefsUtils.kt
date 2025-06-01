package no.steffenhove.betongkalkulator.ui.utils

import android.content.Context

fun savePreference(context: Context, key: String, value: String) {
    val prefs = context.getSharedPreferences("betong_prefs", Context.MODE_PRIVATE)
    prefs.edit().putString(key, value).apply()
}

fun loadPreference(context: Context, key: String, defaultValue: String): String {
    val prefs = context.getSharedPreferences("betong_prefs", Context.MODE_PRIVATE)
    return prefs.getString(key, defaultValue) ?: defaultValue
}
