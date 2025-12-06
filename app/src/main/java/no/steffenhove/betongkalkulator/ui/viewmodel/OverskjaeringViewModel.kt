package no.steffenhove.betongkalkulator.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import kotlin.math.ceil

/**
 * Data som UI viser.
 *
 * wallThicknessValue  = tallet du skrev inn (f.eks. 20.0)
 * wallThicknessUnit   = "mm" eller "cm"
 * min/maxOvercutValue = i SAMME enhet som tykkelsen (mm/cm)
 * recommendedHoleMm   = alltid i mm (borehull oppgis alltid i mm)
 */
data class OverskjaeringResult(
    val bladeDiameterMm: Int,
    val wallThicknessValue: Double,
    val wallThicknessUnit: String,
    val minOvercutValue: Double?,
    val maxOvercutValue: Double?,
    val overcutUnit: String,
    val recommendedHoleMm: Int?
)

class OverskjaeringViewModel : ViewModel() {

    private val calculator = OverskjaeringCalculator()

    // Bladdiameter – default Ø1000 mm (kan du endre som du vil)
    var selectedBladeDiameter by mutableStateOf(1000)
        private set

    // Tykkelsesinput som tekst
    var thicknessInput by mutableStateOf("")
        private set

    // Enhet for tykkelse (og overkutt i UI) – "mm" eller "cm"
    var selectedThicknessUnit by mutableStateOf("cm")
        private set

    // Resultat fra siste beregning
    var result by mutableStateOf<OverskjaeringResult?>(null)
        private set

    // Feilmelding (vises i UI hvis ikke null)
    var errorMessage by mutableStateOf<String?>(null)
        private set

    // -------------------  Oppdatering fra UI  -------------------

    fun onBladeSelected(bladeMm: Int) {
        selectedBladeDiameter = bladeMm
    }

    fun onThicknessChanged(text: String) {
        thicknessInput = text
    }

    fun onUnitSelected(unit: String) {
        selectedThicknessUnit = unit
    }

    // -------------------  Beregning  -------------------

    fun calculate() {
        val raw = thicknessInput.replace(',', '.').trim()

        val thicknessValue = raw.toDoubleOrNull()
        if (thicknessValue == null || thicknessValue <= 0.0) {
            errorMessage = "Ugyldig tykkelse"
            result = null
            return
        }

        // Konverter tykkelse til mm for kalkulatoren
        val thicknessMm = when (selectedThicknessUnit) {
            "mm" -> thicknessValue
            "cm" -> thicknessValue * 10.0
            else -> thicknessValue
        }

        val (minOvercutMm, maxOvercutMm) =
            calculator.calculate(selectedBladeDiameter, thicknessMm)

        if (minOvercutMm == null && maxOvercutMm == null) {
            errorMessage = "Ingen tabellverdier for valgt kombinasjon."
            result = null
            return
        }

        // Overkutt vises i samme enhet som brukeren skrev inn
        fun convertOvercutToUiUnit(valueMm: Double?): Double? {
            return valueMm?.let {
                when (selectedThicknessUnit) {
                    "mm" -> it
                    "cm" -> it / 10.0
                    else -> it
                }
            }
        }

        val minUi = convertOvercutToUiUnit(minOvercutMm)
        val maxUi = convertOvercutToUiUnit(maxOvercutMm)

        // Anbefalt borehull (alltid mm):
        // enkel regel: 2 * maxOverkutt + 20 mm margin
        val recommendedHoleMm = maxOvercutMm?.let {
            ceil(it * 2.0 + 20.0).toInt()
        }

        result = OverskjaeringResult(
            bladeDiameterMm = selectedBladeDiameter,
            wallThicknessValue = thicknessValue,
            wallThicknessUnit = selectedThicknessUnit,
            minOvercutValue = minUi,
            maxOvercutValue = maxUi,
            overcutUnit = selectedThicknessUnit,
            recommendedHoleMm = recommendedHoleMm
        )
        errorMessage = null
    }
}
