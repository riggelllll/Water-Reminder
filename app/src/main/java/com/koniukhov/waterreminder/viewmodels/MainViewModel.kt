package com.koniukhov.waterreminder.viewmodels

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.work.WorkManager
import com.koniukhov.waterreminder.data.user.Gender
import com.koniukhov.waterreminder.data.user.UserDataStore
import com.koniukhov.waterreminder.data.user.UserPreferences
import com.koniukhov.waterreminder.utilities.WorkerUtils
import com.koniukhov.waterreminder.workers.NotificationWorker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel(private val dataStore: UserDataStore, application: Application) : ViewModel() {
    var userFlow = dataStore.userPreferencesFlow
    private set

    private val workManager = WorkManager.getInstance(application)

    lateinit var userPreferences: UserPreferences
        private set
    init {
        viewModelScope.launch(Dispatchers.IO) {
            userFlow.collect{
                userPreferences = it
            }
        }
    }
    fun changeIsRemind(value: Boolean){
        viewModelScope.launch(Dispatchers.IO){

            if (value != userPreferences.isRemind){
                if (value){
                WorkerUtils.registerNotification(workManager, userPreferences.reminderInterval.toLong(),
                        userPreferences.wakeUpTime, userPreferences.bedTime)
                }else{
                    workManager.cancelUniqueWork(NotificationWorker.NAME)
                }
            }
            dataStore.updateIsRemind(value)
        }
    }

    fun changeReminderInterval(value: Int){
        viewModelScope.launch(Dispatchers.IO) {
            dataStore.updateReminderInterval(value)
           WorkerUtils.registerNotification(workManager, userPreferences.reminderInterval.toLong(),
                userPreferences.wakeUpTime, userPreferences.bedTime)
        }
    }

    fun changeWaterLimit(value: Int){
        viewModelScope.launch(Dispatchers.IO) {
            dataStore.updateWaterLimitPerDay(value)
        }
    }

    fun changeGender(value: Gender){
        viewModelScope.launch(Dispatchers.IO) {
            dataStore.updateGender(value.gender)
        }
    }

    fun changeWeight(value: Int){
        viewModelScope.launch(Dispatchers.IO) {
            dataStore.updateWeight(value)
        }
    }

    fun changeWakeUpTime(hour: Int, minute: Int){
        viewModelScope.launch(Dispatchers.IO) {
            dataStore.updateWakeUpTime(hour, minute)
        }
    }

    fun changeBedTime(hour: Int, minute: Int){
        viewModelScope.launch(Dispatchers.IO) {
            dataStore.updateBedTime(hour, minute)
        }
    }

    class MainViewModelFactory(private val dataStore: UserDataStore, private val application: Application
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return MainViewModel(dataStore, application) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

}