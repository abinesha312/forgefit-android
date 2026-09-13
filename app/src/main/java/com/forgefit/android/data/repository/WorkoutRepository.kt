package com.forgefit.android.data.repository

import com.forgefit.android.data.db.dao.WorkoutDao
import com.forgefit.android.data.model.Workout
import com.forgefit.android.data.model.WorkoutSet
import kotlinx.coroutines.flow.Flow

class WorkoutRepository(private val workoutDao: WorkoutDao) {
    
    val allWorkouts: Flow<List<Workout>> = workoutDao.getAllWorkouts()
    
    fun getWorkoutById(id: Long): Flow<Workout?> = workoutDao.getWorkoutById(id)
    
    suspend fun getWorkoutByIdOnce(id: Long): Workout? = workoutDao.getWorkoutByIdOnce(id)
    
    fun getWorkoutsBetween(startDate: Long, endDate: Long): Flow<List<Workout>> =
        workoutDao.getWorkoutsBetween(startDate, endDate)
    
    fun getRecentCompletedWorkouts(limit: Int): Flow<List<Workout>> =
        workoutDao.getRecentCompletedWorkouts(limit)
    
    suspend fun insertWorkout(workout: Workout): Long = workoutDao.insert(workout)
    
    suspend fun updateWorkout(workout: Workout) = workoutDao.update(workout)
    
    suspend fun deleteWorkout(workout: Workout) = workoutDao.delete(workout)
    
    fun getWorkoutSets(workoutId: Long): Flow<List<WorkoutSet>> = workoutDao.getWorkoutSets(workoutId)
    
    fun getSetsByExercise(exerciseId: String): Flow<List<WorkoutSet>> =
        workoutDao.getSetsByExercise(exerciseId)
    
    suspend fun getRecentSetsByExercise(exerciseId: String, limit: Int = 10): List<WorkoutSet> =
        workoutDao.getRecentSetsByExercise(exerciseId, limit)
    
    suspend fun insertSet(set: WorkoutSet): Long = workoutDao.insertSet(set)
    
    suspend fun updateSet(set: WorkoutSet) = workoutDao.updateSet(set)
    
    suspend fun deleteSet(set: WorkoutSet) = workoutDao.deleteSet(set)
    
    suspend fun deleteWorkoutSets(workoutId: Long) = workoutDao.deleteWorkoutSets(workoutId)
}
