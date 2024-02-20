package com.koniukhov.waterreminder.viewmodels

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.work.WorkManager
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import com.koniukhov.waterreminder.data.dailywater.DailyWater
import com.koniukhov.waterreminder.data.drinkware.DrinkWare
import com.koniukhov.waterreminder.data.user.Gender
import com.koniukhov.waterreminder.data.user.UserDataStore
import com.koniukhov.waterreminder.data.user.UserPreferences
import com.koniukhov.waterreminder.database.AppDataBase
import com.koniukhov.waterreminder.utilities.WorkerUtils
import com.koniukhov.waterreminder.utilities.yearMonthFormat
import com.koniukhov.waterreminder.workers.NotificationWorker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.stream.Collectors

class MainViewModel(private val dataStore: UserDataStore, application: Application) : ViewModel() {
    var userFlow = dataStore.userPreferencesFlow
    private set

    private val workManager = WorkManager.getInstance(application)
    lateinit var userPreferences: UserPreferences
        private set

    private val dataBase: AppDataBase = AppDataBase.getDataBase(application.applicationContext)

    val allDrinkWare = dataBase.drinkWareDao().getDrinkWares()

    var allDailyWater = dataBase.dailyWaterDao().getAllByDate(LocalDate.now().toString())
        private set

    var allMonthlyWater = dataBase.dailyWaterDao().getAllByMonth(yearMonthFormat(LocalDate.now()))
        private set

    private lateinit var currentDrinkWare: DrinkWare

    var waterAmount = MutableLiveData<Int>()
        private set

    init {
        observeUserPreferences()
        observeWaterAmount()
        observeCurrentDrinkWare()
    }

    private fun observeCurrentDrinkWare() {
        viewModelScope.launch(Dispatchers.IO) {
            allDrinkWare.collect {

                val drinkWare =
                    it.stream().filter { v -> v.isActive == 1 }.collect(Collectors.toList())
                if (drinkWare.isNotEmpty()) {
                    currentDrinkWare = drinkWare.first()
                }
            }
        }
    }

    private fun observeWaterAmount() {
        viewModelScope.launch(Dispatchers.IO) {
            allDailyWater.collect {
                val amount = it.sumOf { dailyWater -> dailyWater.volume }
                waterAmount.postValue(amount)
            }
        }
    }

    private fun observeUserPreferences() {
        viewModelScope.launch(Dispatchers.IO) {
            userFlow.collect {
                userPreferences = it
                Firebase.crashlytics.log(userPreferences.toString())
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

    fun addWater(time: LocalTime, date: LocalDate, volume: Int, iconName: String){
        val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
        val record = DailyWater(null, volume,time.format(timeFormatter), date.toString(), iconName)
        viewModelScope.launch(Dispatchers.IO) {
            dataBase.dailyWaterDao().addDailyWater(record)
        }
    }

    fun deleteWater(dailyWater: DailyWater){
        viewModelScope.launch(Dispatchers.IO) {
            dataBase.dailyWaterDao().delete(dailyWater)
        }
    }

    fun addWaterByCurrentDrinkWare(){
        val time = LocalTime.now()
        val date = LocalDate.now()
        val volume = currentDrinkWare.volume
        val icon = currentDrinkWare.iconName

        addWater(time, date, volume, icon)
    }

    fun updateDrinkWare(newDrinkWare: DrinkWare){
        if (currentDrinkWare != newDrinkWare){
            currentDrinkWare.isActive = 0
            newDrinkWare.isActive = 1
            viewModelScope.launch(Dispatchers.IO) {
                dataBase.drinkWareDao().updateAll(currentDrinkWare, newDrinkWare)
            }

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