package no.steffenhove.betongkalkulator.ui.viewmodel

import androidx.lifecycle.ViewModel
import kotlin.math.max
import kotlin.math.pow
import kotlin.math.round

/**
 * ViewModel for løftepunkt-skjermen.
 *
 * Denne tar seg kun av å lage tekstlige beskrivelser for plassering av festepunkt,
 * basert på form, antall festepunkt og mål som tekst.
 *
 * Selve vekt- og volum-beregningen håndteres i LoeftepunktScreen.
 */
class LøftepunktViewModel : ViewModel() {

    /**
     * @param form         Kjerne / Firkant / Trekant / Trapes
     * @param antallFester Antall festepunkt (valideres i skjermen)
     * @param festetype    Innvendig / Utvendig (påvirker kun teksten foreløpig)
     * @param a            Lengde / diameter / side A (i valgt enhet)
     * @param b            Bredde / høyde / side B (i valgt enhet)
     * @param enhet        mm / cm / m / inch / foot
     * @param c            Side C (for trekant / trapes)
     * @param d            Side D (for trapes)
     */
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
        val aVal = a.toDoubleOrNull()
        val bVal = b.toDoubleOrNull()
        val cVal = c.toDoubleOrNull()
        val dVal = d.toDoubleOrNull()

        fun toCm(value: Double?, unit: String): Double? {
            if (value == null) return null
            return when (unit) {
                "mm"   -> value / 10.0
                "cm"   -> value
                "m"    -> value * 100.0
                "inch" -> value * 2.54
                "foot" -> value * 30.48
                else   -> value
            }
        }

        val aCm = toCm(aVal, enhet)
        val bCm = toCm(bVal, enhet)
        val cCm = toCm(cVal, enhet)
        val dCm = toCm(dVal, enhet)

        // Kjerne: trivielt – tyngdepunkt i senter
        if (form == "Kjerne") {
            return "Tyngdepunktet ligger i senter. Plasser løftepunktet midt i kjernen."
        }

        // Mangler vi mål, lar vi selve skjermen gi feilmelding
        if (aCm == null || bCm == null) return ""

        return when (form) {
            "Firkant" -> firkantTekst(aCm, bCm, antallFester, festetype)
            "Trekant" -> trekantTekst(aCm, bCm, antallFester, festetype)
            "Trapes"  -> trapesTekst(aCm, bCm, cCm, dCm, antallFester, festetype)
            else      -> ""
        }
    }

    private fun firkantTekst(
        a: Double,
        b: Double,
        antallFester: Int,
        festetype: String
    ): String {
        val midtX = a / 2.0
        val midtY = b / 2.0

        val tekst = when (antallFester) {
            2 -> {
                val x1 = a * 0.25
                val x2 = a * 0.75
                val y = midtY
                """
                Anbefalt plassering for 2 festepunkt på firkant:
                • Feste 1: ca. ${x1.format()} cm fra kant A og ${y.format()} cm fra kant B.
                • Feste 2: ca. ${x2.format()} cm fra kant A og ${y.format()} cm fra kant B.
                
                Dette gir to fester jevnt fordelt rundt tyngdepunktet.
                """.trimIndent()
            }
            4 -> {
                val x1 = a * 0.25
                val x2 = a * 0.75
                val y1 = b * 0.25
                val y2 = b * 0.75
                """
                Anbefalt plassering for 4 festepunkt på firkant:
                • Feste 1: ca. ${x1.format()} cm fra kant A og ${y1.format()} cm fra kant B.
                • Feste 2: ca. ${x2.format()} cm fra kant A og ${y1.format()} cm fra kant B.
                • Feste 3: ca. ${x1.format()} cm fra kant A og ${y2.format()} cm fra kant B.
                • Feste 4: ca. ${x2.format()} cm fra kant A og ${y2.format()} cm fra kant B.
                
                Festene ligger ca. 25 % inn fra hver kant, rundt tyngdepunktet i midten.
                """.trimIndent()
            }
            6 -> {
                val x1 = a / 6.0
                val x2 = a / 2.0
                val x3 = a * 5.0 / 6.0
                val y1 = b / 3.0
                val y2 = b * 2.0 / 3.0
                """
                Anbefalt plassering for 6 festepunkt på firkant:
                • Rad 1: ca. ${x1.format()} cm, ${x2.format()} cm og ${x3.format()} cm fra kant A, 
                  alle ca. ${y1.format()} cm fra kant B.
                • Rad 2: ca. ${x1.format()} cm, ${x2.format()} cm og ${x3.format()} cm fra kant A, 
                  alle ca. ${y2.format()} cm fra kant B.
                
                Dette gir jevn lastfordeling rundt tyngdepunktet.
                """.trimIndent()
            }
            else -> "Antall festepunkt for firkant bør være 2, 4 eller 6. Valgt: $antallFester."
        }

        val ekstra = when (festetype) {
            "Utvendig" ->
                "\n\nVed utvendig løft må posisjonene justeres slik at stropp/kjetting får godt anlegg rundt kanten, " +
                        "men sikt fortsatt mot samme avstand fra hjørnene."
            else -> ""
        }

        return tekst + ekstra
    }

    private fun trekantTekst(
        a: Double,
        b: Double,
        antallFester: Int,
        festetype: String
    ): String {
        // Antar a = grunnlinje, b = høyde.
        val tyngdeX = a / 2.0
        val tyngdeY = b / 3.0

        val tekst = when (antallFester) {
            3 -> {
                val p1x = tyngdeX
                val p1y = tyngdeY
                val p2x = tyngdeX - a * 0.1
                val p2y = tyngdeY + b * 0.1
                val p3x = tyngdeX + a * 0.1
                val p3y = tyngdeY + b * 0.1
                """
                Anbefalt plassering for 3 festepunkt på trekant:
                • Feste 1: ca. ${p1x.format()} cm fra kant A og ${p1y.format()} cm fra kant B (nær geometrisk tyngdepunkt).
                • Feste 2: ca. ${p2x.format()} cm fra kant A og ${p2y.format()} cm fra kant B.
                • Feste 3: ca. ${p3x.format()} cm fra kant A og ${p3y.format()} cm fra kant B.
                
                Festene danner en liten trekant rundt tyngdepunktet og gir stabilt løft.
                """.trimIndent()
            }
            4 -> {
                val x1 = a * 0.25
                val x2 = a * 0.75
                val y1 = b * 0.25
                val y2 = b * 0.6
                """
                Anbefalt plassering for 4 festepunkt på trekant:
                • Feste 1: ca. ${x1.format()} cm fra kant A og ${y1.format()} cm fra kant B.
                • Feste 2: ca. ${x2.format()} cm fra kant A og ${y1.format()} cm fra kant B.
                • Feste 3: ca. ${x1.format()} cm fra kant A og ${y2.format()} cm fra kant B.
                • Feste 4: ca. ${x2.format()} cm fra kant A og ${y2.format()} cm fra kant B.
                
                Dette gir en rektangulær løfteramme rundt tyngdepunktet. Juster noe etter faktisk form og armering.
                """.trimIndent()
            }
            else -> "Antall festepunkt for trekant bør være 3 (innvendig) eller 4 (utvendig). Valgt: $antallFester."
        }

        val ekstra = when (festetype) {
            "Utvendig" ->
                "\n\nVed utvendig løft kan festene flyttes noe nærmere kantene for å få bedre anlegg, " +
                        "men forsøk å beholde samme mønster rundt tyngdepunktet."
            else -> ""
        }

        return tekst + ekstra
    }

    private fun trapesTekst(
        a: Double,
        b: Double,
        c: Double?,
        d: Double?,
        antallFester: Int,
        festetype: String
    ): String {
        // Enkel tilnærming: bruk en "effektiv" firkant basert på midlere lengder.
        val effektivLengde = if (c != null && c > 0.0) (a + c) / 2.0 else a
        val effektivBredde = if (d != null && d > 0.0) (b + d) / 2.0 else b

        val L = max(effektivLengde, 1.0)
        val B = max(effektivBredde, 1.0)

        val baseTekst = firkantTekst(L, B, antallFester, festetype)
        return """
            $baseTekst

            (For trapes er dette en tilnærming der vi bruker en "effektiv" firkant med lengde ≈ ${L.format()} cm
            og bredde ≈ ${B.format()} cm. Kontroller alltid mot faktisk form før boring.)
        """.trimIndent()
    }

    private fun Double.format(decimals: Int = 1): String {
        val factor = 10.0.pow(decimals.toDouble())
        val v = round(this * factor) / factor
        return if (decimals == 0) {
            v.toInt().toString()
        } else {
            "%.${decimals}f".format(v)
        }
    }
}
