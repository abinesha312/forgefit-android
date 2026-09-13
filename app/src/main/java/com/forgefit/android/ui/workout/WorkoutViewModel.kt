package com.forgefit.android.ui.workout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.forgefit.android.data.healthconnect.HealthConnectManager
import com.forgefit.android.data.model.*
import com.forgefit.android.data.repository.ExerciseRepository
import com.forgefit.android.data.repository.WorkoutRepository
import com.forgefit.android.data.db.dao.PersonalRecordDao
import com.forgefit.android.domain.PersonalRecordDetector
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.Instant

class WorkoutViewModel(
    private val workoutRepository: WorkoutRepository,
    private val exerciseRepository: ExerciseRepository,
    private val personalRecordDao: PersonalRecordDao,
    private val healthConnectManager: HealthConnectManager
) : ViewModel() {

    private val recordDetector = PersonalRecordDetector()

    private val _activeWorkout = MutableStateFlow<Workout?>(null)
    val activeWorkout: StateFlow<Workout?> = _activeWorkout.asStateFlow()

    private val _workoutSets = MutableStateFlow<List<WorkoutSet>>(emptyList())
    val workoutSets: StateFlow<List<WorkoutSet>> = _workoutSets.asStateFlow()

    private val _currentExerciseHistory = MutableStateFlow<List<WorkoutSet>>(emptyList())
    val currentExerciseHistory: StateFlow<List<WorkoutSet>> = _currentExerciseHistory.asStateFlow()

    fun startWorkout(name: String, exerciseIds: List<String>) {
        viewModelScope.launch {
            val workout = Workout(
                name = name,
                date = System.currentTimeMillis(),
                startTime = System.currentTimeMillis(),
                exerciseIds = exerciseIds
            )
            val id = workoutRepository.insertWorkout(workout)
            _activeWorkout.value = workout.copy(id = id)
        }
    }

    fun loadWorkout(workoutId: Long) {
        viewModelScope.launch {
            val workout = workoutRepository.getWorkoutByIdOnce(workoutId)
            _activeWorkout.value = workout
            
            if (workout != null) {
                workoutRepository.getWorkoutSets(workoutId).collect { sets ->
                    _workoutSets.value = sets
                }
            }
        }
    }

    fun addSet(exerciseId: String, weightKg: Float, reps: Int, rpe: Float? = null, isWarmup: Boolean = false) {
        viewModelScope.launch {
            val workoutId = _activeWorkout.value?.id ?: return@launch
            val setNumber = _workoutSets.value.count { it.exerciseId == exerciseId } + 1
            
            val set = WorkoutSet(
                workoutId = workoutId,
                exerciseId = exerciseId,
                setNumber = setNumber,
                weightKg = weightKg,
                reps = reps,
                rpe = rpe,
                isWarmup = isWarmup
            )
            
            workoutRepository.insertSet(set)
            loadCurrentExerciseHistory(exerciseId)
        }
    }

    fun loadCurrentExerciseHistory(exerciseId: String) {
        viewModelScope.launch {
            val history = workoutRepository.getRecentSetsByExercise(exerciseId, 10)
            _currentExerciseHistory.value = history
        }
    }

    fun completeWorkout() {
        viewModelScope.launch {
            val workout = _activeWorkout.value ?: return@launch
            val updatedWorkout = workout.copy(
                completed = true,
                endTime = System.currentTimeMillis(),
                totalVolume = calculateTotalVolume()
            )
            workoutRepository.updateWorkout(updatedWorkout)
            
            detectAndSaveRecords(workout.id)
            
            syncToHealthConnect(updatedWorkout)
            
            _activeWorkout.value = null
            _workoutSets.value = emptyList()
        }
    }

    private fun calculateTotalVolume(): Float {
        return _workoutSets.value
            .filter { !it.isWarmup }
            .sumOf { (it.weightKg * it.reps).toDouble() }
            .toFloat()
    }

    private suspend fun detectAndSaveRecords(workoutId: Long) {
        val exerciseGroups = _workoutSets.value.groupBy { it.exerciseId }
        
        exerciseGroups.forEach { (exerciseId, sets) ->
            val exercise = exerciseRepository.getExerciseById(exerciseId) ?: return@forEach
            val existingRecords = personalRecordDao.getRecordsByExercise(exerciseId).first()
            
            val detected = recordDetector.detectRecords(exerciseId, exercise.name, sets, existingRecords)
            
            detected.filter { it.isNewRecord }.forEach { record ->
                val pr = recordDetector.createPersonalRecord(exerciseId, exercise.name, record, workoutId)
                personalRecordDao.insert(pr)
            }
        }
    }

    private suspend fun syncToHealthConnect(workout: Workout) {
        if (!healthConnectManager.isHealthConnectAvailable()) return
        if (!healthConnectManager.hasAllPermissions()) return
        
        try {
            val startTime = Instant.ofEpochMilli(workout.startTime ?: return)
            val endTime = Instant.ofEpochMilli(workout.endTime ?: return)
            
            healthConnectManager.writeExerciseSession(
                startTime = startTime,
                endTime = endTime,
                title = workout.name,
                notes = "Volume: ${workout.totalVolume}kg"
            )
        } catch (e: Exception) {
        }
    }
}
