package no.steffenhove.betongkalkulator.ui.viewmodel

import kotlin.math.abs

/**
 * Ren kalkulator for overskjæring.
 *
 * Bruker tabeller i MILLIMETER (mm) og bilineær interpolasjon
 * mellom tykkelser og bladdiametre.
 *
 * Returnerer:
 *   Pair(minOverkuttMm, maxOverkuttMm)
 */
class OverskjaeringCalculator {

    // --------------------------------------------------------------------
    //  TABELLER – ALLE TALL I MM
    //  Du fyller inn verdiene fra Excel her.
    //  NØKKEL: betongTykkelseMm -> (bladMm -> overkuttMm)
    // --------------------------------------------------------------------

    // Industridiamant – MAKSIMAL gjennomskjæring (overkutt) [mm]
    private val maxCutIndustridiamant: Map<Int, Map<Int, Int>> = mapOf(
        // Eksempelrad (fyll ut resten etter Excel):
        // 50 mm betong:
        // 50 to mapOf(
        //     500 to 20,
        //     600 to 20,
        //     750 to 20,
        //     800 to 20,
        //     900 to 10,
        //     1000 to 10
        // ),
    )

    // Industridiamant – MINIMAL gjennomskjæring (overkutt) [mm]
    private val minCutIndustridiamant: Map<Int, Map<Int, Int>> = mapOf(
        // Fyll inn fra høyre del av Industridiamant-arket
    )

    // Tyrolitt – MAKSIMAL gjennomskjæring (overkutt) [mm]
    private val maxCutTyrolitt: Map<Int, Map<Int, Int>> = mapOf(
        // Fyll inn fra venstre del av Tyrolitt-arket (alle tall i mm)
    )

    // Tyrolitt – MINIMAL gjennomskjæring (overkutt) [mm]
    private val minCutTyrolitt: Map<Int, Map<Int, Int>> = mapOf(
        // Fyll inn fra høyre del av Tyrolitt-arket (alle tall i mm)
    )

    // Hilti – MAKSIMAL gjennomskjæring (overkutt) [mm]
    // (Hilti-arket du viste har kun "maksimal gjennomskjæring")
    private val maxCutHilti: Map<Int, Map<Int, Int>> = mapOf(
        // Fyll inn fra Hilti-arket (alle tall i mm)
    )

    // Hvis du en dag får "min"-tabell fra Hilti, kan du legge den til her
    private val minCutHilti: Map<Int, Map<Int, Int>> = emptyMap()

    // Samle tabellene i lister for "worst case"
    private val maxCutTables: List<Map<Int, Map<Int, Int>>> =
        listOf(maxCutIndustridiamant, maxCutTyrolitt, maxCutHilti)

    private val minCutTables: List<Map<Int, Map<Int, Int>>> =
        listOf(minCutIndustridiamant, minCutTyrolitt, minCutHilti)

    /**
     * Hovedfunksjon:
     *
     * @param bladeDiameterMm bladdiameter i mm (f.eks. 650, 750, 1000, 1600)
     * @param wallThicknessMm betongtykkelse i mm
     *
     * @return Pair(minOverkuttMm, maxOverkuttMm)
     */
    fun calculate(
        bladeDiameterMm: Int,
        wallThicknessMm: Double
    ): Pair<Double?, Double?> {
        if (wallThicknessMm <= 0.0) return Pair(null, null)

        val minOver = lookupWorstOvercut(
            tables = minCutTables,
            thicknessMm = wallThicknessMm,
            bladeMm = bladeDiameterMm
        )

        val maxOver = lookupWorstOvercut(
            tables = maxCutTables,
            thicknessMm = wallThicknessMm,
            bladeMm = bladeDiameterMm
        )

        return Pair(minOver, maxOver)
    }

    // --------------------------------------------------------------------
    //  "Worst case" oppslag – tar maks verdi på tvers av alle tabeller
    // --------------------------------------------------------------------

    private fun lookupWorstOvercut(
        tables: List<Map<Int, Map<Int, Int>>>,
        thicknessMm: Double,
        bladeMm: Int
    ): Double? {
        var best: Double? = null

        for (table in tables) {
            if (table.isEmpty()) continue

            val value = lookupOvercutInSingleTable(
                table = table,
                thicknessMm = thicknessMm,
                bladeMm = bladeMm
            )

            if (value != null) {
                if (best == null || value > best!!) {
                    best = value
                }
            }
        }

        return best
    }

    // --------------------------------------------------------------------
    //  Interpolasjon i EN tabell
    // --------------------------------------------------------------------

    private fun lookupOvercutInSingleTable(
        table: Map<Int, Map<Int, Int>>,
        thicknessMm: Double,
        bladeMm: Int
    ): Double? {
        if (table.isEmpty()) return null

        val thicknessKeys = table.keys.sorted()
        val t = thicknessMm

        // Finn nærmeste tykkelser over/under
        val tLow = thicknessKeys.lastOrNull { it <= t } ?: thicknessKeys.first()
        val tHigh = thicknessKeys.firstOrNull { it >= t } ?: thicknessKeys.last()

        fun valueAtThickness(tKey: Int): Double? {
            val row = table[tKey] ?: return null
            val bladeKeys = row.keys.sorted()
            if (bladeKeys.isEmpty()) return null

            val b = bladeMm
            val bLow = bladeKeys.lastOrNull { it <= b } ?: bladeKeys.first()
            val bHigh = bladeKeys.firstOrNull { it >= b } ?: bladeKeys.last()

            val vLow = row[bLow] ?: return null
            val vHigh = row[bHigh] ?: vLow

            return if (bHigh == bLow) {
                vLow.toDouble()
            } else {
                lerp(
                    x = b.toDouble(),
                    x0 = bLow.toDouble(),
                    x1 = bHigh.toDouble(),
                    y0 = vLow.toDouble(),
                    y1 = vHigh.toDouble()
                )
            }
        }

        val vLow = valueAtThickness(tLow) ?: return null
        val vHigh = valueAtThickness(tHigh) ?: vLow

        return if (tHigh == tLow) {
            vLow
        } else {
            lerp(
                x = t,
                x0 = tLow.toDouble(),
                x1 = tHigh.toDouble(),
                y0 = vLow,
                y1 = vHigh
            )
        }
    }

    private fun lerp(
        x: Double,
        x0: Double,
        x1: Double,
        y0: Double,
        y1: Double
    ): Double {
        if (abs(x1 - x0) < 1e-9) return y0
        val ratio = (x - x0) / (x1 - x0)
        return y0 + (y1 - y0) * ratio
    }
}
