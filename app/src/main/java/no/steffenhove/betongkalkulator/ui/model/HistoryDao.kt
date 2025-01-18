package no.steffenhove.betongkalkulator.ui.model

import androidx.room.*

@Dao
interface HistoryDao {
    @Query("SELECT * FROM history ORDER BY id DESC LIMIT 20")
    fun getAllHistory(): List<HistoryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertHistory(historyEntity: HistoryEntity)

    @Delete
    fun deleteHistory(historyEntity: HistoryEntity)

    @Query("DELETE FROM history WHERE isSelected = 1")
    fun deleteSelectedHistory()
}