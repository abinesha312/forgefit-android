package com.forgefit.android.ui.onboarding

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.forgefit.android.ForgeFitApplication
import com.forgefit.android.data.model.*
import com.forgefit.android.domain.WorkoutPlanEngine

@Composable
fun OnboardingScreen(
    app: ForgeFitApplication,
    onComplete: () -> Unit
) {
    val viewModel = remember {
        OnboardingViewModel(
            app.userRepository,
            app.exerciseRepository,
            WorkoutPlanEngine()
        )
    }
    
    val state by viewModel.uiState.collectAsState()
    var currentStep by remember { mutableStateOf(0) }
    
    LaunchedEffect(state.isComplete) {
        if (state.isComplete) {
            onComplete()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Welcome to ForgeFit",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(vertical = 24.dp)
        )

        when (currentStep) {
            0 -> DisplayNameStep(state.displayName) { viewModel.updateDisplayName(it) }
            1 -> AgeStep(state.age) { viewModel.updateAge(it) }
            2 -> HeightWeightStep(
                heightCm = state.heightCm,
                weightKg = state.weightKg,
                onHeightChange = { viewModel.updateHeightCm(it) },
                onWeightChange = { viewModel.updateWeightKg(it) }
            )
            3 -> GoalStep(state.goal) { viewModel.updateGoal(it) }
            4 -> ExperienceStep(state.experience) { viewModel.updateExperience(it) }
            5 -> EquipmentStep(state.equipment) { viewModel.updateEquipment(it) }
            6 -> TrainingDaysStep(state.trainingDaysPerWeek) { viewModel.updateTrainingDays(it) }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (currentStep > 0) {
                OutlinedButton(onClick = { currentStep-- }) {
                    Text("Back")
                }
            } else {
                Spacer(modifier = Modifier.width(1.dp))
            }

            Button(
                onClick = {
                    if (currentStep < 6) {
                        currentStep++
                    } else {
                        viewModel.completeOnboarding()
                    }
                },
                enabled = when (currentStep) {
                    0 -> state.displayName.isNotBlank()
                    else -> true
                }
            ) {
                Text(if (currentStep == 6) "Finish" else "Next")
            }
        }
    }
}

@Composable
fun DisplayNameStep(name: String, onNameChange: (String) -> Unit) {
    Column {
        Text("What should we call you?", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = name,
            onValueChange = onNameChange,
            label = { Text("Display Name") },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun AgeStep(age: Int, onAgeChange: (Int) -> Unit) {
    Column {
        Text("How old are you?", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = age.toString(),
            onValueChange = { it.toIntOrNull()?.let(onAgeChange) },
            label = { Text("Age") },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun HeightWeightStep(
    heightCm: Float,
    weightKg: Float,
    onHeightChange: (Float) -> Unit,
    onWeightChange: (Float) -> Unit
) {
    Column {
        Text("Height & Weight", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = heightCm.toInt().toString(),
            onValueChange = { it.toFloatOrNull()?.let(onHeightChange) },
            label = { Text("Height (cm)") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = weightKg.toInt().toString(),
            onValueChange = { it.toFloatOrNull()?.let(onWeightChange) },
            label = { Text("Weight (kg)") },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun GoalStep(goal: TrainingGoal, onGoalChange: (TrainingGoal) -> Unit) {
    Column {
        Text("What's your primary goal?", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(16.dp))
        TrainingGoal.values().forEach { g ->
            FilterChip(
                selected = goal == g,
                onClick = { onGoalChange(g) },
                label = { Text(g.name.replace("_", " ")) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            )
        }
    }
}

@Composable
fun ExperienceStep(experience: ExperienceLevel, onExperienceChange: (ExperienceLevel) -> Unit) {
    Column {
        Text("Training experience?", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(16.dp))
        ExperienceLevel.values().forEach { exp ->
            FilterChip(
                selected = experience == exp,
                onClick = { onExperienceChange(exp) },
                label = { Text(exp.name) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            )
        }
    }
}

@Composable
fun EquipmentStep(equipment: EquipmentType, onEquipmentChange: (EquipmentType) -> Unit) {
    Column {
        Text("Available equipment?", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(16.dp))
        EquipmentType.values().forEach { eq ->
            FilterChip(
                selected = equipment == eq,
                onClick = { onEquipmentChange(eq) },
                label = { Text(eq.name.replace("_", " ")) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            )
        }
    }
}

@Composable
fun TrainingDaysStep(days: Int, onDaysChange: (Int) -> Unit) {
    Column {
        Text("Days per week?", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(16.dp))
        (2..6).forEach { d ->
            FilterChip(
                selected = days == d,
                onClick = { onDaysChange(d) },
                label = { Text("$d days per week") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            )
        }
    }
}
