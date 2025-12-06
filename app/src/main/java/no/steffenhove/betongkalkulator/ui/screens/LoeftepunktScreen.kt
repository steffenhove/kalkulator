package no.steffenhove.betongkalkulator.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.pow
import kotlin.math.roundToInt
import no.steffenhove.betongkalkulator.ui.components.AppDropdown

/**
 * Typer vi støtter foreløpig.
 */
private enum class ShapeType(val displayName: String) {
    Rectangle("Firkant"),
    Circle("Kjerne"),
    Triangle("Trekant"),
    Trapezoid("Trapes")
}

/**
 * Enheter for inntasting på skjermen.
 * Disse er uavhengige av globale innstillinger foreløpig.
 */
private enum class LoeftepunktUnit(val label: String) {
    Millimeter("mm"),
    Centimeter("cm"),
    Meter("m"),
    Inch("in"),
    Foot("ft")
}

@Composable
fun LoeftepunktScreen(
    navigateBack: () -> Unit
) {
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()

    // Valg / state
    var selectedShape by rememberSaveable { mutableStateOf(ShapeType.Rectangle) }
    var selectedUnit by rememberSaveable { mutableStateOf(LoeftepunktUnit.Meter) }
    var festepunkt by rememberSaveable { mutableStateOf(4) }

    var lengthA by rememberSaveable { mutableStateOf("") }
    var lengthB by rememberSaveable { mutableStateOf("") }
    var lengthC by rememberSaveable { mutableStateOf("") } // brukes for trekant / trapes
    var thickness by rememberSaveable { mutableStateOf("") }

    var resultText by rememberSaveable { mutableStateOf("") }

    fun parseNumber(text: String): Double? =
        text.replace(',', '.').toDoubleOrNull()

    fun toMeters(value: Double, unit: LoeftepunktUnit): Double =
        when (unit) {
            LoeftepunktUnit.Millimeter -> value / 1000.0
            LoeftepunktUnit.Centimeter -> value / 100.0
            LoeftepunktUnit.Meter -> value
            LoeftepunktUnit.Inch -> value * 0.0254
            LoeftepunktUnit.Foot -> value * 0.3048
        }

    fun fromMeters(value: Double, unit: LoeftepunktUnit): Double =
        when (unit) {
            LoeftepunktUnit.Millimeter -> value * 1000.0
            LoeftepunktUnit.Centimeter -> value * 100.0
            LoeftepunktUnit.Meter -> value
            LoeftepunktUnit.Inch -> value / 0.0254
            LoeftepunktUnit.Foot -> value / 0.3048
        }

    fun format(value: Double, unit: LoeftepunktUnit): String {
        val v = fromMeters(value, unit)
        val rounded = (v * 10.0).roundToInt() / 10.0
        return "$rounded ${unit.label}"
    }

    fun calculate() {
        val aRaw = parseNumber(lengthA)
        val bRaw = parseNumber(lengthB)
        val cRaw = parseNumber(lengthC)
        val tRaw = parseNumber(thickness)

        if (aRaw == null || tRaw == null || (selectedShape != ShapeType.Circle && bRaw == null)) {
            resultText = "Fyll ut nødvendige mål før beregning."
            return
        }

        val a = toMeters(aRaw, selectedUnit)
        val t = toMeters(tRaw, selectedUnit)
        val density = 2400.0 // kg/m³ – kan senere hentes fra innstillinger

        val area = when (selectedShape) {
            ShapeType.Rectangle -> {
                val b = toMeters(bRaw!!, selectedUnit)
                a * b
            }
            ShapeType.Circle -> {
                // Her bruker vi A som diameter
                val radius = a / 2.0
                PI * radius.pow(2)
            }
            ShapeType.Triangle -> {
                val b = toMeters(bRaw!!, selectedUnit)
                0.5 * a * b
            }
            ShapeType.Trapezoid -> {
                if (cRaw == null) {
                    resultText = "Fyll inn A, B og C for trapes."
                    return
                }
                val b = toMeters(bRaw!!, selectedUnit)
                val h = toMeters(cRaw, selectedUnit)
                ((a + b) / 2.0) * h
            }
        }

        val volume = area * t          // m³
        val weight = volume * density  // kg

        // Enkle tommelfingerregler for festepunkt
        val advice = when (selectedShape) {
            ShapeType.Rectangle -> {
                val bMeters = if (selectedShape == ShapeType.Rectangle && bRaw != null) {
                    toMeters(bRaw, selectedUnit)
                } else 0.0

                when (festepunkt) {
                    2 -> {
                        val offsetA = 0.25 * a
                        "2 festepunkt langs lengde A:\n" +
                                "- Plasser dem ca. ${format(offsetA, selectedUnit)} inn fra hver kortside,\n" +
                                "- midt på B-retningen."
                    }
                    4 -> {
                        val offsetA = 0.25 * a
                        val offsetB = 0.25 * bMeters
                        "4 festepunkt (rektangel):\n" +
                                "- Ca. ${format(offsetA, selectedUnit)} inn fra hver kant på A,\n" +
                                "- Ca. ${format(offsetB, selectedUnit)} inn fra hver kant på B."
                    }
                    6 -> {
                        "6 festepunkt (tung bit):\n" +
                                "- 4 festepunkt som ved 4-punkts løft,\n" +
                                "- 2 ekstra nær midten for å avlaste ved ujevn last."
                    }
                    else -> "Antall festepunkt støttes ikke fullt ut ennå – bruk 2, 4 eller 6."
                }
            }

            ShapeType.Circle -> {
                when (festepunkt) {
                    1 -> "1 festepunkt: Sentrer i midten (typisk for kjerner og små runde element)."
                    2 -> "2 festepunkt: Plasser dem på hver side av senter, 90° mellom slynger, med tilnærmet lik avstand til kant."
                    3, 4 -> "3–4 festepunkt: Fordel jevnt rundt senter, med lik vinkel mellom slyngene."
                    else -> "Runde element: fordel festepunkt jevnt rundt senter."
                }
            }

            ShapeType.Triangle -> {
                "Trekantet element:\n" +
                        "- Minst 3 festepunkt – ett nær hvert hjørne.\n" +
                        "- Flytt festene litt inn fra hjørnene for å unngå at betongen sprikker.\n" +
                        "- Sikt mot at tyngdepunktet ligger midt i trekanten av slyngene."
            }

            ShapeType.Trapezoid -> {
                "Trapesformet element:\n" +
                        "- Bruk 4 festepunkt hvis mulig.\n" +
                        "- Plasser to langs hver av de parallelle sidene,\n" +
                        "- juster slik at tyngdepunktet kommer omtrent midt mellom festepunktene."
            }
        }

        val weightRounded = (weight * 10.0).roundToInt() / 10.0
        val volumeRounded = (volume * 1000.0).roundToInt() / 1000.0

        resultText =
            "Estimert vekt: $weightRounded kg\n" +
                    "Volum: $volumeRounded m³\n\n" +
                    "Forslag til plassering av festepunkt:\n$advice"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState)
    ) {
        // "AppBar" – enkel
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(onClick = navigateBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Tilbake"
                )
            }
            Text(
                text = "Løftepunkt",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Form + enhet (under hverandre)
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            AppDropdown(
                label = "Form",
                options = ShapeType.values().map { it.displayName },
                selectedOption = selectedShape.displayName,
                onOptionSelected = { label ->
                    selectedShape = ShapeType.values().first { it.displayName == label }
                }
            )

            AppDropdown(
                label = "Enhet",
                options = LoeftepunktUnit.values().map { it.label },
                selectedOption = selectedUnit.label,
                onOptionSelected = { label ->
                    selectedUnit = LoeftepunktUnit.values().first { it.label == label }
                }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Antall festepunkt
        AppDropdown(
            label = "Antall festepunkt",
            options = listOf("2", "4", "6"),
            selectedOption = festepunkt.toString(),
            onOptionSelected = { festepunkt = it.toIntOrNull() ?: 4 }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Mål – avhengig av form
        OutlinedTextField(
            value = lengthA,
            onValueChange = { lengthA = it },
            label = {
                Text(
                    when (selectedShape) {
                        ShapeType.Circle -> "Diameter (A) (${selectedUnit.label})"
                        else -> "Lengde A (${selectedUnit.label})"
                    }
                )
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            ),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        if (selectedShape != ShapeType.Circle) {
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = lengthB,
                onValueChange = { lengthB = it },
                label = {
                    Text(
                        when (selectedShape) {
                            ShapeType.Rectangle -> "Lengde B (${selectedUnit.label})"
                            ShapeType.Triangle -> "Høyde (B) (${selectedUnit.label})"
                            ShapeType.Trapezoid -> "Side B (parallell med A) (${selectedUnit.label})"
                            else -> "Lengde B (${selectedUnit.label})"
                        }
                    )
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        }

        if (selectedShape == ShapeType.Trapezoid) {
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = lengthC,
                onValueChange = { lengthC = it },
                label = { Text("Høyde (C) (${selectedUnit.label})") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = thickness,
            onValueChange = { thickness = it },
            label = { Text("Tykkelse (${selectedUnit.label})") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    focusManager.clearFocus()
                    calculate()
                }
            ),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                focusManager.clearFocus()
                calculate()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Beregn løftepunkt")
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (resultText.isNotBlank()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Text(
                    text = resultText,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}
