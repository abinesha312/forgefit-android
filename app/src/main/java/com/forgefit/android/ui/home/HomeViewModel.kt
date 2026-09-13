package com.forgefit.android.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.forgefit.android.data.model.UserProfile
import com.forgefit.android.data.model.Workout
import com.forgefit.android.data.repository.ExerciseRepository
import com.forgefit.android.data.repository.UserRepository
import com.forgefit.android.data.repository.WorkoutRepository
import com.forgefit.android.domain.WorkoutPlanEngine
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId

class HomeViewModel(
    private val userRepository: UserRepository,
    private val workoutRepository: WorkoutRepository,
    private val exerciseRepository: ExerciseRepository,
    private val planEngine: WorkoutPlanEngine
) : ViewModel() {

    val userProfile: StateFlow<UserProfile?> = userRepository.getUserProfile()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val recentWorkouts: StateFlow<List<Workout>> = workoutRepository.getRecentCompletedWorkouts(7)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _todayWorkout = MutableStateFlow<WorkoutPlanEngine.PlannedWorkout?>(null)
    val todayWorkout: StateFlow<WorkoutPlanEngine.PlannedWorkout?> = _todayWorkout.asStateFlow()

    private val _weeklyPlan = MutableStateFlow<List<WorkoutPlanEngine.PlannedWorkout>>(emptyList())
    val weeklyPlan: StateFlow<List<WorkoutPlanEngine.PlannedWorkout>> = _weeklyPlan.asStateFlow()

    init {
        generateTodayWorkout()
    }

    private fun generateTodayWorkout() {
        viewModelScope.launch {
            val profile = userRepository.getUserProfileOnce()
            if (profile != null) {
                exerciseRepository.allExercises.first().let { exercises ->
                    val plan = planEngine.generateWorkoutPlan(profile, exercises)
                    _weeklyPlan.value = plan.weeklyWorkouts
                    
                    val today = LocalDate.now().dayOfWeek.value
                    _todayWorkout.value = plan.weeklyWorkouts.find { it.dayOfWeek == today }
                        ?: plan.weeklyWorkouts.firstOrNull()
                }
            }
        }
    }

    fun regeneratePlan() {
        generateTodayWorkout()
    }
}
