package no.steffenhove.betongkalkulator.ui.model

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CalculationDao {
    @Insert
    suspend fun insert(calculation: CalculationEntity)

    @Query("SELECT * FROM calculations ORDER BY timestamp DESC LIMIT 20")
    fun getAllCalculations(): Flow<List<CalculationEntity>>

    @Delete
    suspend fun deleteCalculation(calculation: CalculationEntity)

    @Query("DELETE FROM calculations")
    suspend fun deleteAllCalculations()
}