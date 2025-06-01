package no.steffenhove.betongkalkulator.ui.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import no.steffenhove.betongkalkulator.model.ResultData

class OverskjaeringViewModel : ViewModel() {

    private val _result = MutableStateFlow<ResultData?>(null)
    val result: StateFlow<ResultData?> = _result

    fun calculateOverskjaering(blad: String, tykkelse: String, unitSystem: String) {
        val bladDiameter = blad.toDoubleOrNull() ?: return
        val betongTykkelse = tykkelse.toDoubleOrNull() ?: return

        // Her kan du bytte ut med ekte logikk for overskjæring
        val minCut = betongTykkelse - 10
        val maxCut = betongTykkelse + 10
        val minCoreHole = betongTykkelse + 50

        _result.value = ResultData(minCut, maxCut, minCoreHole)
    }
}
class OverskjaeringViewModel(application: Application) : AndroidViewModel(application) {
    private val data: List<OverskjaeringData> = loadOverskjaeringData(application)

    fun calculate(bladdiameter: Int, tykkelseCm: Int): OverskjaeringData? {
        return data.find { it.bladdiameter == bladdiameter && it.betongtykkelse_cm == tykkelseCm }
    }
}
