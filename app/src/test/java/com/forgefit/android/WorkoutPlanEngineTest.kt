package com.forgefit.android

import com.forgefit.android.data.model.*
import com.forgefit.android.domain.WorkoutPlanEngine
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class WorkoutPlanEngineTest {

    private lateinit var planEngine: WorkoutPlanEngine
    private lateinit var sampleExercises: List<Exercise>

    @Before
    fun setup() {
        planEngine = WorkoutPlanEngine()
        sampleExercises = listOf(
            Exercise(
                id = "squat",
                name = "Squat",
                primaryMuscles = listOf("quadriceps", "glutes"),
                secondaryMuscles = listOf("hamstrings"),
                equipment = listOf("barbell"),
                category = "strength",
                instructions = "Test"
            ),
            Exercise(
                id = "bench",
                name = "Bench Press",
                primaryMuscles = listOf("chest"),
                secondaryMuscles = listOf("triceps"),
                equipment = listOf("barbell"),
                category = "strength",
                instructions = "Test"
            ),
            Exercise(
                id = "row",
                name = "Row",
                primaryMuscles = listOf("back", "lats"),
                secondaryMuscles = listOf("biceps"),
                equipment = listOf("barbell"),
                category = "strength",
                instructions = "Test"
            )
        )
    }

    @Test
    fun `test full body split generation for 3 days`() {
        val profile = UserProfile(
            displayName = "Test",
            age = 25,
            heightCm = 170f,
            weightKg = 70f,
            goal = TrainingGoal.GENERAL_FITNESS,
            experience = ExperienceLevel.BEGINNER,
            equipment = EquipmentType.FULL_GYM,
            trainingDaysPerWeek = 3,
            sessionLengthMinutes = 60
        )

        val plan = planEngine.generateWorkoutPlan(profile, sampleExercises)

        assertEquals("Full Body", plan.splitType)
        assertEquals(3, plan.weeklyWorkouts.size)
        assertTrue(plan.weeklyWorkouts.all { it.exercises.isNotEmpty() })
    }

    @Test
    fun `test strength goal has appropriate sets and reps`() {
        val profile = UserProfile(
            displayName = "Test",
            age = 25,
            heightCm = 170f,
            weightKg = 70f,
            goal = TrainingGoal.STRENGTH,
            experience = ExperienceLevel.INTERMEDIATE,
            equipment = EquipmentType.FULL_GYM,
            trainingDaysPerWeek = 3,
            sessionLengthMinutes = 60
        )

        val plan = planEngine.generateWorkoutPlan(profile, sampleExercises)

        val firstWorkout = plan.weeklyWorkouts.first()
        val firstExercise = firstWorkout.exercises.first()
        
        assertTrue(firstExercise.sets >= 3)
        assertTrue(firstExercise.reps.contains("3") || firstExercise.reps.contains("5"))
    }

    @Test
    fun `test push pull legs split for 6 days`() {
        val profile = UserProfile(
            displayName = "Test",
            age = 25,
            heightCm = 170f,
            weightKg = 70f,
            goal = TrainingGoal.HYPERTROPHY,
            experience = ExperienceLevel.ADVANCED,
            equipment = EquipmentType.FULL_GYM,
            trainingDaysPerWeek = 6,
            sessionLengthMinutes = 90
        )

        val plan = planEngine.generateWorkoutPlan(profile, sampleExercises)

        assertEquals("Push/Pull/Legs", plan.splitType)
        assertTrue(plan.weeklyWorkouts.size >= 3)
    }
}
