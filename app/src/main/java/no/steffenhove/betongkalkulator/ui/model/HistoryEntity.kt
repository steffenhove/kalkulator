package no.steffenhove.betongkalkulator.ui.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "history")
data class HistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val calculationDetails: String,
    val date: String,
    val time: String,
    var isSelected: Boolean = false // Legg til valgt status
)