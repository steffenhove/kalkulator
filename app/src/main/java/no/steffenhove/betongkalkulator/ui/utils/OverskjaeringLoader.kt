package no.steffenhove.betongkalkulator.ui.utils

import android.content.Context
import android.util.Log
import no.steffenhove.betongkalkulator.ui.model.OverskjaeringData
import no.steffenhove.betongkalkulator.ui.model.ThicknessValues
import org.json.JSONObject

fun loadOverskjaeringData(context: Context): List<OverskjaeringData> {
    val TAG = "OverskjæringLoader"
    Log.d(TAG, "Laster overskjæringsdata...")

    return try {
        val jsonText = context.assets.open("overskjaering_interpolert.json")
            .bufferedReader()
            .use { it.readText() }
        Log.d(TAG, "Fil lest OK.")

        val rootObject = JSONObject(jsonText)
        val overskjaeringList = mutableListOf<OverskjaeringData>()

        for (bladeSizeStr in rootObject.keys()) {
            val bladeSizeInt = bladeSizeStr.toIntOrNull()
            if (bladeSizeInt != null) {
                val thicknessDataMap = mutableMapOf<Int, ThicknessValues>()
                val thicknessObject = rootObject.getJSONObject(bladeSizeStr)

                for (thicknessStr in thicknessObject.keys()) {
                    val thicknessInt = thicknessStr.toIntOrNull()
                    if (thicknessInt != null) {
                        val valuesObject = thicknessObject.getJSONObject(thicknessStr)
                        val minCut = valuesObject.getDouble("minCutCm").toFloat()
                        val maxCut = valuesObject.getDouble("maxCutCm").toFloat()
                        val overcut = valuesObject.getDouble("overcutCm").toFloat()
                        thicknessDataMap[thicknessInt] = ThicknessValues(minCut, maxCut, overcut)
                    }
                }
                if (thicknessDataMap.isNotEmpty()) {
                    overskjaeringList.add(OverskjaeringData(bladeSizeInt, thicknessDataMap))
                }
            }
        }
        Log.d(TAG, "Parsing ferdig. Lastet data for ${overskjaeringList.size} bladstørrelser.")
        overskjaeringList
    } catch (e: Exception) {
        Log.e(TAG, "Feil ved lasting av JSON:", e)
        emptyList()
    }
}