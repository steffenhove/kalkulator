package no.steffenhove.betongkalkulator.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import no.steffenhove.betongkalkulator.ui.utils.AppPreferenceManager
import kotlin.math.pow
import kotlin.math.sqrt

class OverskjaeringViewModel : ViewModel() {

    private val _bladeSizeInput = MutableStateFlow("")
    val bladeSizeInput: StateFlow<String> = _bladeSizeInput

    private val _thicknessInput = MutableStateFlow("")
    val thicknessInput: StateFlow<String> = _thicknessInput

    private val _minCut = MutableStateFlow<Double?>(null)
    val minCut: StateFlow<Double?> = _minCut

    private val _maxCut = MutableStateFlow<Double?>(null)
    val maxCut: StateFlow<Double?> = _maxCut

    private val _minBoreDiameter = MutableStateFlow<Double?>(null)
    val minBoreDiameter: StateFlow<Double?> = _minBoreDiameter

    private val _maxBoreDiameter = MutableStateFlow<Double?>(null)
    val maxBoreDiameter: StateFlow<Double?> = _maxBoreDiameter

    fun setBladeSizeInput(value: String) {
        _bladeSizeInput.value = value
    }

    fun setThicknessInput(value: String) {
        _thicknessInput.value = value
    }

    fun calculate(context: Context) {
        val bladeMm = _bladeSizeInput.value.replace(",", ".").toDoubleOrNull() ?: return
        val thicknessStr = _thicknessInput.value

        val unit = AppPreferenceManager.getLastOverskjaeringUnit(context)

        val thicknessMeters = try {
            val value = thicknessStr.replace(",", ".").toDouble()
            when (unit) {
                "mm" -> value / 1000.0
                "cm" -> value / 100.0
                "m" -> value
                "inch" -> value * 0.0254
                "foot" -> value * 0.3048
                else -> null
            }
        } catch (e: Exception) {
            null
        }

        val maksimalSkjaeredybde = when (bladeMm.toInt()) {
            600 -> 24.0
            700 -> 28.0
            750 -> 30.0
            800 -> 32.5
            900 -> 36.5
            1000 -> 42.0
            1200 -> 52.0
            1500 -> 62.0
            1600 -> 72.0
            else -> (bladeMm * 0.45) / 10.0 // fallback for ukjent blad
        } // cm

        val tilgjengeligRadius = maksimalSkjaeredybde * 10.0 // mm

        if (thicknessMeters != null && thicknessMeters > 0.0) {
            val tykkelseMm = thicknessMeters * 1000.0

            val minimalSkjaering = sqrt(4 * tilgjengeligRadius * tykkelseMm - tykkelseMm.pow(2))
            val maksimalSkjaering = 2 * tilgjengeligRadius

            val overkappMin = maksimalSkjaering - tykkelseMm
            val overkappMax = minimalSkjaering - tykkelseMm

            _minCut.value = minimalSkjaering / 10.0 // til cm
            _maxCut.value = maksimalSkjaering / 10.0 // til cm

            _minBoreDiameter.value = overkappMax
            _maxBoreDiameter.value = overkappMin
        } else {
            _minCut.value = null
            _maxCut.value = null
            _minBoreDiameter.value = null
            _maxBoreDiameter.value = null
        }
    }
}
