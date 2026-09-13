package com.forgefit.android.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey val id: Int = 1,
    val displayName: String,
    val sex: String? = null,
    val age: Int,
    val heightCm: Float,
    val weightKg: Float,
    val goal: TrainingGoal,
    val experience: ExperienceLevel,
    val equipment: EquipmentType,
    val trainingDaysPerWeek: Int,
    val sessionLengthMinutes: Int,
    val onboardingCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

enum class TrainingGoal {
    STRENGTH,
    HYPERTROPHY,
    FAT_LOSS,
    GENERAL_FITNESS,
    CONSISTENCY
}

enum class ExperienceLevel {
    BEGINNER,
    INTERMEDIATE,
    ADVANCED
}

enum class EquipmentType {
    FULL_GYM,
    DUMBBELLS,
    BODYWEIGHT,
    HOME_LIMITED
}
