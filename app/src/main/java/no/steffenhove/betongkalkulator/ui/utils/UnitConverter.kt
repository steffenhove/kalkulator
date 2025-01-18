package no.steffenhove.betongkalkulator.utils

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