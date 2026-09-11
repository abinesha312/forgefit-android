package com.forgefit.android

import android.app.Application
import com.forgefit.android.data.db.ForgeFitDatabase
import com.forgefit.android.data.healthconnect.HealthConnectManager
import com.forgefit.android.data.repository.ExerciseRepository
import com.forgefit.android.data.repository.UserRepository
import com.forgefit.android.data.repository.WorkoutRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class ForgeFitApplication : Application() {
    
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    
    val database by lazy { ForgeFitDatabase.getInstance(this) }
    
    val userRepository by lazy { UserRepository(database.userProfileDao()) }
    val exerciseRepository by lazy { ExerciseRepository(database.exerciseDao(), this) }
    val workoutRepository by lazy { WorkoutRepository(database.workoutDao()) }
    val healthConnectManager by lazy { HealthConnectManager(this) }
    
    override fun onCreate() {
        super.onCreate()
        
        applicationScope.launch(Dispatchers.IO) {
            exerciseRepository.seedExercisesIfNeeded()
        }
    }
}
