package no.steffenhove.betongkalkulator.ui.repository

import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.Flow
import no.steffenhove.betongkalkulator.ui.model.CalculationDao
import no.steffenhove.betongkalkulator.ui.model.CalculationEntity

class CalculationRepository(private val calculationDao: CalculationDao) {

    val allCalculations: Flow<List<CalculationEntity>> = calculationDao.getAllCalculations()

    @WorkerThread
    suspend fun insert(calculation: CalculationEntity) {
        calculationDao.insert(calculation)
    }

    @WorkerThread
    suspend fun delete(calculation: CalculationEntity) {
        calculationDao.deleteCalculation(calculation)
    }

    @WorkerThread
    suspend fun deleteAll() {
        calculationDao.deleteAllCalculations()
    }
}