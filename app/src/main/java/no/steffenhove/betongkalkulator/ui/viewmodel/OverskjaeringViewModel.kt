package no.steffenhove.betongkalkulator.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import no.steffenhove.betongkalkulator.ui.model.OverskjaeringData
import no.steffenhove.betongkalkulator.ui.model.OverskjaeringResult
import no.steffenhove.betongkalkulator.ui.utils.loadOverskjaeringData

class OverskjaeringViewModel(application: Application) : AndroidViewModel(application) {
    private val overskjaeringDataList: List<OverskjaeringData> = loadOverskjaeringData(application).sortedBy { it.bladeSize }
    private val _result = MutableStateFlow<OverskjaeringResult?>(null)
    val result: StateFlow<OverskjaeringResult?> = _result
    private val _infoMessage = MutableStateFlow<String?>(null)
    val infoMessage: StateFlow<String?> = _infoMessage

    init {
        Log.d("OverskjæringDebug", "ViewModel initialisert. Antall blad-datatyper lastet: ${overskjaeringDataList.size}")
    }

    fun calculate(bladDiameter: Int, betongTykkelseCm: Int) {
        _infoMessage.value = null
        _result.value = null

        val bladDataMap = overskjaeringDataList.find { it.bladeSize == bladDiameter }?.data
        if (bladDataMap == null) {
            _infoMessage.value = "Fant ikke data for Ø$bladDiameter mm blad."
            return
        }

        val maksTykkelseForBlad = bladDataMap.keys.maxOrNull() ?: 0
        if (betongTykkelseCm > maksTykkelseForBlad) {
            val anbefaltBlad = overskjaeringDataList.find { it.bladeSize > bladDiameter && (it.data.keys.maxOrNull() ?: 0) >= betongTykkelseCm }
            val anbefaltBladTekst = anbefaltBlad?.let { "Ø${it.bladeSize} mm eller større." } ?: "et større blad."
            _infoMessage.value = "Ø$bladDiameter mm er for lite for $betongTykkelseCm cm betong.\nAnbefalt blad: $anbefaltBladTekst"
            return
        }

        val lavereTykkelseKey = bladDataMap.keys.filter { it <= betongTykkelseCm }.maxOrNull()
        val hoyereTykkelseKey = bladDataMap.keys.filter { it >= betongTykkelseCm }.minOrNull()

        if (lavereTykkelseKey == null || hoyereTykkelseKey == null) {
            _infoMessage.value = "Ugyldig betongtykkelse for det valgte bladet."
            return
        }

        val values1 = bladDataMap[lavereTykkelseKey]!!
        val values2 = bladDataMap[hoyereTykkelseKey]!!

        val overkapp1 = values1.overcutCm
        val minSkjaering1 = values1.minCutCm
        val overkapp2 = values2.overcutCm
        val minSkjaering2 = values2.minCutCm

        val interpolertOverkappCm = if (lavereTykkelseKey == hoyereTykkelseKey) overkapp1 else overkapp1 + (betongTykkelseCm - lavereTykkelseKey) * (overkapp2 - overkapp1) / (hoyereTykkelseKey - lavereTykkelseKey)
        val interpolertMinSkjaeringCm = if (lavereTykkelseKey == hoyereTykkelseKey) minSkjaering1 else minSkjaering1 + (betongTykkelseCm - lavereTykkelseKey) * (minSkjaering2 - minSkjaering1) / (hoyereTykkelseKey - lavereTykkelseKey)
        val minBorehullMm = if (interpolertOverkappCm > 0) interpolertOverkappCm * 10f else 0f

        _result.value = OverskjaeringResult(interpolertMinSkjaeringCm, interpolertOverkappCm, minBorehullMm)
    }
}