package no.steffenhove.betongkalkulator.ui.utils

import java.text.DecimalFormat

fun formatWeight(weight: Double, unit: String): String {
    val decimalFormat = DecimalFormat("#,###.##")
    val kg = decimalFormat.format(weight)

    return if (unit == "lbs") {
        val lbs = decimalFormat.format(weight * 2.20462)
        "$lbs lbs"
    } else {
        if (weight >= 1000) {
            val tons = decimalFormat.format(weight / 1000)
            "$kg kg / $tons t"
        } else {
            "$kg kg"
        }
    }
}
