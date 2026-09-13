package com.forgefit.android.data.healthconnect

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContract
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.PermissionController
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.ExerciseSessionRecord
import androidx.health.connect.client.records.HeightRecord
import androidx.health.connect.client.records.WeightRecord
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.time.Instant
import java.time.ZoneOffset

class HealthConnectManager(private val context: Context) {

    private val healthConnectClient by lazy {
        HealthConnectClient.getOrCreate(context)
    }

    companion object {
        const val HEALTH_CONNECT_PACKAGE = "com.google.android.apps.healthdata"
        
        val PERMISSIONS = setOf(
            HealthPermission.getReadPermission(ExerciseSessionRecord::class),
            HealthPermission.getWritePermission(ExerciseSessionRecord::class),
            HealthPermission.getReadPermission(WeightRecord::class),
            HealthPermission.getWritePermission(WeightRecord::class),
            HealthPermission.getReadPermission(HeightRecord::class)
        )
    }

    suspend fun hasAllPermissions(): Boolean {
        return try {
            val granted = healthConnectClient.permissionController.getGrantedPermissions()
            PERMISSIONS.all { it in granted }
        } catch (e: Exception) {
            false
        }
    }

    suspend fun requestPermissions() = PERMISSIONS

    fun isHealthConnectAvailable(): Boolean {
        return HealthConnectClient.getSdkStatus(context) == HealthConnectClient.SDK_AVAILABLE
    }

    suspend fun writeExerciseSession(
        startTime: Instant,
        endTime: Instant,
        title: String,
        notes: String? = null
    ) {
        try {
            val session = ExerciseSessionRecord(
                startTime = startTime,
                startZoneOffset = ZoneOffset.UTC,
                endTime = endTime,
                endZoneOffset = ZoneOffset.UTC,
                exerciseType = ExerciseSessionRecord.EXERCISE_TYPE_WEIGHTLIFTING,
                title = title,
                notes = notes
            )

            healthConnectClient.insertRecords(listOf(session))
        } catch (e: Exception) {
        }
    }

    suspend fun readLatestWeight(): WeightRecord? {
        return try {
            val response = healthConnectClient.readRecords(
                ReadRecordsRequest(
                    recordType = WeightRecord::class,
                    timeRangeFilter = TimeRangeFilter.none(),
                    ascendingOrder = false,
                    limit = 1
                )
            )
            response.records.firstOrNull()
        } catch (e: Exception) {
            null
        }
    }

    suspend fun readLatestHeight(): HeightRecord? {
        return try {
            val response = healthConnectClient.readRecords(
                ReadRecordsRequest(
                    recordType = HeightRecord::class,
                    timeRangeFilter = TimeRangeFilter.none(),
                    ascendingOrder = false,
                    limit = 1
                )
            )
            response.records.firstOrNull()
        } catch (e: Exception) {
            null
        }
    }

    fun openHealthConnectSettings() {
        val intent = Intent().apply {
            action = "androidx.health.ACTION_HEALTH_CONNECT_SETTINGS"
        }
        context.startActivity(intent)
    }
}

class HealthConnectPermissionContract : ActivityResultContract<Set<String>, Set<String>>() {
    override fun createIntent(context: Context, input: Set<String>): Intent {
        return PermissionController.createRequestPermissionResultContract()
            .createIntent(context, input)
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Set<String> {
        return PermissionController.createRequestPermissionResultContract()
            .parseResult(resultCode, intent)
    }
}
