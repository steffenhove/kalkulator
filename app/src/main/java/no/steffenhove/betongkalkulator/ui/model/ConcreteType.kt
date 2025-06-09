package no.steffenhove.betongkalkulator.ui.model

data class ConcreteType(val name: String, val density: Double)

val concreteTypes = listOf(
    ConcreteType("Betong", 2400.0),
    ConcreteType("Leca", 800.0),
    ConcreteType("Siporex", 600.0),
    ConcreteType("Egendefinert", 0.0)  // Brukeren kan angi densiteten selv for egendefinert type
)