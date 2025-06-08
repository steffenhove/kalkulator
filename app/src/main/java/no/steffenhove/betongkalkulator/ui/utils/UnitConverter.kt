package no.steffenhove.betongkalkulator.ui.utils

object UnitConverter {
    private val conversionRates = mapOf(
        "mm" to 0.001,
        "cm" to 0.01,
        "m" to 1.0,
        "ft" to 0.3048,
        "inch" to 0.0254
    )

    fun convert(value: Double, fromUnit: String, toUnit: String): Double {
        val fromRate = conversionRates[fromUnit] ?: error("Unknown unit: $fromUnit")
        val toRate = conversionRates[toUnit] ?: error("Unknown unit: $toUnit")
        return value * fromRate / toRate
    }
}
fun convertToMeters(input: String, fromUnit: String): Double? {
    val value = input.replace(",", ".").toDoubleOrNull() ?: return null
    return when (fromUnit) {
        "mm" -> value / 1000
        "cm" -> value / 100
        "m" -> value
        "inch" -> value * 0.0254
        "foot" -> value * 0.3048
        else -> null
    }
}
