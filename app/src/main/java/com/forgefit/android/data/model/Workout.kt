package com.forgefit.android.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.forgefit.android.data.db.Converters

@Entity(tableName = "workouts")
@TypeConverters(Converters::class)
data class Workout(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val date: Long,
    val startTime: Long? = null,
    val endTime: Long? = null,
    val notes: String? = null,
    val completed: Boolean = false,
    val routineId: Long? = null,
    val totalVolume: Float = 0f,
    val exerciseIds: List<String> = emptyList()
)

@Entity(tableName = "workout_sets")
data class WorkoutSet(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val workoutId: Long,
    val exerciseId: String,
    val setNumber: Int,
    val weightKg: Float,
    val reps: Int,
    val rpe: Float? = null,
    val notes: String? = null,
    val isWarmup: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "personal_records")
data class PersonalRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val exerciseId: String,
    val exerciseName: String,
    val recordType: RecordType,
    val value: Float,
    val reps: Int? = null,
    val achievedDate: Long,
    val workoutId: Long
)

enum class RecordType {
    ONE_REP_MAX,
    VOLUME,
    MAX_REPS,
    BEST_SET
}
