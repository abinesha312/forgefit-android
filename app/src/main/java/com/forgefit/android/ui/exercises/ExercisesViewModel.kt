package com.forgefit.android.ui.exercises

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.forgefit.android.data.model.Exercise
import com.forgefit.android.data.repository.ExerciseRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ExercisesViewModel(
    private val exerciseRepository: ExerciseRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val exercises: StateFlow<List<Exercise>> = _searchQuery
        .flatMapLatest { query ->
            if (query.isBlank()) {
                exerciseRepository.allExercises
            } else {
                exerciseRepository.searchExercises(query)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun addCustomExercise(exercise: Exercise) {
        viewModelScope.launch {
            exerciseRepository.addCustomExercise(exercise.copy(isCustom = true))
        }
    }
}
