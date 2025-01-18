package no.steffenhove.betongkalkulator.ui.utils

object UnitConverter {
    fun convertToMeters(value: Double, unit: String): Double {
        return when (unit) {
            "mm" -> value / 1000
            "cm" -> value / 100
            "m" -> value
            "ft" -> value * 0.3048
            "inch" -> value * 0.0254
            "yard" -> value * 0.9144
            else -> value
        }
    }

    fun convertFromMeters(value: Double, unit: String): Double {
        return when (unit) {
            "mm" -> value * 1000
            "cm" -> value * 100
            "m" -> value
            "ft" -> value / 0.3048
            "inch" -> value / 0.0254
            "yard" -> value / 0.9144
            else -> value
        }
    }
}