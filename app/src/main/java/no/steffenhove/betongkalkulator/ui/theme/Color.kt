package no.steffenhove.betongkalkulator.ui.theme

import androidx.compose.ui.graphics.Color

// Brand / grunnfarger – "betong + signal"
val ConcretePrimary = Color(0xFF37474F)      // Mørk grå/blå (betong / stål)
val ConcretePrimaryDark = Color(0xFF263238)  // Enda mørkere variant
val AccentOrange = Color(0xFFFFA000)         // Oransje – “arbeidsklær / varsling”
val AccentTeal = Color(0xFF26A69A)           // Frisk bieffekt om vi trenger

// Lys modus
val LightPrimary = ConcretePrimary
val LightPrimaryVariant = ConcretePrimaryDark
val LightSecondary = AccentOrange
val LightBackground = Color(0xFFF2F4F5)      // Lys grå, ikke helt hvit
val LightSurface = Color(0xFFFFFFFF)
val LightOnPrimary = Color(0xFFFFFFFF)
val LightOnSecondary = Color(0xFF000000)
val LightOnBackground = Color(0xFF121212)
val LightOnSurface = Color(0xFF121212)

// Mørk modus
val DarkPrimary = Color(0xFF90A4AE)          // Lys gråblå til mørk bakgrunn
val DarkPrimaryVariant = ConcretePrimary
val DarkSecondary = AccentOrange
val DarkBackground = Color(0xFF0B0C0D)
val DarkSurface = Color(0xFF161819)
val DarkOnPrimary = Color(0xFF000000)
val DarkOnSecondary = Color(0xFF000000)
val DarkOnBackground = Color(0xFFECEFF1)
val DarkOnSurface = Color(0xFFECEFF1)
