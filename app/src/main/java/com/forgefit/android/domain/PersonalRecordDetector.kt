package com.forgefit.android.domain

import com.forgefit.android.data.model.PersonalRecord
import com.forgefit.android.data.model.RecordType
import com.forgefit.android.data.model.WorkoutSet

class PersonalRecordDetector {
    
    data class DetectedRecord(
        val type: RecordType,
        val value: Float,
        val reps: Int? = null,
        val isNewRecord: Boolean = false
    )

    fun detectRecords(
        exerciseId: String,
        exerciseName: String,
        sets: List<WorkoutSet>,
        existingRecords: List<PersonalRecord>
    ): List<DetectedRecord> {
        val detected = mutableListOf<DetectedRecord>()

        val workingSets = sets.filter { !it.isWarmup }
        if (workingSets.isEmpty()) return detected

        val maxWeight = workingSets.maxByOrNull { it.weightKg * (1 + it.reps / 30f) }
        if (maxWeight != null) {
            val estimated1RM = estimateOneRepMax(maxWeight.weightKg, maxWeight.reps)
            val current1RM = existingRecords
                .filter { it.recordType == RecordType.ONE_REP_MAX }
                .maxOfOrNull { it.value } ?: 0f
            
            detected.add(
                DetectedRecord(
                    type = RecordType.ONE_REP_MAX,
                    value = estimated1RM,
                    isNewRecord = estimated1RM > current1RM
                )
            )
        }

        val maxRepsSet = workingSets.maxByOrNull { it.reps }
        if (maxRepsSet != null) {
            val currentMaxReps = existingRecords
                .filter { it.recordType == RecordType.MAX_REPS }
                .maxOfOrNull { it.reps ?: 0 } ?: 0
            
            detected.add(
                DetectedRecord(
                    type = RecordType.MAX_REPS,
                    value = maxRepsSet.weightKg,
                    reps = maxRepsSet.reps,
                    isNewRecord = maxRepsSet.reps > currentMaxReps
                )
            )
        }

        val totalVolume = workingSets.sumOf { (it.weightKg * it.reps).toDouble() }.toFloat()
        val currentVolume = existingRecords
            .filter { it.recordType == RecordType.VOLUME }
            .maxOfOrNull { it.value } ?: 0f
        
        detected.add(
            DetectedRecord(
                type = RecordType.VOLUME,
                value = totalVolume,
                isNewRecord = totalVolume > currentVolume
            )
        )

        val bestSet = workingSets.maxByOrNull { it.weightKg }
        if (bestSet != null) {
            val currentBestSet = existingRecords
                .filter { it.recordType == RecordType.BEST_SET }
                .maxOfOrNull { it.value } ?: 0f
            
            detected.add(
                DetectedRecord(
                    type = RecordType.BEST_SET,
                    value = bestSet.weightKg,
                    reps = bestSet.reps,
                    isNewRecord = bestSet.weightKg > currentBestSet
                )
            )
        }

        return detected
    }

    private fun estimateOneRepMax(weight: Float, reps: Int): Float {
        if (reps == 1) return weight
        return weight * (1 + reps / 30f)
    }

    fun createPersonalRecord(
        exerciseId: String,
        exerciseName: String,
        record: DetectedRecord,
        workoutId: Long
    ): PersonalRecord {
        return PersonalRecord(
            exerciseId = exerciseId,
            exerciseName = exerciseName,
            recordType = record.type,
            value = record.value,
            reps = record.reps,
            achievedDate = System.currentTimeMillis(),
            workoutId = workoutId
        )
    }
}
