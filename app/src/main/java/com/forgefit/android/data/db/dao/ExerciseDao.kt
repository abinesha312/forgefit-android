package com.forgefit.android.data.db.dao

import androidx.room.*
import com.forgefit.android.data.model.Exercise
import kotlinx.coroutines.flow.Flow

@Dao
interface ExerciseDao {
    @Query("SELECT * FROM exercises ORDER BY name ASC")
    fun getAllExercises(): Flow<List<Exercise>>

    @Query("SELECT * FROM exercises WHERE id = :exerciseId")
    suspend fun getExerciseById(exerciseId: String): Exercise?

    @Query("SELECT * FROM exercises WHERE id = :exerciseId")
    fun getExerciseByIdFlow(exerciseId: String): Flow<Exercise?>

    @Query("""
        SELECT * FROM exercises 
        WHERE name LIKE '%' || :query || '%' 
        OR category LIKE '%' || :query || '%'
        ORDER BY name ASC
    """)
    fun searchExercises(query: String): Flow<List<Exercise>>

    @Query("""
        SELECT * FROM exercises 
        WHERE equipment IN (:equipmentTypes)
        ORDER BY name ASC
    """)
    fun getExercisesByEquipment(equipmentTypes: List<String>): Flow<List<Exercise>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(exercises: List<Exercise>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(exercise: Exercise)

    @Delete
    suspend fun delete(exercise: Exercise)

    @Query("DELETE FROM exercises WHERE isCustom = 1")
    suspend fun deleteAllCustom()

    @Query("SELECT COUNT(*) FROM exercises")
    suspend fun getExerciseCount(): Int
}
