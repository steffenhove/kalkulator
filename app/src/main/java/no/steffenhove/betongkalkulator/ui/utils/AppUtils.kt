package no.steffenhove.betongkalkulator.ui.utils

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import no.steffenhove.betongkalkulator.ui.model.CalculationEntity
import no.steffenhove.betongkalkulator.ui.model.ConcreteType
import no.steffenhove.betongkalkulator.ui.model.OverskjaeringData
import no.steffenhove.betongkalkulator.ui.model.OverskjaeringEntry
import no.steffenhove.betongkalkulator.ui.model.ThicknessValues
import java.text.DecimalFormat

// --- KONSTANTER ---
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

// --- PREFERANSER ---
fun savePreference(context: Context, key: String, value: String) {
    context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE).edit().putString(key, value).apply()
}

fun loadPreference(context: Context, key: String, defaultValue: String): String {
    return context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE).getString(key, defaultValue) ?: defaultValue
}

fun getUnitSystemPreference(context: Context): String {
    return loadPreference(context, UNIT_SYSTEM_KEY, "Metrisk")
}

fun saveUnitSystemPreference(context: Context, unitSystem: String) {
    savePreference(context, UNIT_SYSTEM_KEY, unitSystem)
}

fun getWeightUnitPreference(context: Context): String {
    return loadPreference(context, WEIGHT_UNIT_KEY, "kg")
}

fun saveWeightUnitPreference(context: Context, weightUnit: String) {
    savePreference(context, WEIGHT_UNIT_KEY, weightUnit)
}

fun getConcreteTypesPreference(context: Context): List<ConcreteType> {
    val json = loadPreference(context, CONCRETE_TYPES_KEY, "")
    return if (json.isNotEmpty()) {
        val type = object : TypeToken<List<ConcreteType>>() {}.type
        Gson().fromJson(json, type)
    } else {
        getDefaultConcreteTypes()
    }
}

fun saveConcreteTypesPreference(context: Context, concreteTypes: List<ConcreteType>) {
    savePreference(context, CONCRETE_TYPES_KEY, Gson().toJson(concreteTypes))
}

fun getDefaultConcreteTypes(): List<ConcreteType> {
    return listOf(
        ConcreteType("Betong", 2400.0),
        ConcreteType("Leca", 800.0),
        ConcreteType("Siporex", 600.0)
    )
}

fun resetToDefaultPreferences(context: Context) {
    saveUnitSystemPreference(context, "Metrisk")
    saveWeightUnitPreference(context, "kg")
    saveConcreteTypesPreference(context, getDefaultConcreteTypes())
}

// --- KALKULATORVALG ---
fun saveLastCalculatorPreferences(context: Context, form: String, unit: String, concreteType: String) {
    context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE).edit()
        .putString(SELECTED_FORM_KEY, form)
        .putString(SELECTED_UNIT_KEY, unit)
        .putString(SELECTED_CONCRETE_TYPE_KEY, concreteType)
        .apply()
}

fun getLastCalculatorPreferences(context: Context): Triple<String, String, String> {
    val prefs = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
    val form = prefs.getString(SELECTED_FORM_KEY, "Kjerne") ?: "Kjerne"
    val unit = prefs.getString(SELECTED_UNIT_KEY, "cm") ?: "cm"
    val concreteType = prefs.getString(SELECTED_CONCRETE_TYPE_KEY, "Betong") ?: "Betong"
    return Triple(form, unit, concreteType)
}

// --- LØFTEPUNKT ---
fun saveLiftPreferences(context: Context, form: String, unit: String, feste: Int) {
    context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE).edit()
        .putString(LIFT_FORM_KEY, form)
        .putString(LIFT_UNIT_KEY, unit)
        .putInt(LIFT_COUNT_KEY, feste)
        .apply()
}

fun loadLiftPreferences(context: Context): Triple<String, String, Int> {
    val prefs = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
    val form = prefs.getString(LIFT_FORM_KEY, "Firkant") ?: "Firkant"
    val unit = prefs.getString(LIFT_UNIT_KEY, "cm") ?: "cm"
    val feste = prefs.getInt(LIFT_COUNT_KEY, 4)
    return Triple(form, unit, feste)
}

// --- OVERKUTT-DATA ---
fun loadOverskjaeringData(context: Context): List<OverskjaeringData> {
    val TAG = "OverskjæringLoaderDebug"
    return try {
        val jsonText = context.assets.open("overskjaering_data.json").bufferedReader().use { it.readText() }
        val gson = Gson()
        val entryListType = object : TypeToken<List<OverskjaeringEntry>>() {}.type
        val entries: List<OverskjaeringEntry> = gson.fromJson(jsonText, entryListType)

        val groupedByBladeSize = entries.groupBy { it.bladeSize }

        val overskjaeringList = groupedByBladeSize.map { (bladeSize, entryList) ->
            val dataMap = entryList.associate { entry ->
                entry.thicknessCm to ThicknessValues(
                    minCutCm = entry.minCuttingDepthCm,
                    maxCutCm = entry.maxOvercutCm,
                    overcutCm = entry.maxOvercutCm
                )
            }
            OverskjaeringData(bladeSize, dataMap)
        }

        Log.d(TAG, "Laste ferdig. Antall bladstørrelser: ${overskjaeringList.size}")
        return overskjaeringList
    } catch (e: Exception) {
        Log.e(TAG, "Feil ved lasting av overskjæringsdata:", e)
        return emptyList()
    }
}

// --- HJELPEFUNKSJONER ---
fun convertToMeters(input: String, fromUnit: String): Double? {
    val value = input.trim().replace(',', '.').toDoubleOrNull() ?: return null
    return when (fromUnit) {
        "mm" -> value / 1000.0
        "cm" -> value / 100.0
        "m" -> value
        "inch" -> value * 0.0254
        "foot" -> value * 0.3048
        else -> null
    }
}

fun toggleSelection(calculation: CalculationEntity, selected: MutableList<CalculationEntity>) {
    if (selected.contains(calculation)) {
        selected.remove(calculation)
    } else {
        selected.add(calculation)
    }
}

fun getDimensionsText(form: String, dimensions: String, thickness: Double, unit: String): String {
    val dims = dimensions.split(", ").mapNotNull { it.toDoubleOrNull() }
    val formattedDims = dims.map { formatLargeNumber(it) }
    val formattedThickness = formatLargeNumber(thickness)

    return when (form) {
        "Kjerne" -> "Ø${formattedDims.getOrNull(0)}$unit x ${formattedThickness}$unit"
        "Firkant" -> "${formattedDims.getOrNull(0)}$unit x ${formattedDims.getOrNull(1)}$unit x ${formattedThickness}$unit"
        "Trekant" -> "${formattedDims.getOrNull(0)}$unit x ${formattedDims.getOrNull(1)}$unit x ${formattedDims.getOrNull(2)}$unit x ${formattedThickness}$unit"
        "Trapes" -> "${formattedDims.getOrNull(0)}$unit x ${formattedDims.getOrNull(1)}$unit x ${formattedDims.getOrNull(2)}$unit x ${formattedDims.getOrNull(3)}$unit x ${formattedThickness}$unit"
        else -> dimensions
    }
}

fun formatLargeNumber(number: Double): String {
    val decimalFormat = DecimalFormat("#,###.##")
    return decimalFormat.format(number)
}

fun formatWeight(weight: Double, unit: String): String {
    val decimalFormat = DecimalFormat("#,###.##")
    return if (unit.equals("lbs", ignoreCase = true)) {
        val lbs = decimalFormat.format(weight * 2.20462)
        "$lbs lbs"
    } else {
        val kg = decimalFormat.format(weight)
        if (weight >= 1000) {
            val tons = decimalFormat.format(weight / 1000)
            "$kg kg / $tons t"
        } else {
            "$kg kg"
        }
    }
}

fun getUnitOptions(system: String): List<String> {
    return if (system == "Imperialsk") listOf("inch", "foot") else listOf("mm", "cm", "m")
}
