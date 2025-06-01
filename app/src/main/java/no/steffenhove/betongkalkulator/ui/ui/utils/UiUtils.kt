package no.steffenhove.betongkalkulator.ui.utils

import no.steffenhove.betongkalkulator.ui.model.CalculationEntity
import java.text.DecimalFormat

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
        "Kjerne" -> "Ø${formattedDims[0]}$unit x ${formattedThickness}$unit"
        "Firkant" -> "${formattedDims[0]}$unit x ${formattedDims[1]}$unit x ${formattedThickness}$unit"
        "Trekant" -> "${formattedDims[0]}$unit x ${formattedDims[1]}$unit x ${formattedDims[2]}$unit x ${formattedThickness}$unit"
        "Trapes" -> "${formattedDims[0]}$unit x ${formattedDims[1]}$unit x ${formattedDims[2]}$unit x ${formattedDims[3]}$unit x ${formattedThickness}$unit"
        else -> dimensions
    }
}

fun formatLargeNumber(number: Double): String {
    val decimalFormat = DecimalFormat("#,###")
    decimalFormat.isGroupingUsed = true
    decimalFormat.maximumFractionDigits = 0
    return decimalFormat.format(number)
}
