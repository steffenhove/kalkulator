package no.steffenhove.betongkalkulator.ui.utils

import no.steffenhove.betongkalkulator.ui.components.Unit

object UnitConverter {
    fun convert(value: Float, from: Unit, to: Unit): Float {
        return when (from) {
            Unit.MM -> when (to) {
                Unit.CM -> value / 10
                Unit.M -> value / 1000
                else -> value
            }
            Unit.CM -> when (to) {
                Unit.MM -> value * 10
                Unit.M -> value / 100
                else -> value
            }
            Unit.M -> when (to) {
                Unit.MM -> value * 1000
                Unit.CM -> value * 100
                else -> value
            }
            Unit.FT -> when (to) {
                Unit.INCH -> value * 12
                Unit.YD -> value / 3
                else -> value
            }
            Unit.INCH -> when (to) {
                Unit.FT -> value / 12
                Unit.YD -> value / 36
                else -> value
            }
            Unit.YD -> when (to) {
                Unit.FT -> value * 3
                Unit.INCH -> value * 36
                else -> value
            }
            Unit.KG -> when (to) {
                Unit.LBS -> value * 2.20462f
                else -> value
            }
            Unit.LBS -> when (to) {
                Unit.KG -> value / 2.20462f
                else -> value
            }
        }
    }
}