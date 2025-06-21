package no.steffenhove.betongkalkulator.ui.utils

enum class UnitPreference(val symbol: String) {
    Millimeter("mm"),
    Centimeter("cm"),
    Meter("m"),
    Inch("inch"),
    Foot("foot");

    companion object {
        fun fromString(value: String): UnitPreference {
            return values().firstOrNull { it.symbol.equals(value, ignoreCase = true) } ?: Centimeter
        }
    }
}
