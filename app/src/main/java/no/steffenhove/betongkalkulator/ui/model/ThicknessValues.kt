// Fil: app/src/main/java/no/steffenhove/betongkalkulator/ui/model/ThicknessValues.kt
package no.steffenhove.betongkalkulator.ui.model

/**
 * Verdier for én kombinasjon av bladdiameter og betongtykkelse.
 *
 * Alle verdier er i CM.
 *
 * minCutCm  = "snill" overskjæring (cm), dvs. minste overkapp vi har fra kildene
 * maxCutCm  = "brutal" overskjæring (cm), dvs. største overkapp vi har fra kildene
 * overcutCm = "typisk"/sikker overskjæring (cm). Vi setter den lik maxCutCm
 *             slik at eksisterande kode som brukar overcutCm får den tryggaste.
 */
data class ThicknessValues(
    val minCutCm: Float,
    val maxCutCm: Float,
    val overcutCm: Float
)
