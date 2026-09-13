package com.forgefit.android.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.forgefit.android.data.db.Converters

@Entity(tableName = "exercises")
@TypeConverters(Converters::class)
data class Exercise(
    @PrimaryKey val id: String,
    val name: String,
    val primaryMuscles: List<String>,
    val secondaryMuscles: List<String>,
    val equipment: List<String>,
    val category: String,
    val instructions: String,
    val mediaUrl: String? = null,
    val isCustom: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

enum class MuscleGroup {
    CHEST, BACK, SHOULDERS, BICEPS, TRICEPS, FOREARMS,
    QUADRICEPS, HAMSTRINGS, GLUTES, CALVES, ABS, OBLIQUES,
    LOWER_BACK, TRAPS, LATS, NECK
}

enum class ExerciseCategory {
    BARBELL, DUMBBELL, MACHINE, BODYWEIGHT, CABLE, 
    KETTLEBELL, BAND, CARDIO, STRETCHING, OTHER
}
