package com.forgefit.android.data.repository

import android.content.Context
import com.forgefit.android.data.db.dao.ExerciseDao
import com.forgefit.android.data.model.Exercise
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class ExerciseRepository(
    private val exerciseDao: ExerciseDao,
    private val context: Context
) {
    val allExercises: Flow<List<Exercise>> = exerciseDao.getAllExercises()

    suspend fun seedExercisesIfNeeded() = withContext(Dispatchers.IO) {
        val count = exerciseDao.getExerciseCount()
        if (count == 0) {
            val exercises = loadExercisesFromAssets()
            exerciseDao.insertAll(exercises)
        }
    }

    private fun loadExercisesFromAssets(): List<Exercise> {
        return try {
            val json = context.assets.open("exercises.json").bufferedReader().use { it.readText() }
            val type = object : TypeToken<Map<String, Any>>() {}.type
            val data: Map<String, Any> = Gson().fromJson(json, type)
            
            val exercisesArray = data["exercises"] as? List<Map<String, Any>> ?: emptyList()
            
            exercisesArray.map { exerciseMap ->
                Exercise(
                    id = exerciseMap["id"] as String,
                    name = exerciseMap["name"] as String,
                    primaryMuscles = (exerciseMap["primaryMuscles"] as? List<String>) ?: emptyList(),
                    secondaryMuscles = (exerciseMap["secondaryMuscles"] as? List<String>) ?: emptyList(),
                    equipment = (exerciseMap["equipment"] as? List<String>) ?: emptyList(),
                    category = exerciseMap["category"] as? String ?: "other",
                    instructions = exerciseMap["instructions"] as? String ?: "",
                    mediaUrl = exerciseMap["mediaUrl"] as? String,
                    isCustom = false
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getExerciseById(id: String): Exercise? = exerciseDao.getExerciseById(id)

    fun getExerciseByIdFlow(id: String): Flow<Exercise?> = exerciseDao.getExerciseByIdFlow(id)

    fun searchExercises(query: String): Flow<List<Exercise>> = exerciseDao.searchExercises(query)

    fun getExercisesByEquipment(equipmentTypes: List<String>): Flow<List<Exercise>> =
        exerciseDao.getExercisesByEquipment(equipmentTypes)

    suspend fun addCustomExercise(exercise: Exercise) = exerciseDao.insert(exercise)

    suspend fun deleteExercise(exercise: Exercise) = exerciseDao.delete(exercise)
}
