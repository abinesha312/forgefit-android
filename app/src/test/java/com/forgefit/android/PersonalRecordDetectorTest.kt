package com.forgefit.android

import com.forgefit.android.data.model.PersonalRecord
import com.forgefit.android.data.model.RecordType
import com.forgefit.android.data.model.WorkoutSet
import com.forgefit.android.domain.PersonalRecordDetector
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class PersonalRecordDetectorTest {

    private lateinit var detector: PersonalRecordDetector

    @Before
    fun setup() {
        detector = PersonalRecordDetector()
    }

    @Test
    fun `test new PR detection when no existing records`() {
        val sets = listOf(
            WorkoutSet(
                workoutId = 1,
                exerciseId = "bench",
                setNumber = 1,
                weightKg = 100f,
                reps = 5,
                isWarmup = false
            )
        )

        val detected = detector.detectRecords("bench", "Bench Press", sets, emptyList())

        assertTrue(detected.any { it.type == RecordType.ONE_REP_MAX && it.isNewRecord })
        assertTrue(detected.any { it.type == RecordType.VOLUME && it.isNewRecord })
    }

    @Test
    fun `test PR detection with existing better record`() {
        val sets = listOf(
            WorkoutSet(
                workoutId = 1,
                exerciseId = "bench",
                setNumber = 1,
                weightKg = 100f,
                reps = 5,
                isWarmup = false
            )
        )

        val existingRecords = listOf(
            PersonalRecord(
                exerciseId = "bench",
                exerciseName = "Bench Press",
                recordType = RecordType.ONE_REP_MAX,
                value = 150f,
                achievedDate = System.currentTimeMillis(),
                workoutId = 0
            )
        )

        val detected = detector.detectRecords("bench", "Bench Press", sets, existingRecords)

        val oneRepMax = detected.find { it.type == RecordType.ONE_REP_MAX }
        assertNotNull(oneRepMax)
        assertFalse(oneRepMax!!.isNewRecord)
    }

    @Test
    fun `test warmup sets are excluded from PR calculation`() {
        val sets = listOf(
            WorkoutSet(
                workoutId = 1,
                exerciseId = "bench",
                setNumber = 1,
                weightKg = 60f,
                reps = 10,
                isWarmup = true
            ),
            WorkoutSet(
                workoutId = 1,
                exerciseId = "bench",
                setNumber = 2,
                weightKg = 100f,
                reps = 5,
                isWarmup = false
            )
        )

        val detected = detector.detectRecords("bench", "Bench Press", sets, emptyList())

        val bestSet = detected.find { it.type == RecordType.BEST_SET }
        assertNotNull(bestSet)
        assertEquals(100f, bestSet!!.value, 0.01f)
    }

    @Test
    fun `test volume calculation`() {
        val sets = listOf(
            WorkoutSet(
                workoutId = 1,
                exerciseId = "bench",
                setNumber = 1,
                weightKg = 100f,
                reps = 5,
                isWarmup = false
            ),
            WorkoutSet(
                workoutId = 1,
                exerciseId = "bench",
                setNumber = 2,
                weightKg = 100f,
                reps = 5,
                isWarmup = false
            ),
            WorkoutSet(
                workoutId = 1,
                exerciseId = "bench",
                setNumber = 3,
                weightKg = 100f,
                reps = 5,
                isWarmup = false
            )
        )

        val detected = detector.detectRecords("bench", "Bench Press", sets, emptyList())

        val volume = detected.find { it.type == RecordType.VOLUME }
        assertNotNull(volume)
        assertEquals(1500f, volume!!.value, 0.01f)
    }
}
