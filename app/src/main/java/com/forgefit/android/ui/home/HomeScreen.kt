package com.forgefit.android.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.forgefit.android.ForgeFitApplication
import com.forgefit.android.domain.WorkoutPlanEngine

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(app: ForgeFitApplication) {
    val viewModel = remember {
        HomeViewModel(
            app.userRepository,
            app.workoutRepository,
            app.exerciseRepository,
            WorkoutPlanEngine()
        )
    }
    
    val userProfile by viewModel.userProfile.collectAsState()
    val todayWorkout by viewModel.todayWorkout.collectAsState()
    val weeklyPlan by viewModel.weeklyPlan.collectAsState()
    val recentWorkouts by viewModel.recentWorkouts.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Home") }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                userProfile?.let { profile ->
                    WelcomeCard(displayName = profile.displayName)
                }
            }

            item {
                todayWorkout?.let { workout ->
                    TodayWorkoutCard(workout = workout)
                } ?: run {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("No workout scheduled for today", style = MaterialTheme.typography.titleMedium)
                            Text("Rest day or create a custom workout!", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }

            if (weeklyPlan.isNotEmpty()) {
                item {
                    Text("This Week's Plan", style = MaterialTheme.typography.titleLarge)
                }
                
                items(weeklyPlan) { workout ->
                    WeeklyWorkoutCard(workout = workout)
                }
            }

            if (recentWorkouts.isNotEmpty()) {
                item {
                    Text("Recent Workouts", style = MaterialTheme.typography.titleLarge)
                }
                
                items(recentWorkouts.take(5)) { workout ->
                    RecentWorkoutCard(workout = workout)
                }
            }
        }
    }
}

@Composable
fun WelcomeCard(displayName: String) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Welcome back, $displayName!", style = MaterialTheme.typography.headlineSmall)
            Text("Ready to crush your goals today?", style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
fun TodayWorkoutCard(workout: WorkoutPlanEngine.PlannedWorkout) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Today's Workout", style = MaterialTheme.typography.labelSmall)
            Text(workout.name, style = MaterialTheme.typography.titleLarge)
            Text("${workout.exercises.size} exercises", style = MaterialTheme.typography.bodyMedium)
            
            Spacer(modifier = Modifier.height(8.dp))
            
            workout.exercises.take(3).forEach { exercise ->
                Text(
                    "• ${exercise.sets} sets × ${exercise.reps} reps",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
fun WeeklyWorkoutCard(workout: WorkoutPlanEngine.PlannedWorkout) {
    OutlinedCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text("Day ${workout.dayOfWeek}: ${workout.name}", style = MaterialTheme.typography.titleMedium)
            Text("${workout.exercises.size} exercises", style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
fun RecentWorkoutCard(workout: com.forgefit.android.data.model.Workout) {
    OutlinedCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(workout.name, style = MaterialTheme.typography.titleMedium)
            Text("Volume: ${workout.totalVolume.toInt()}kg", style = MaterialTheme.typography.bodySmall)
        }
    }
}
