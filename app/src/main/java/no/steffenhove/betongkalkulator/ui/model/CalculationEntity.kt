package no.steffenhove.betongkalkulator.ui.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Calendar

@Entity(tableName = "calculations")
data class CalculationEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val form: String,
    val unit: String,
    val concreteType: String,
    val dimensions: String,
    val thickness: String,
    val density: Double,
    val result: Double,
    val resultUnit: String,
    val timestamp: Long = Calendar.getInstance().timeInMillis
)