package com.forgefit.android.data.db.dao

import androidx.room.*
import com.forgefit.android.data.model.Routine
import kotlinx.coroutines.flow.Flow

@Dao
interface RoutineDao {
    @Query("SELECT * FROM routines ORDER BY createdAt DESC")
    fun getAllRoutines(): Flow<List<Routine>>

    @Query("SELECT * FROM routines WHERE id = :routineId")
    fun getRoutineById(routineId: Long): Flow<Routine?>

    @Query("SELECT * FROM routines WHERE isTemplate = 1 ORDER BY name ASC")
    fun getTemplates(): Flow<List<Routine>>

    @Query("SELECT * FROM routines WHERE isActive = 1 LIMIT 1")
    fun getActiveRoutine(): Flow<Routine?>

    @Insert
    suspend fun insert(routine: Routine): Long

    @Update
    suspend fun update(routine: Routine)

    @Delete
    suspend fun delete(routine: Routine)

    @Query("UPDATE routines SET isActive = 0")
    suspend fun clearAllActive()
}
