package com.forgefit.android.ui.progress

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.forgefit.android.data.db.dao.BodyWeightLogDao
import com.forgefit.android.data.db.dao.PersonalRecordDao
import com.forgefit.android.data.model.BodyWeightLog
import com.forgefit.android.data.model.PersonalRecord
import com.forgefit.android.data.repository.WorkoutRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId

class ProgressViewModel(
    private val workoutRepository: WorkoutRepository,
    private val personalRecordDao: PersonalRecordDao,
    private val bodyWeightLogDao: BodyWeightLogDao
) : ViewModel() {

    val allRecords: StateFlow<List<PersonalRecord>> = personalRecordDao.getAllRecords()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val weightLogs: StateFlow<List<BodyWeightLog>> = bodyWeightLogDao.getAllLogs()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val workoutStreak: StateFlow<Int> = flow {
        val workouts = workoutRepository.allWorkouts.first()
        val streak = calculateStreak(workouts.map { it.date })
        emit(streak)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    fun addWeightLog(weightKg: Float, notes: String? = null) {
        viewModelScope.launch {
            val log = BodyWeightLog(
                weightKg = weightKg,
                date = System.currentTimeMillis(),
                notes = notes
            )
            bodyWeightLogDao.insert(log)
        }
    }

    private fun calculateStreak(dates: List<Long>): Int {
        if (dates.isEmpty()) return 0
        
        val sortedDates = dates.map { 
            LocalDate.ofInstant(java.time.Instant.ofEpochMilli(it), ZoneId.systemDefault())
        }.sorted().reversed()
        
        var streak = 0
        var currentDate = LocalDate.now()
        
        for (date in sortedDates) {
            if (date == currentDate || date == currentDate.minusDays(1)) {
                streak++
                currentDate = date.minusDays(1)
            } else {
                break
            }
        }
        
        return streak
    }
}
