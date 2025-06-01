package no.steffenhove.betongkalkulator.ui.utils

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import no.steffenhove.betongkalkulator.ui.model.OverskjaeringData

fun loadOverskjaeringData(context: Context): List<OverskjaeringData> {
    val json = context.assets.open("overskjaering_interpolert.json")
        .bufferedReader().use { it.readText() }

    val type = object : TypeToken<List<OverskjaeringData>>() {}.type
    return Gson().fromJson(json, type)
}
