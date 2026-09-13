package com.forgefit.android.ui.workout

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.forgefit.android.ForgeFitApplication

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutScreen(app: ForgeFitApplication) {
    val viewModel = remember {
        WorkoutViewModel(
            app.workoutRepository,
            app.exerciseRepository,
            app.database.personalRecordDao(),
            app.healthConnectManager
        )
    }
    
    val activeWorkout by viewModel.activeWorkout.collectAsState()
    val workoutSets by viewModel.workoutSets.collectAsState()
    
    var showStartDialog by remember { mutableStateOf(false) }
    var workoutName by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Workout") }
            )
        },
        floatingActionButton = {
            if (activeWorkout == null) {
                FloatingActionButton(onClick = { showStartDialog = true }) {
                    Text("Start")
                }
            } else {
                FloatingActionButton(onClick = { viewModel.completeWorkout() }) {
                    Text("Finish")
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (activeWorkout != null) {
                ActiveWorkoutView(
                    workout = activeWorkout!!,
                    sets = workoutSets,
                    onAddSet = { exerciseId, weight, reps, rpe, isWarmup ->
                        viewModel.addSet(exerciseId, weight, reps, rpe, isWarmup)
                    }
                )
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        "No active workout",
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        "Tap the button to start a workout",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }

        if (showStartDialog) {
            AlertDialog(
                onDismissRequest = { showStartDialog = false },
                title = { Text("Start Workout") },
                text = {
                    OutlinedTextField(
                        value = workoutName,
                        onValueChange = { workoutName = it },
                        label = { Text("Workout Name") }
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            if (workoutName.isNotBlank()) {
                                viewModel.startWorkout(workoutName, emptyList())
                                showStartDialog = false
                                workoutName = ""
                            }
                        }
                    ) {
                        Text("Start")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showStartDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
fun ActiveWorkoutView(
    workout: com.forgefit.android.data.model.Workout,
    sets: List<com.forgefit.android.data.model.WorkoutSet>,
    onAddSet: (String, Float, Int, Float?, Boolean) -> Unit
) {
    var weight by remember { mutableStateOf("") }
    var reps by remember { mutableStateOf("") }
    var exerciseId by remember { mutableStateOf("barbell_bench_press") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Text(workout.name, style = MaterialTheme.typography.headlineMedium)
            Text("${sets.size} sets logged", style = MaterialTheme.typography.bodyMedium)
            Divider(modifier = Modifier.padding(vertical = 8.dp))
        }

        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Add Set", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    OutlinedTextField(
                        value = weight,
                        onValueChange = { weight = it },
                        label = { Text("Weight (kg)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    OutlinedTextField(
                        value = reps,
                        onValueChange = { reps = it },
                        label = { Text("Reps") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Button(
                        onClick = {
                            val w = weight.toFloatOrNull()
                            val r = reps.toIntOrNull()
                            if (w != null && r != null) {
                                onAddSet(exerciseId, w, r, null, false)
                                weight = ""
                                reps = ""
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Add Set")
                    }
                }
            }
        }

        item {
            Text("Sets", style = MaterialTheme.typography.titleMedium)
        }

        items(sets) { set ->
            SetCard(set = set)
        }
    }
}

@Composable
fun SetCard(set: com.forgefit.android.data.model.WorkoutSet) {
    OutlinedCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Set ${set.setNumber}")
            Text("${set.weightKg}kg × ${set.reps} reps")
        }
    }
}
