package no.steffenhove.betongkalkulator.model

data class Calculation(
    val form: String,
    val dimensions: Map<String, Double>,
    val concreteType: String,
    val result: Double
)