package no.steffenhove.betongkalkulator.ui.utils

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import no.steffenhove.betongkalkulator.ui.model.ConcreteType

private const val PREFERENCES_NAME = "betongkalkulator_preferences"
private const val UNIT_SYSTEM_KEY = "unit_system"
private const val WEIGHT_UNIT_KEY = "weight_unit"
private const val CONCRETE_TYPES_KEY = "concrete_types"

fun saveUnitSystemPreference(context: Context, unitSystem: String) {
    val sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
    with(sharedPreferences.edit()) {
        putString(UNIT_SYSTEM_KEY, unitSystem)
        apply()
    }
}

fun getUnitSystemPreference(context: Context): String {
    val sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
    return sharedPreferences.getString(UNIT_SYSTEM_KEY, "Metrisk") ?: "Metrisk"
}

fun saveWeightUnitPreference(context: Context, weightUnit: String) {
    val sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
    with(sharedPreferences.edit()) {
        putString(WEIGHT_UNIT_KEY, weightUnit)
        apply()
    }
}

fun getWeightUnitPreference(context: Context): String {
    val sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
    return sharedPreferences.getString(WEIGHT_UNIT_KEY, "kg") ?: "kg"
}

fun saveConcreteTypesPreference(context: Context, concreteTypes: List<ConcreteType>) {
    val sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
    val json = Gson().toJson(concreteTypes)
    with(sharedPreferences.edit()) {
        putString(CONCRETE_TYPES_KEY, json)
        apply()
    }
}

fun getConcreteTypesPreference(context: Context): List<ConcreteType> {
    val sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
    val json = sharedPreferences.getString(CONCRETE_TYPES_KEY, null) ?: return getDefaultConcreteTypes()
    val type = object : TypeToken<List<ConcreteType>>() {}.type
    return Gson().fromJson(json, type)
}

fun resetToDefaultPreferences(context: Context) {
    val sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
    with(sharedPreferences.edit()) {
        putString(UNIT_SYSTEM_KEY, "Metrisk")
        putString(WEIGHT_UNIT_KEY, "kg")
        putString(CONCRETE_TYPES_KEY, Gson().toJson(getDefaultConcreteTypes()))
        apply()
    }
}

fun getDefaultConcreteTypes(): List<ConcreteType> {
    return listOf(
        ConcreteType("Betong", 2400.0),
        ConcreteType("Leca", 600.0),
        ConcreteType("Siporex", 500.0)
    )
}