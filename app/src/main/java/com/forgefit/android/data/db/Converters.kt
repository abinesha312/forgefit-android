package com.forgefit.android.data.db

import androidx.room.TypeConverter
import com.forgefit.android.data.model.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromStringList(value: List<String>): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toStringList(value: String): List<String> {
        val type = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(value, type)
    }

    @TypeConverter
    fun fromRoutineWorkoutList(value: List<RoutineWorkout>): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toRoutineWorkoutList(value: String): List<RoutineWorkout> {
        val type = object : TypeToken<List<RoutineWorkout>>() {}.type
        return gson.fromJson(value, type)
    }

    @TypeConverter
    fun fromRoutineExerciseList(value: List<RoutineExercise>): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toRoutineExerciseList(value: String): List<RoutineExercise> {
        val type = object : TypeToken<List<RoutineExercise>>() {}.type
        return gson.fromJson(value, type)
    }

    @TypeConverter
    fun fromTrainingGoal(value: TrainingGoal): String {
        return value.name
    }

    @TypeConverter
    fun toTrainingGoal(value: String): TrainingGoal {
        return TrainingGoal.valueOf(value)
    }

    @TypeConverter
    fun fromExperienceLevel(value: ExperienceLevel): String {
        return value.name
    }

    @TypeConverter
    fun toExperienceLevel(value: String): ExperienceLevel {
        return ExperienceLevel.valueOf(value)
    }

    @TypeConverter
    fun fromEquipmentType(value: EquipmentType): String {
        return value.name
    }

    @TypeConverter
    fun toEquipmentType(value: String): EquipmentType {
        return EquipmentType.valueOf(value)
    }

    @TypeConverter
    fun fromRecordType(value: RecordType): String {
        return value.name
    }

    @TypeConverter
    fun toRecordType(value: String): RecordType {
        return RecordType.valueOf(value)
    }
}
