package no.steffenhove.betongkalkulator.data

import kotlin.math.max

/**
 * Inndata-rad fra overskjæringstabellen.
 *
 * @param bladeMm      Bladdiameter i mm (f.eks. 600, 700, 900 ...)
 * @param thicknessCm  Betongtykkelse i cm som denne raden gjelder for (15, 16, 17, ...)
 * @param maxDepthCm   Maksimal skjæredybde for gitt blad, i cm.
 */
data class OverskjaeringData(
    val bladeMm: Int,
    val thicknessCm: Int,
    val maxDepthCm: Double
)

/**
 * Loader for overskjæringsdata.
 *
 * FASE 1:
 *  - Vi bruker en enkel, geometrisk modell for å beregne maks skjæredybde
 *    for hvert blad (Ø600–1600) uavhengig av veggtykkelse.
 *
 * Antakelse:
 *  - Maks innmating begrenses av:
 *      - bladradius
 *      - spindelradius (72 mm ved Ø144 mm spindel)
 *      - en liten sikkerhetsmargin
 *
 *  - Derfor: usableDepthMm ≈ (bladeMm / 2) - 72 mm - margin
 *
 * Denne modellen kan når som helst byttes ut med fabrikk-tabellverdier
 * ved å endre innholdet i loadOverskjaeringData().
 */
object OverskjaeringLoader {

    // Spindeldiameter oppgitt av deg → 144 mm → radius = 72 mm
    private const val SPINDEL_RADIUS_MM = 72.0

    // Liten sikkerhetsmargin (mm) for å ikke legge oss helt i klem
    private const val SAFETY_MARGIN_MM = 5.0

    /**
     * Lager en liste med OverskjaeringData for:
     *  - Blad: 600, 700, 750, 800, 900, 1000, 1200, 1500, 1600 mm
     *  - Tykkelser: 15–40 cm (1 cm intervall)
     *
     * Foreløpig: samme maksdybde for alle tykkelser for et gitt blad.
     * (Innmatingsbegrensningen kommer fra sag/bladdiameter, ikke veggtykkelsen.)
     */
    fun loadOverskjaeringData(): List<OverskjaeringData> {
        val blades = listOf(600, 700, 750, 800, 900, 1000, 1200, 1500, 1600)
        val thicknessRangeCm = 15..40

        val data = mutableListOf<OverskjaeringData>()

        for (blade in blades) {
            val maxDepthMm = computeMaxDepthFromGeometry(bladeMm = blade)
            val maxDepthCm = maxDepthMm / 10.0

            for (thicknessCm in thicknessRangeCm) {
                data.add(
                    OverskjaeringData(
                        bladeMm = blade,
                        thicknessCm = thicknessCm,
                        maxDepthCm = maxDepthCm
                    )
                )
            }
        }

        return data
    }

    /**
     * Enkel geometrisk modell for maksimal skjæredybde.
     *
     * radius = bladeMm / 2
     * usableDepth = radius - spindelRadius - margin
     */
    private fun computeMaxDepthFromGeometry(bladeMm: Int): Double {
        val radiusMm = bladeMm / 2.0
        val usable = radiusMm - SPINDEL_RADIUS_MM - SAFETY_MARGIN_MM
        return max(0.0, usable)
    }
}
