package no.steffenhove.betongkalkulator.data

import no.steffenhove.betongkalkulator.ui.model.ThicknessValues
import no.steffenhove.betongkalkulator.ui.model.OverskjaeringData

object OverskjaeringLoader {
    fun loadOverskjaeringData(): List<OverskjaeringData> {
        return listOf(
            OverskjaeringData(
                bladeSize = 600,
                data = generateData(600, 220)
            ),
            OverskjaeringData(
                bladeSize = 700,
                data = generateData(700, 270)
            ),
            OverskjaeringData(
                bladeSize = 750,
                data = generateData(750, 295)
            ),
            OverskjaeringData(
                bladeSize = 800,
                data = generateData(800, 325)
            ),
            OverskjaeringData(
                bladeSize = 900,
                data = generateData(900, 370)
            ),
            OverskjaeringData(
                bladeSize = 1000,
                data = generateData(1000, 425)
            ),
            OverskjaeringData(
                bladeSize = 1200,
                data = generateData(1200, 520)
            ),
            OverskjaeringData(
                bladeSize = 1500,
                data = generateData(1500, 630)
            ),
            OverskjaeringData(
                bladeSize = 1600,
                data = generateData(1600, 720)
            ),
            OverskjaeringData(
                bladeSize = 1800,
                data = generateData(1800, 820)
            ),
            OverskjaeringData(
                bladeSize = 2000,
                data = generateData(2000, 920)
            ),
            OverskjaeringData(
                bladeSize = 2200,
                data = generateData(2200, 1020)
            )
        )
    }

    private fun generateData(bladeSize: Int, maxDepth: Int): Map<Int, ThicknessValues> {
        val radius = bladeSize / 2f
        val data = mutableMapOf<Int, ThicknessValues>()
        for (thickness in 10..40) {
            val thicknessMm = thickness * 10
            if (thicknessMm > maxDepth) continue
            val minCut = (thicknessMm / 2f - 20).coerceAtLeast(0f)
            val maxCut = (thicknessMm / 2f + 20).coerceAtMost(maxDepth.toFloat())
            val overcut = ((radius - maxCut) * 2).coerceAtLeast(0f)
            data[thickness] = ThicknessValues(
                minCutCm = minCut / 10f,
                maxCutCm = maxCut / 10f,
                overcutCm = overcut / 10f
            )
        }
        return data
    }
}
