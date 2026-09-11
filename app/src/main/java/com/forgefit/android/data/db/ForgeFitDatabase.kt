package com.forgefit.android.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.forgefit.android.data.db.dao.*
import com.forgefit.android.data.model.*

@Database(
    entities = [
        UserProfile::class,
        Exercise::class,
        Workout::class,
        WorkoutSet::class,
        PersonalRecord::class,
        BodyWeightLog::class,
        Routine::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class ForgeFitDatabase : RoomDatabase() {
    abstract fun userProfileDao(): UserProfileDao
    abstract fun exerciseDao(): ExerciseDao
    abstract fun workoutDao(): WorkoutDao
    abstract fun personalRecordDao(): PersonalRecordDao
    abstract fun bodyWeightLogDao(): BodyWeightLogDao
    abstract fun routineDao(): RoutineDao

    companion object {
        @Volatile
        private var INSTANCE: ForgeFitDatabase? = null

        fun getInstance(context: Context): ForgeFitDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ForgeFitDatabase::class.java,
                    "forgefit_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
