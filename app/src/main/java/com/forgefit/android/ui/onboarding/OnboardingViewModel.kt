package com.forgefit.android.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.forgefit.android.data.model.*
import com.forgefit.android.data.repository.ExerciseRepository
import com.forgefit.android.data.repository.UserRepository
import com.forgefit.android.domain.WorkoutPlanEngine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class OnboardingViewModel(
    private val userRepository: UserRepository,
    private val exerciseRepository: ExerciseRepository,
    private val planEngine: WorkoutPlanEngine
) : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingState())
    val uiState: StateFlow<OnboardingState> = _uiState.asStateFlow()

    fun updateDisplayName(name: String) {
        _uiState.value = _uiState.value.copy(displayName = name)
    }

    fun updateSex(sex: String?) {
        _uiState.value = _uiState.value.copy(sex = sex)
    }

    fun updateAge(age: Int) {
        _uiState.value = _uiState.value.copy(age = age)
    }

    fun updateHeightCm(height: Float) {
        _uiState.value = _uiState.value.copy(heightCm = height)
    }

    fun updateWeightKg(weight: Float) {
        _uiState.value = _uiState.value.copy(weightKg = weight)
    }

    fun updateGoal(goal: TrainingGoal) {
        _uiState.value = _uiState.value.copy(goal = goal)
    }

    fun updateExperience(experience: ExperienceLevel) {
        _uiState.value = _uiState.value.copy(experience = experience)
    }

    fun updateEquipment(equipment: EquipmentType) {
        _uiState.value = _uiState.value.copy(equipment = equipment)
    }

    fun updateTrainingDays(days: Int) {
        _uiState.value = _uiState.value.copy(trainingDaysPerWeek = days)
    }

    fun updateSessionLength(minutes: Int) {
        _uiState.value = _uiState.value.copy(sessionLengthMinutes = minutes)
    }

    fun completeOnboarding() {
        viewModelScope.launch {
            val state = _uiState.value
            val profile = UserProfile(
                displayName = state.displayName,
                sex = state.sex,
                age = state.age,
                heightCm = state.heightCm,
                weightKg = state.weightKg,
                goal = state.goal,
                experience = state.experience,
                equipment = state.equipment,
                trainingDaysPerWeek = state.trainingDaysPerWeek,
                sessionLengthMinutes = state.sessionLengthMinutes,
                onboardingCompleted = true
            )
            userRepository.saveUserProfile(profile)
            _uiState.value = _uiState.value.copy(isComplete = true)
        }
    }
}

data class OnboardingState(
    val displayName: String = "",
    val sex: String? = null,
    val age: Int = 25,
    val heightCm: Float = 170f,
    val weightKg: Float = 70f,
    val goal: TrainingGoal = TrainingGoal.GENERAL_FITNESS,
    val experience: ExperienceLevel = ExperienceLevel.BEGINNER,
    val equipment: EquipmentType = EquipmentType.FULL_GYM,
    val trainingDaysPerWeek: Int = 3,
    val sessionLengthMinutes: Int = 60,
    val isComplete: Boolean = false
)
