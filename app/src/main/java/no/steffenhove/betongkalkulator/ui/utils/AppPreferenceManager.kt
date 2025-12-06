package no.steffenhove.betongkalkulator.ui.utils

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import no.steffenhove.betongkalkulator.ui.model.ConcreteType
import no.steffenhove.betongkalkulator.ui.utils.UnitPreference

object AppPreferenceManager {

    private const val PREFERENCES_NAME = "betongkalkulator_preferences"

    private const val UNIT_SYSTEM_KEY = "unit_system"
    private const val WEIGHT_UNIT_KEY = "weight_unit"
    private const val CONCRETE_TYPES_KEY = "concrete_types"

    private const val SELECTED_FORM_KEY = "selected_form"
    private const val SELECTED_UNIT_KEY = "selected_unit"
    private const val SELECTED_CONCRETE_TYPE_KEY = "selected_concrete_type"

    private const val LIFT_FORM_KEY = "lift_last_form"
    private const val LIFT_UNIT_KEY = "lift_last_unit"
    private const val LIFT_COUNT_KEY = "lift_last_feste_count"

    private const val FESTE_UNIT_KEY = "festepunkt_unit"

    private const val OVERSKJAERING_UNIT_KEY = "overskjaering_unit"
    private const val OVERSKJAERING_BLADE_KEY = "overskjaering_blade"

    private fun prefs(context: Context) = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    fun savePreference(context: Context, key: String, value: String) {
        prefs(context).edit().putString(key, value).apply()
    }

    fun loadPreference(context: Context, key: String, defaultValue: String): String {
        return prefs(context).getString(key, defaultValue) ?: defaultValue
    }

    fun getUnitSystemPreference(context: Context): String =
        loadPreference(context, UNIT_SYSTEM_KEY, "Metrisk")

    fun saveUnitSystemPreference(context: Context, unitSystem: String) =
        savePreference(context, UNIT_SYSTEM_KEY, unitSystem)

    fun getWeightUnitPreference(context: Context): String =
        loadPreference(context, WEIGHT_UNIT_KEY, "kg")

    fun saveWeightUnitPreference(context: Context, weightUnit: String) =
        savePreference(context, WEIGHT_UNIT_KEY, weightUnit)

    fun getLengthUnitPreference(context: Context): String =
        loadPreference(context, SELECTED_UNIT_KEY, "cm")

    fun getConcreteTypesPreference(context: Context): List<ConcreteType> {
        return try {
            val json = loadPreference(context, CONCRETE_TYPES_KEY, "")
            if (json.isNotEmpty()) {
                val type = object : TypeToken<List<ConcreteType>>() {}.type
                Gson().fromJson(json, type)
            } else getDefaultConcreteTypes()
        } catch (e: Exception) {
            getDefaultConcreteTypes()
        }
    }

    fun saveConcreteTypesPreference(context: Context, types: List<ConcreteType>) =
        savePreference(context, CONCRETE_TYPES_KEY, Gson().toJson(types))

    fun getDefaultConcreteTypes(): List<ConcreteType> = listOf(
        ConcreteType("Betong", 2400.0),
        ConcreteType("Leca", 800.0),
        ConcreteType("Siporex", 600.0),
        ConcreteType("Asfalt", 2300.0),
        ConcreteType("Egendefinert", 0.0)
    )

    fun resetToDefaultPreferences(context: Context) {
        saveUnitSystemPreference(context, "Metrisk")
        saveWeightUnitPreference(context, "kg")
        saveConcreteTypesPreference(context, getDefaultConcreteTypes())
    }

    fun getLastCalculatorPreferences(context: Context): Triple<String, String, String> {
        val form = loadPreference(context, SELECTED_FORM_KEY, "Firkant")
        val unit = loadPreference(context, SELECTED_UNIT_KEY, "cm")
        val type = loadPreference(context, SELECTED_CONCRETE_TYPE_KEY, "Betong")
        return Triple(form, unit, type)
    }

    fun saveLastCalculatorPreferences(context: Context, form: String, unit: String, type: String) {
        savePreference(context, SELECTED_FORM_KEY, form)
        savePreference(context, SELECTED_UNIT_KEY, unit)
        savePreference(context, SELECTED_CONCRETE_TYPE_KEY, type)
    }

    fun getLastLiftPreferences(context: Context): Triple<String, String, Int> {
        val form = loadPreference(context, LIFT_FORM_KEY, "Firkant")
        val unit = loadPreference(context, LIFT_UNIT_KEY, "cm")
        val count = prefs(context).getInt(LIFT_COUNT_KEY, 4)
        return Triple(form, unit, count)
    }

    fun saveLastLiftPreferences(context: Context, form: String, unit: String, count: Int) {
        prefs(context).edit()
            .putString(LIFT_FORM_KEY, form)
            .putString(LIFT_UNIT_KEY, unit)
            .putInt(LIFT_COUNT_KEY, count)
            .apply()
    }

    fun getLastFestepunktUnit(context: Context): String =
        loadPreference(context, FESTE_UNIT_KEY, "cm")

    fun saveLastFestepunktUnit(context: Context, unit: String) =
        savePreference(context, FESTE_UNIT_KEY, unit)

    fun getLastOverskjaeringUnit(context: Context): String =
        loadPreference(context, OVERSKJAERING_UNIT_KEY, "cm")

    fun saveLastOverskjaeringUnit(context: Context, unit: String) =
        savePreference(context, OVERSKJAERING_UNIT_KEY, unit)

    fun getLastOverskjaeringBlade(context: Context): Int =
        prefs(context).getInt(OVERSKJAERING_BLADE_KEY, 800)

    fun saveLastOverskjaeringBlade(context: Context, bladeSize: Int) {
        prefs(context).edit().putInt(OVERSKJAERING_BLADE_KEY, bladeSize).apply()
    }

    fun getLastUsedValues(context: Context, screenKey: String): Triple<String, String, String> {
        val form = loadPreference(context, "${screenKey}_form", "")
        val unit = loadPreference(context, "${screenKey}_unit", "")
        val type = loadPreference(context, "${screenKey}_type", "")
        return Triple(form, unit, type)
    }

    fun saveLastUsedValues(context: Context, screenKey: String, form: String, unit: String, type: String = "") {
        savePreference(context, "${screenKey}_form", form)
        savePreference(context, "${screenKey}_unit", unit)
        savePreference(context, "${screenKey}_type", type)
    }

    fun getUnitPreference(context: Context): UnitPreference {
        val unit = loadPreference(context, SELECTED_UNIT_KEY, "cm")
        return UnitPreference.fromString(unit)
    }
}
