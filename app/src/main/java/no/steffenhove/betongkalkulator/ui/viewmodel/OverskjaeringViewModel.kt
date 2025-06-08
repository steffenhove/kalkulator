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

    // Laster inn og sorterer dataene én gang når ViewModelen opprettes
    private val overskjaeringDataList: List<OverskjaeringData> = loadOverskjaeringData(application).sortedBy { it.bladeSize }

    // State for å holde på det gyldige resultatet
    private val _result = MutableStateFlow<OverskjaeringResult?>(null)
    val result: StateFlow<OverskjaeringResult?> = _result

    // State for å holde på info- eller feilmeldinger til brukeren
    private val _infoMessage = MutableStateFlow<String?>(null)
    val infoMessage: StateFlow<String?> = _infoMessage

    init {
        // Skriver ut en loggmelding når ViewModelen er klar, for feilsøking
        Log.d("OverskjæringViewModel", "ViewModel initialisert. Antall blad-datatyper lastet: ${overskjaeringDataList.size}")
    }

    fun calculate(bladDiameter: Int, betongTykkelseCm: Int) {
        // Nullstiller tidligere meldinger og resultater ved hver nye beregning
        _infoMessage.value = null
        _result.value = null

        // Finner riktig sett med data for valgt bladdiameter
        val bladDataMap = overskjaeringDataList.find { it.bladeSize == bladDiameter }?.data

        // Hvis vi ikke fant data for det valgte bladet, sett en feilmelding
        if (bladDataMap == null) {
            _infoMessage.value = "Fant ikke data for Ø$bladDiameter mm blad."
            return
        }

        // Sjekker om den valgte tykkelsen er større enn det bladet kan håndtere
        val maksTykkelseForBlad = bladDataMap.keys.maxOrNull() ?: 0
        if (betongTykkelseCm > maksTykkelseForBlad) {
            // Finner det minste bladet som er større enn det valgte, og som kan håndtere tykkelsen
            val anbefaltBlad = overskjaeringDataList.find {
                it.bladeSize > bladDiameter && (it.data.keys.maxOrNull() ?: 0) >= betongTykkelseCm
            }
            val anbefaltBladTekst = anbefaltBlad?.let { "Ø${it.bladeSize} mm eller større." } ?: "et større blad."
            _infoMessage.value = "Ø$bladDiameter mm er for lite for $betongTykkelseCm cm betong.\nAnbefalt blad: $anbefaltBladTekst"
            return
        }

        // Finner nærmeste lavere og høyere betongtykkelse i tabellen for interpolasjon
        val lavereTykkelseKey = bladDataMap.keys.filter { it <= betongTykkelseCm }.maxOrNull()
        val hoyereTykkelseKey = bladDataMap.keys.filter { it >= betongTykkelseCm }.minOrNull()

        if (lavereTykkelseKey == null || hoyereTykkelseKey == null) {
            _infoMessage.value = "Ugyldig betongtykkelse for det valgte bladet."
            return
        }

        // Henter ut verdiene (Overkapp, Skjæredybde) for interpolasjon
        val (overkapp1_cm, minSkjaering1_cm) = bladDataMap[lavereTykkelseKey]!!
        val (overkapp2_cm, minSkjaering2_cm) = bladDataMap[hoyereTykkelseKey]!!

        val interpolertOverkappCm: Float
        val interpolertMinSkjaeringCm: Float

        if (lavereTykkelseKey == hoyereTykkelseKey) { // Eksakt treff i tabellen
            interpolertOverkappCm = overkapp1_cm
            interpolertMinSkjaeringCm = minSkjaering1_cm
        } else { // Lineær interpolasjon
            val t = (betongTykkelseCm - lavereTykkelseKey).toFloat() / (hoyereTykkelseKey - lavereTykkelseKey).toFloat()
            interpolertOverkappCm = overkapp1_cm + t * (overkapp2_cm - overkapp1_cm)
            interpolertMinSkjaeringCm = minSkjaering1_cm + t * (minSkjaering2_cm - minSkjaering1_cm)
        }

        // Beregn minste borehull basert på overkapp-lengden.
        // Hvis overkapp kan være negativt (som i noen tabeller), settes borehullet til 0.
        val minBorehullMm = if (interpolertOverkappCm > 0) interpolertOverkappCm * 10f else 0f

        // Setter det endelige, gyldige resultatet
        _result.value = OverskjaeringResult(
            minSkjaeringCm = interpolertMinSkjaeringCm,
            maksSkjaeringCm = interpolertOverkappCm,
            minBorehullMm = minBorehullMm
        )
    }
}