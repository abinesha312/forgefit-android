package com.forgefit.android.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.forgefit.android.data.db.Converters

@Entity(tableName = "routines")
@TypeConverters(Converters::class)
data class Routine(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val description: String? = null,
    val workouts: List<RoutineWorkout>,
    val isTemplate: Boolean = false,
    val isActive: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

data class RoutineWorkout(
    val dayOfWeek: Int,
    val name: String,
    val exercises: List<RoutineExercise>
)

data class RoutineExercise(
    val exerciseId: String,
    val sets: Int,
    val reps: String,
    val restSeconds: Int,
    val notes: String? = null
)
