package no.steffenhove.betongkalkulator.ui.viewmodel

import androidx.lifecycle.ViewModel
import no.steffenhove.betongkalkulator.ui.utils.convertToMeters
import no.steffenhove.betongkalkulator.ui.utils.formatLargeNumber

class LøftepunktViewModel : ViewModel() {

    fun beregnFesteplasseringSomTekst(
        form: String,
        antallFester: Int,
        festetype: String,
        a: String,
        b: String,
        enhet: String,
        c: String = "",
        d: String = ""
    ): String {
        val aCm = konverterTilCm(a, enhet)
        val bCm = konverterTilCm(b, enhet)
        val cCm = konverterTilCm(c, enhet)
        val dCm = konverterTilCm(d, enhet)

        val linjer = when (form.lowercase()) {
            "kjerne" -> listOf("Festepunkt: ca %.0f cm fra kant A og %.0f cm fra kant B (sentrisk)"
                .format(aCm / 2, bCm / 2))

            "firkant" -> beregnFirkant(aCm, bCm, antallFester, festetype)

            "trekant" -> beregnTrekant(aCm, bCm, cCm, antallFester, festetype)

            "trapes" -> beregnTrapes(aCm, bCm, cCm, dCm, antallFester, festetype)

            else -> listOf("Formen støttes ikke ennå.")
        }

        return linjer.joinToString("\n")
    }

    fun konverterTilCm(input: String, enhet: String): Double {
        val verdi = input.toDoubleOrNull() ?: 0.0
        return when (enhet.lowercase()) {
            "mm" -> verdi / 10.0
            "cm" -> verdi
            "m" -> verdi * 100.0
            "inch" -> verdi * 2.54
            "foot" -> verdi * 30.48
            else -> verdi
        }
    }

    private fun beregnFirkant(lengde: Double, bredde: Double, antall: Int, festetype: String): List<String> {
        val fester = mutableListOf<String>()
        val margin = if (festetype == "Utvendig") 0.0 else 0.25
        val posA = when (antall) {
            1 -> listOf(lengde / 2)
            2 -> listOf(lengde / 2, lengde / 2)
            3 -> listOf(lengde * margin, lengde * (1 - margin), lengde / 2)
            4 -> listOf(lengde * margin, lengde * (1 - margin), lengde * margin, lengde * (1 - margin))
            6 -> listOf(lengde * margin, lengde / 2, lengde * (1 - margin), lengde * margin, lengde / 2, lengde * (1 - margin))
            else -> return listOf("Støtter 1, 2, 3, 4 eller 6 festepunkt for firkant.")
        }

        val posB = when (antall) {
            1 -> listOf(bredde / 2)
            2 -> listOf(bredde * margin, bredde * (1 - margin))
            3 -> listOf(bredde / 2, bredde * margin, bredde * (1 - margin))
            4 -> listOf(bredde * margin, bredde * margin, bredde * (1 - margin), bredde * (1 - margin))
            6 -> listOf(bredde * margin, bredde * margin, bredde * margin, bredde * (1 - margin), bredde * (1 - margin), bredde * (1 - margin))
            else -> return listOf("Støtter 1, 2, 3, 4 eller 6 festepunkt for firkant.")
        }

        for (i in 0 until antall) {
            val x = posA.getOrElse(i) { lengde / 2 }
            val y = posB.getOrElse(i) { bredde / 2 }
            fester.add("Festepunkt ${i + 1}: ca %.0f cm fra kant A og %.0f cm fra kant B".format(x, y))
        }

        return fester
    }

    private fun beregnTrekant(a: Double, b: Double, c: Double, antall: Int, festetype: String): List<String> {
        val fester = mutableListOf<String>()
        val x = b / 2
        val y = a / 3

        when (antall) {
            1 -> fester.add("Festepunkt: ca %.0f cm fra kant A og %.0f cm fra kant B (tyngdepunkt)".format(y, x))
            3, 4, 6 -> {
                val margin = if (festetype == "Utvendig") 0.0 else 0.25
                val ax = a * margin
                val ax2 = a * (1 - margin)
                val bx = b * margin
                val bx2 = b * (1 - margin)
                val cx = a / 2
                val cy = b / 2
                val kombinasjoner = listOf(
                    Pair(ax, bx), Pair(ax2, bx), Pair(ax, bx2), Pair(ax2, bx2), Pair(cx, bx), Pair(cx, bx2)
                )
                for (i in 0 until antall.coerceAtMost(kombinasjoner.size)) {
                    val (aVal, bVal) = kombinasjoner[i]
                    fester.add("Festepunkt ${i + 1}: ca %.0f cm fra kant A og %.0f cm fra kant B".format(aVal, bVal))
                }
            }
            else -> fester.add("Støtte for 1, 3, 4 eller 6 festepunkt på trekant.")
        }
        return fester
    }

    private fun beregnTrapes(a: Double, b: Double, c: Double, d: Double, antall: Int, festetype: String): List<String> {
        val fester = mutableListOf<String>()
        val margin = if (festetype == "Utvendig") 0.0 else 0.25
        val a1 = a * margin
        val a2 = a * (1 - margin)
        val b1 = b * margin
        val b2 = b * (1 - margin)
        val midA = a / 2
        val midB = (b + c) / 2 / 2

        when (antall) {
            1 -> {
                val tyngdepunktFraBunn = (a / 3) * ((2 * c + b) / (c + b))
                val tyngdepunktFraKantA = tyngdepunktFraBunn
                val tyngdepunktFraKantB = (b + c) / 4
                fester.add("Festepunkt: ca %.0f cm fra kant A og %.0f cm fra kant B (tyngdepunkt)".format(tyngdepunktFraKantA, tyngdepunktFraKantB))
            }
            3, 4, 6 -> {
                fester.add("Festepunkt 1: %.0f cm fra kant A og %.0f cm fra kant B".format(a1, b1))
                fester.add("Festepunkt 2: %.0f cm fra kant A og %.0f cm fra kant B".format(a2, b1))
                fester.add("Festepunkt 3: %.0f cm fra kant A og %.0f cm fra kant B".format(a1, b2))
                if (antall >= 4) fester.add("Festepunkt 4: %.0f cm fra kant A og %.0f cm fra kant B".format(a2, b2))
                if (antall >= 6) {
                    fester.add("Festepunkt 5: %.0f cm fra kant A og %.0f cm fra kant B".format(midA, b1))
                    fester.add("Festepunkt 6: %.0f cm fra kant A og %.0f cm fra kant B".format(midA, b2))
                }
            }
            else -> fester.add("Støtte for 1, 3, 4 eller 6 festepunkt på trapes.")
        }
        return fester
    }
}
