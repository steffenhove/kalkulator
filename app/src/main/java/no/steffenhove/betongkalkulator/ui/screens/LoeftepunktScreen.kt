package no.steffenhove.betongkalkulator.ui.screens

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import no.steffenhove.betongkalkulator.ui.components.AppDropdown
import no.steffenhove.betongkalkulator.ui.components.DimensionField
import no.steffenhove.betongkalkulator.ui.utils.convertToMeters
import no.steffenhove.betongkalkulator.ui.utils.getUnitSystemPreference
import androidx.compose.runtime.mutableDoubleStateOf
import kotlin.math.PI
import kotlin.math.pow
import kotlin.math.sqrt

@Composable
fun LoeftepunktScreen(context: Context) {
    val unitSystem = getUnitSystemPreference(context)
    val availableUnits = if (unitSystem == "Imperialsk") listOf("inch", "foot") else listOf("mm", "cm", "m")
    var selectedUnit by rememberSaveable { mutableStateOf(availableUnits.first()) }

    val forms = listOf("Kjerne", "Firkant", "Trekant", "Trapes")
    var selectedForm by rememberSaveable { mutableStateOf(forms[0]) }

    val festetyper = listOf("Innvendig", "Utvendig")
    var valgtFestetype by rememberSaveable { mutableStateOf(festetyper[0]) }

    val dim1 = remember { mutableStateOf(TextFieldValue()) }
    val dim2 = remember { mutableStateOf(TextFieldValue()) }
    val dim3 = remember { mutableStateOf(TextFieldValue()) }
    val dim4 = remember { mutableStateOf(TextFieldValue()) }
    val thickness = remember { mutableStateOf(TextFieldValue()) }

    val f1 = remember { FocusRequester() }
    val f2 = remember { FocusRequester() }
    val f3 = remember { FocusRequester() }
    val f4 = remember { FocusRequester() }
    val f5 = remember { FocusRequester() }

    var weightKg by remember { mutableDoubleStateOf(0.0) }

    fun calculateWeight() {
        val d1 = convertToMeters(dim1.value.text, selectedUnit) ?: 0.0
        val d2 = convertToMeters(dim2.value.text, selectedUnit) ?: 0.0
        val d3 = convertToMeters(dim3.value.text, selectedUnit) ?: 0.0
        val d4 = convertToMeters(dim4.value.text, selectedUnit) ?: 0.0
        val t = convertToMeters(thickness.value.text, selectedUnit) ?: 0.0

        val density = 2400.0 // standard betongtetthet kg/m³
        val volume = when (selectedForm) {
            "Kjerne" -> PI * (d1 / 2).pow(2) * t
            "Firkant" -> d1 * d2 * t
            "Trekant" -> 0.5 * d1 * d2 * t
            "Trapes" -> {
                val s = (d1 + d2 + d3 + d4) / 2
                val area = sqrt((s - d1) * (s - d2) * (s - d3) * (s - d4))
                area * t
            }
            else -> 0.0
        }

        weightKg = volume * density
    }

    Column(modifier = Modifier.padding(16.dp).fillMaxSize()) {
        Text("Løftepunkt", style = MaterialTheme.typography.titleLarge)

        Spacer(modifier = Modifier.height(16.dp))
        AppDropdown("Form", forms, selectedForm) { selectedForm = it }

        Spacer(modifier = Modifier.height(8.dp))
        AppDropdown("Enhet", availableUnits, selectedUnit) { selectedUnit = it }

        Spacer(modifier = Modifier.height(8.dp))
        AppDropdown("Festetype", festetyper, valgtFestetype) { valgtFestetype = it }

        Spacer(modifier = Modifier.height(16.dp))

        when (selectedForm) {
            "Kjerne" -> {
                DimensionField(dim1, "Diameter", selectedUnit, f1, f2)
                DimensionField(thickness, "Tykkelse", selectedUnit, f2) { calculateWeight() }
            }
            "Firkant" -> {
                DimensionField(dim1, "Lengde", selectedUnit, f1, f2)
                DimensionField(dim2, "Bredde", selectedUnit, f2, f3)
                DimensionField(thickness, "Tykkelse", selectedUnit, f3) { calculateWeight() }
            }
            "Trekant" -> {
                DimensionField(dim1, "A", selectedUnit, f1, f2)
                DimensionField(dim2, "B", selectedUnit, f2, f3)
                DimensionField(dim3, "C", selectedUnit, f3, f4)
                DimensionField(thickness, "Tykkelse", selectedUnit, f4) { calculateWeight() }
            }
            "Trapes" -> {
                DimensionField(dim1, "A", selectedUnit, f1, f2)
                DimensionField(dim2, "B", selectedUnit, f2, f3)
                DimensionField(dim3, "C", selectedUnit, f3, f4)
                DimensionField(dim4, "D", selectedUnit, f4, f5)
                DimensionField(thickness, "Tykkelse", selectedUnit, f5) { calculateWeight() }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { calculateWeight() }) {
            Text("Beregn vekt og plassering")
        }

        Spacer(modifier = Modifier.height(16.dp))
        if (weightKg > 0) {
            val tonsFormatted = "%.2f".format(weightKg / 1000)
            Text("Estimert vekt: %.1f kg ($tonsFormatted t".format(weightKg))

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                when (valgtFestetype) {
                    "Utvendig" -> "Plasser løftepunkt på hver langside, nær endene. Borr gjerne halvt hull i skjøt mellom to biter for å låse stropp."
                    "Innvendig" -> "Fordel løftepunkt jevnt og symmetrisk innenfor tyngdepunktet."
                    else -> ""
                },
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
