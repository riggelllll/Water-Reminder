package com.koniukhov.waterreminder.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalTime


const val USER_PREFERENCES_NAME = "user_preferences"

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = USER_PREFERENCES_NAME
)

class UserDataStore(private val preferencesDatastore: DataStore<Preferences>){

    companion object{
        val WEIGHT = intPreferencesKey("weight")
        val SEX = intPreferencesKey("sex")
        val WAKE_UP_TIME = intPreferencesKey("wake_up_time")
        val BED_TIME = intPreferencesKey("bed_time")
        val IS_REMIND = booleanPreferencesKey("is_remind")
        val REMINDER_INTERVAL = intPreferencesKey("reminder_interval")
        val WATER_LIMIT_PER_DAY = intPreferencesKey("water_limit_per_day")
        val IS_FIRST_OPENING = booleanPreferencesKey("is_first_opening")
    }

    val userPreferencesFlow: Flow<UserPreferences> = preferencesDatastore.data.map { preferences ->

        val weight = preferences[WEIGHT] ?: 0
        val sex = preferences[SEX] ?: 0
        val wakeUpTime = preferences[WAKE_UP_TIME] ?: 0
        val bedTime = preferences[BED_TIME] ?: 0
        val isRemind = preferences[IS_REMIND] ?: false
        val reminderInterval = preferences[REMINDER_INTERVAL] ?: 2
        val waterLimitPerDay = preferences[WATER_LIMIT_PER_DAY] ?: 0
        val isFirstOpening = preferences[IS_FIRST_OPENING] ?: true

        UserPreferences(weight, Sex.from(sex)!!, LocalTime.ofSecondOfDay(wakeUpTime.toLong()),
            LocalTime.ofSecondOfDay(bedTime.toLong()), isRemind, reminderInterval, waterLimitPerDay, isFirstOpening)
    }

    suspend fun updateWeight(weight: Int) {
        preferencesDatastore.edit { preferences ->
            preferences[WEIGHT] = weight
        }
    }

    suspend fun updateSex(sex: Int){
        preferencesDatastore.edit {preferences ->
            preferences[SEX] = Sex.from(sex)?.sex ?: 0
        }
    }

    suspend fun updateWakeUpTime(hour: Int, minute: Int){
        preferencesDatastore.edit { preferences ->
            preferences[WAKE_UP_TIME] = LocalTime.of(hour, minute).toSecondOfDay()
        }
    }

    suspend fun updateBedTime(hour: Int, minute: Int){
        preferencesDatastore.edit { preferences ->
            preferences[BED_TIME] = LocalTime.of(hour, minute).toSecondOfDay()
        }
    }

    suspend fun updateIsRemind(value: Boolean){
        preferencesDatastore.edit { preferences ->
            preferences[IS_REMIND] = value
        }
    }

    suspend fun updateReminderInterval(value: Int){
        preferencesDatastore.edit { preferences ->
            preferences[REMINDER_INTERVAL] = value
        }
    }

    suspend fun updateWaterLimitPerDay(value: Int){
        preferencesDatastore.edit { preferences ->
            preferences[WATER_LIMIT_PER_DAY] = value
        }
    }

    suspend fun updateIsFirstOpening(value: Boolean){
        preferencesDatastore.edit { preferences ->
            preferences[IS_FIRST_OPENING] = value
        }
    }
}

enum class Sex(val sex: Int){
    MALE(0),
    FEMALE(1);

    companion object {
        private val map = Sex.values().associateBy { it.sex }
        fun from(value: Int) = map[value]
    }
}