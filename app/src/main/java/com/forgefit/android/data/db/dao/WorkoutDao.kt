package com.forgefit.android.data.db.dao

import androidx.room.*
import com.forgefit.android.data.model.Workout
import com.forgefit.android.data.model.WorkoutSet
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutDao {
    @Query("SELECT * FROM workouts ORDER BY date DESC")
    fun getAllWorkouts(): Flow<List<Workout>>

    @Query("SELECT * FROM workouts WHERE id = :workoutId")
    fun getWorkoutById(workoutId: Long): Flow<Workout?>

    @Query("SELECT * FROM workouts WHERE id = :workoutId")
    suspend fun getWorkoutByIdOnce(workoutId: Long): Workout?

    @Query("SELECT * FROM workouts WHERE date >= :startDate AND date <= :endDate ORDER BY date ASC")
    fun getWorkoutsBetween(startDate: Long, endDate: Long): Flow<List<Workout>>

    @Query("SELECT * FROM workouts WHERE completed = 1 ORDER BY date DESC LIMIT :limit")
    fun getRecentCompletedWorkouts(limit: Int): Flow<List<Workout>>

    @Insert
    suspend fun insert(workout: Workout): Long

    @Update
    suspend fun update(workout: Workout)

    @Delete
    suspend fun delete(workout: Workout)

    @Query("SELECT * FROM workout_sets WHERE workoutId = :workoutId ORDER BY setNumber ASC")
    fun getWorkoutSets(workoutId: Long): Flow<List<WorkoutSet>>

    @Query("SELECT * FROM workout_sets WHERE exerciseId = :exerciseId ORDER BY timestamp DESC")
    fun getSetsByExercise(exerciseId: String): Flow<List<WorkoutSet>>

    @Query("""
        SELECT * FROM workout_sets 
        WHERE exerciseId = :exerciseId 
        ORDER BY timestamp DESC 
        LIMIT :limit
    """)
    suspend fun getRecentSetsByExercise(exerciseId: String, limit: Int): List<WorkoutSet>

    @Insert
    suspend fun insertSet(set: WorkoutSet): Long

    @Update
    suspend fun updateSet(set: WorkoutSet)

    @Delete
    suspend fun deleteSet(set: WorkoutSet)

    @Query("DELETE FROM workout_sets WHERE workoutId = :workoutId")
    suspend fun deleteWorkoutSets(workoutId: Long)
}
