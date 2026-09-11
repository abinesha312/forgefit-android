package com.forgefit.android.domain

import com.forgefit.android.data.model.*
import kotlin.random.Random

class WorkoutPlanEngine {
    
    data class WorkoutPlan(
        val weeklyWorkouts: List<PlannedWorkout>,
        val splitType: String
    )
    
    data class PlannedWorkout(
        val dayOfWeek: Int,
        val name: String,
        val exercises: List<PlannedExercise>
    )
    
    data class PlannedExercise(
        val exerciseId: String,
        val sets: Int,
        val reps: String,
        val restSeconds: Int,
        val notes: String? = null
    )

    fun generateWorkoutPlan(
        profile: UserProfile,
        exercises: List<Exercise>
    ): WorkoutPlan {
        val equipmentFilter = getEquipmentFilter(profile.equipment)
        val availableExercises = exercises.filter { exercise ->
            exercise.equipment.any { it in equipmentFilter }
        }

        val splitType = determineSplit(profile.trainingDaysPerWeek, profile.goal)
        val weeklyWorkouts = when (splitType) {
            "Full Body" -> generateFullBodySplit(profile, availableExercises)
            "Upper/Lower" -> generateUpperLowerSplit(profile, availableExercises)
            "Push/Pull/Legs" -> generatePushPullLegsSplit(profile, availableExercises)
            "4-Day Split" -> generate4DaySplit(profile, availableExercises)
            else -> generateFullBodySplit(profile, availableExercises)
        }

        return WorkoutPlan(weeklyWorkouts, splitType)
    }

    private fun determineSplit(daysPerWeek: Int, goal: TrainingGoal): String {
        return when {
            daysPerWeek <= 2 -> "Full Body"
            daysPerWeek == 3 -> when (goal) {
                TrainingGoal.STRENGTH -> "Full Body"
                else -> "Push/Pull/Legs"
            }
            daysPerWeek == 4 -> "Upper/Lower"
            daysPerWeek >= 5 -> "Push/Pull/Legs"
            else -> "Full Body"
        }
    }

    private fun getEquipmentFilter(equipment: EquipmentType): List<String> {
        return when (equipment) {
            EquipmentType.FULL_GYM -> listOf("barbell", "dumbbells", "machine", "cable", "bodyweight", "pull_up_bar", "bench")
            EquipmentType.DUMBBELLS -> listOf("dumbbells", "bodyweight", "bench")
            EquipmentType.BODYWEIGHT -> listOf("bodyweight", "pull_up_bar")
            EquipmentType.HOME_LIMITED -> listOf("dumbbells", "bodyweight", "pull_up_bar", "bench")
        }
    }

    private fun generateFullBodySplit(
        profile: UserProfile,
        exercises: List<Exercise>
    ): List<PlannedWorkout> {
        val workouts = mutableListOf<PlannedWorkout>()
        val daysPerWeek = profile.trainingDaysPerWeek

        for (day in 1..daysPerWeek) {
            val plannedExercises = selectFullBodyExercises(exercises, profile)
            workouts.add(
                PlannedWorkout(
                    dayOfWeek = day,
                    name = "Full Body Workout $day",
                    exercises = plannedExercises
                )
            )
        }

        return workouts
    }

    private fun selectFullBodyExercises(
        exercises: List<Exercise>,
        profile: UserProfile
    ): List<PlannedExercise> {
        val selected = mutableListOf<PlannedExercise>()
        
        val (sets, reps, rest) = getSetsRepsRest(profile.goal, profile.experience)

        val squat = findExercise(exercises, listOf("quadriceps", "glutes"), primary = true)
        val push = findExercise(exercises, listOf("chest"), primary = true)
        val pull = findExercise(exercises, listOf("lats", "back"), primary = true)
        val hinge = findExercise(exercises, listOf("hamstrings", "glutes"), primary = true)
        val shoulder = findExercise(exercises, listOf("shoulders"), primary = true)
        val bicep = findExercise(exercises, listOf("biceps"), primary = true)
        val tricep = findExercise(exercises, listOf("triceps"), primary = true)
        val core = findExercise(exercises, listOf("abs"), primary = true)

        listOfNotNull(squat, push, pull, hinge, shoulder, bicep, tricep, core).forEach { exercise ->
            selected.add(
                PlannedExercise(
                    exerciseId = exercise.id,
                    sets = sets,
                    reps = reps,
                    restSeconds = rest
                )
            )
        }

        return selected
    }

    private fun generateUpperLowerSplit(
        profile: UserProfile,
        exercises: List<Exercise>
    ): List<PlannedWorkout> {
        val workouts = mutableListOf<PlannedWorkout>()
        val (sets, reps, rest) = getSetsRepsRest(profile.goal, profile.experience)

        val upperExercises = selectUpperBodyExercises(exercises, sets, reps, rest)
        val lowerExercises = selectLowerBodyExercises(exercises, sets, reps, rest)

        workouts.add(PlannedWorkout(1, "Upper Body", upperExercises))
        workouts.add(PlannedWorkout(2, "Lower Body", lowerExercises))
        
        if (profile.trainingDaysPerWeek >= 4) {
            workouts.add(PlannedWorkout(3, "Upper Body", upperExercises))
            workouts.add(PlannedWorkout(4, "Lower Body", lowerExercises))
        }

        return workouts
    }

    private fun generatePushPullLegsSplit(
        profile: UserProfile,
        exercises: List<Exercise>
    ): List<PlannedWorkout> {
        val workouts = mutableListOf<PlannedWorkout>()
        val (sets, reps, rest) = getSetsRepsRest(profile.goal, profile.experience)

        val pushExercises = selectPushExercises(exercises, sets, reps, rest)
        val pullExercises = selectPullExercises(exercises, sets, reps, rest)
        val legExercises = selectLegExercises(exercises, sets, reps, rest)

        workouts.add(PlannedWorkout(1, "Push", pushExercises))
        workouts.add(PlannedWorkout(2, "Pull", pullExercises))
        workouts.add(PlannedWorkout(3, "Legs", legExercises))
        
        if (profile.trainingDaysPerWeek >= 6) {
            workouts.add(PlannedWorkout(4, "Push", pushExercises))
            workouts.add(PlannedWorkout(5, "Pull", pullExercises))
            workouts.add(PlannedWorkout(6, "Legs", legExercises))
        }

        return workouts
    }

    private fun generate4DaySplit(
        profile: UserProfile,
        exercises: List<Exercise>
    ): List<PlannedWorkout> {
        val workouts = mutableListOf<PlannedWorkout>()
        val (sets, reps, rest) = getSetsRepsRest(profile.goal, profile.experience)

        workouts.add(PlannedWorkout(1, "Chest & Triceps", selectChestTricepsExercises(exercises, sets, reps, rest)))
        workouts.add(PlannedWorkout(2, "Back & Biceps", selectBackBicepsExercises(exercises, sets, reps, rest)))
        workouts.add(PlannedWorkout(3, "Legs", selectLegExercises(exercises, sets, reps, rest)))
        workouts.add(PlannedWorkout(4, "Shoulders & Arms", selectShoulderArmExercises(exercises, sets, reps, rest)))

        return workouts
    }

    private fun selectPushExercises(exercises: List<Exercise>, sets: Int, reps: String, rest: Int): List<PlannedExercise> {
        val selected = mutableListOf<PlannedExercise>()
        
        val chest1 = findExercise(exercises, listOf("chest"), primary = true)
        val chest2 = findExercise(exercises, listOf("chest"), primary = true, exclude = listOfNotNull(chest1))
        val shoulder = findExercise(exercises, listOf("shoulders"), primary = true)
        val tricep1 = findExercise(exercises, listOf("triceps"), primary = true)
        val tricep2 = findExercise(exercises, listOf("triceps"), primary = true, exclude = listOfNotNull(tricep1))

        listOfNotNull(chest1, chest2, shoulder, tricep1, tricep2).forEach { exercise ->
            selected.add(PlannedExercise(exercise.id, sets, reps, rest))
        }

        return selected
    }

    private fun selectPullExercises(exercises: List<Exercise>, sets: Int, reps: String, rest: Int): List<PlannedExercise> {
        val selected = mutableListOf<PlannedExercise>()
        
        val back1 = findExercise(exercises, listOf("lats", "back"), primary = true)
        val back2 = findExercise(exercises, listOf("lats", "back"), primary = true, exclude = listOfNotNull(back1))
        val row = findExercise(exercises, listOf("back"), primary = true, exclude = listOfNotNull(back1, back2))
        val bicep1 = findExercise(exercises, listOf("biceps"), primary = true)
        val bicep2 = findExercise(exercises, listOf("biceps"), primary = true, exclude = listOfNotNull(bicep1))

        listOfNotNull(back1, back2, row, bicep1, bicep2).forEach { exercise ->
            selected.add(PlannedExercise(exercise.id, sets, reps, rest))
        }

        return selected
    }

    private fun selectLegExercises(exercises: List<Exercise>, sets: Int, reps: String, rest: Int): List<PlannedExercise> {
        val selected = mutableListOf<PlannedExercise>()
        
        val squat = findExercise(exercises, listOf("quadriceps", "glutes"), primary = true)
        val hinge = findExercise(exercises, listOf("hamstrings", "glutes"), primary = true)
        val quad = findExercise(exercises, listOf("quadriceps"), primary = true, exclude = listOfNotNull(squat))
        val hamstring = findExercise(exercises, listOf("hamstrings"), primary = true, exclude = listOfNotNull(hinge))
        val calf = findExercise(exercises, listOf("calves"), primary = true)

        listOfNotNull(squat, hinge, quad, hamstring, calf).forEach { exercise ->
            selected.add(PlannedExercise(exercise.id, sets, reps, rest))
        }

        return selected
    }

    private fun selectUpperBodyExercises(exercises: List<Exercise>, sets: Int, reps: String, rest: Int): List<PlannedExercise> {
        val selected = mutableListOf<PlannedExercise>()
        
        val chest = findExercise(exercises, listOf("chest"), primary = true)
        val back = findExercise(exercises, listOf("lats", "back"), primary = true)
        val shoulder = findExercise(exercises, listOf("shoulders"), primary = true)
        val bicep = findExercise(exercises, listOf("biceps"), primary = true)
        val tricep = findExercise(exercises, listOf("triceps"), primary = true)

        listOfNotNull(chest, back, shoulder, bicep, tricep).forEach { exercise ->
            selected.add(PlannedExercise(exercise.id, sets, reps, rest))
        }

        return selected
    }

    private fun selectLowerBodyExercises(exercises: List<Exercise>, sets: Int, reps: String, rest: Int): List<PlannedExercise> {
        val selected = mutableListOf<PlannedExercise>()
        
        val squat = findExercise(exercises, listOf("quadriceps", "glutes"), primary = true)
        val hinge = findExercise(exercises, listOf("hamstrings", "glutes"), primary = true)
        val quad = findExercise(exercises, listOf("quadriceps"), primary = true, exclude = listOfNotNull(squat))
        val hamstring = findExercise(exercises, listOf("hamstrings"), primary = true, exclude = listOfNotNull(hinge))
        val core = findExercise(exercises, listOf("abs"), primary = true)

        listOfNotNull(squat, hinge, quad, hamstring, core).forEach { exercise ->
            selected.add(PlannedExercise(exercise.id, sets, reps, rest))
        }

        return selected
    }

    private fun selectChestTricepsExercises(exercises: List<Exercise>, sets: Int, reps: String, rest: Int): List<PlannedExercise> {
        val selected = mutableListOf<PlannedExercise>()
        
        val chest1 = findExercise(exercises, listOf("chest"), primary = true)
        val chest2 = findExercise(exercises, listOf("chest"), primary = true, exclude = listOfNotNull(chest1))
        val tricep1 = findExercise(exercises, listOf("triceps"), primary = true)
        val tricep2 = findExercise(exercises, listOf("triceps"), primary = true, exclude = listOfNotNull(tricep1))

        listOfNotNull(chest1, chest2, tricep1, tricep2).forEach { exercise ->
            selected.add(PlannedExercise(exercise.id, sets, reps, rest))
        }

        return selected
    }

    private fun selectBackBicepsExercises(exercises: List<Exercise>, sets: Int, reps: String, rest: Int): List<PlannedExercise> {
        val selected = mutableListOf<PlannedExercise>()
        
        val back1 = findExercise(exercises, listOf("lats", "back"), primary = true)
        val back2 = findExercise(exercises, listOf("lats", "back"), primary = true, exclude = listOfNotNull(back1))
        val bicep1 = findExercise(exercises, listOf("biceps"), primary = true)
        val bicep2 = findExercise(exercises, listOf("biceps"), primary = true, exclude = listOfNotNull(bicep1))

        listOfNotNull(back1, back2, bicep1, bicep2).forEach { exercise ->
            selected.add(PlannedExercise(exercise.id, sets, reps, rest))
        }

        return selected
    }

    private fun selectShoulderArmExercises(exercises: List<Exercise>, sets: Int, reps: String, rest: Int): List<PlannedExercise> {
        val selected = mutableListOf<PlannedExercise>()
        
        val shoulder1 = findExercise(exercises, listOf("shoulders"), primary = true)
        val shoulder2 = findExercise(exercises, listOf("shoulders"), primary = true, exclude = listOfNotNull(shoulder1))
        val bicep = findExercise(exercises, listOf("biceps"), primary = true)
        val tricep = findExercise(exercises, listOf("triceps"), primary = true)

        listOfNotNull(shoulder1, shoulder2, bicep, tricep).forEach { exercise ->
            selected.add(PlannedExercise(exercise.id, sets, reps, rest))
        }

        return selected
    }

    private fun findExercise(
        exercises: List<Exercise>,
        targetMuscles: List<String>,
        primary: Boolean = true,
        exclude: List<Exercise> = emptyList()
    ): Exercise? {
        val excludeIds = exclude.map { it.id }.toSet()
        return exercises
            .filter { it.id !in excludeIds }
            .filter { exercise ->
                if (primary) {
                    exercise.primaryMuscles.any { it in targetMuscles }
                } else {
                    exercise.secondaryMuscles.any { it in targetMuscles }
                }
            }
            .randomOrNull()
    }

    private fun getSetsRepsRest(goal: TrainingGoal, experience: ExperienceLevel): Triple<Int, String, Int> {
        return when (goal) {
            TrainingGoal.STRENGTH -> when (experience) {
                ExperienceLevel.BEGINNER -> Triple(3, "5", 180)
                ExperienceLevel.INTERMEDIATE -> Triple(4, "3-5", 180)
                ExperienceLevel.ADVANCED -> Triple(5, "1-5", 240)
            }
            TrainingGoal.HYPERTROPHY -> when (experience) {
                ExperienceLevel.BEGINNER -> Triple(3, "8-12", 90)
                ExperienceLevel.INTERMEDIATE -> Triple(4, "8-12", 90)
                ExperienceLevel.ADVANCED -> Triple(4, "6-12", 90)
            }
            TrainingGoal.FAT_LOSS -> when (experience) {
                ExperienceLevel.BEGINNER -> Triple(3, "12-15", 60)
                ExperienceLevel.INTERMEDIATE -> Triple(3, "12-15", 60)
                ExperienceLevel.ADVANCED -> Triple(4, "12-20", 45)
            }
            TrainingGoal.GENERAL_FITNESS, TrainingGoal.CONSISTENCY -> when (experience) {
                ExperienceLevel.BEGINNER -> Triple(3, "10-12", 90)
                ExperienceLevel.INTERMEDIATE -> Triple(3, "10-12", 90)
                ExperienceLevel.ADVANCED -> Triple(4, "8-12", 90)
            }
        }
    }
}
