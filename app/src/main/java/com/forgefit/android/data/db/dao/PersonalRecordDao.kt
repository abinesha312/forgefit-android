package com.forgefit.android.data.db.dao

import androidx.room.*
import com.forgefit.android.data.model.PersonalRecord
import com.forgefit.android.data.model.RecordType
import kotlinx.coroutines.flow.Flow

@Dao
interface PersonalRecordDao {
    @Query("SELECT * FROM personal_records ORDER BY achievedDate DESC")
    fun getAllRecords(): Flow<List<PersonalRecord>>

    @Query("SELECT * FROM personal_records WHERE exerciseId = :exerciseId ORDER BY achievedDate DESC")
    fun getRecordsByExercise(exerciseId: String): Flow<List<PersonalRecord>>

    @Query("""
        SELECT * FROM personal_records 
        WHERE exerciseId = :exerciseId AND recordType = :recordType
        ORDER BY value DESC 
        LIMIT 1
    """)
    suspend fun getBestRecord(exerciseId: String, recordType: RecordType): PersonalRecord?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(record: PersonalRecord)

    @Delete
    suspend fun delete(record: PersonalRecord)

    @Query("SELECT COUNT(*) FROM personal_records")
    suspend fun getRecordCount(): Int
}
