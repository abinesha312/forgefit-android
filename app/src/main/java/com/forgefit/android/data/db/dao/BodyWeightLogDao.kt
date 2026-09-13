package com.forgefit.android.data.db.dao

import androidx.room.*
import com.forgefit.android.data.model.BodyWeightLog
import kotlinx.coroutines.flow.Flow

@Dao
interface BodyWeightLogDao {
    @Query("SELECT * FROM body_weight_log ORDER BY date DESC")
    fun getAllLogs(): Flow<List<BodyWeightLog>>

    @Query("SELECT * FROM body_weight_log ORDER BY date DESC LIMIT 1")
    suspend fun getLatestLog(): BodyWeightLog?

    @Query("SELECT * FROM body_weight_log WHERE date >= :startDate AND date <= :endDate ORDER BY date ASC")
    fun getLogsBetween(startDate: Long, endDate: Long): Flow<List<BodyWeightLog>>

    @Insert
    suspend fun insert(log: BodyWeightLog)

    @Update
    suspend fun update(log: BodyWeightLog)

    @Delete
    suspend fun delete(log: BodyWeightLog)
}
