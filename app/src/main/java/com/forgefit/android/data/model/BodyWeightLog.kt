package com.forgefit.android.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "body_weight_log")
data class BodyWeightLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val weightKg: Float,
    val date: Long,
    val notes: String? = null
)
