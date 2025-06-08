package no.steffenhove.betongkalkulator.ui.utils

import android.content.Context

object SharedPrefsUtils {

    private const val PREFS_NAME = "betong_prefs" // Navnet på SharedPreferences-filen
    private const val KEY_UNIT_SYSTEM = "unit_system" // Nøkkel for enhetssystem

    // Funksjon for å lagre valgt enhetssystem
    fun saveUnitSystem(context: Context, unitSystem: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_UNIT_SYSTEM, unitSystem).apply()
    }

    // Funksjon for å hente valgt enhetssystem
    fun getUnitSystem(context: Context): String {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        // Returnerer "Metrisk" som standardverdi hvis ingenting er lagret
        return prefs.getString(KEY_UNIT_SYSTEM, "Metrisk") ?: "Metrisk"
    }

    // Generelle funksjoner du delte tidligere, nå inne i objektet
    fun savePreference(context: Context, key: String, value: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(key, value).apply()
    }

    fun loadPreference(context: Context, key: String, defaultValue: String): String {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(key, defaultValue) ?: defaultValue
    }
}


fun savePreference(context: Context, key: String, value: String) {

    val prefs = context.getSharedPreferences("betong_prefs", Context.MODE_PRIVATE)

    prefs.edit().putString(key, value).apply()

}



fun loadPreference(context: Context, key: String, defaultValue: String): String {

    val prefs = context.getSharedPreferences("betong_prefs", Context.MODE_PRIVATE)

    return prefs.getString(key, defaultValue) ?: defaultValue

}