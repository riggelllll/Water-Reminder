package com.koniukhov.waterreminder.viewmodels

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.work.WorkManager
import com.koniukhov.waterreminder.R
import com.koniukhov.waterreminder.data.dailywater.DailyWater
import com.koniukhov.waterreminder.data.drinkware.DrinkWare
import com.koniukhov.waterreminder.data.user.Gender
import com.koniukhov.waterreminder.data.user.UserDataStore
import com.koniukhov.waterreminder.data.user.UserPreferences
import com.koniukhov.waterreminder.database.AppDataBase
import com.koniukhov.waterreminder.utilities.WorkerUtils
import com.koniukhov.waterreminder.workers.NotificationWorker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime

class MainViewModel(private val dataStore: UserDataStore, application: Application) : ViewModel() {
    var userFlow = dataStore.userPreferencesFlow
    private set

    private val workManager = WorkManager.getInstance(application)

    lateinit var userPreferences: UserPreferences
        private set

    private val dataBase: AppDataBase = AppDataBase.getDataBase(application.applicationContext)

    private val allDrinkWare: Flow<List<DrinkWare>> by lazy {
        dataBase.drinkWareDao().getDrinkWares()
    }

    private val allDailyWater = dataBase.dailyWaterDao().getAllByDate(LocalDate.now().toString())

    var waterAmount = MutableLiveData<Int>()
        private set

    init {
        viewModelScope.launch(Dispatchers.IO) {
            userFlow.collect{
                userPreferences = it
            }
        }
        viewModelScope.launch(Dispatchers.IO) {
            allDailyWater.collect{
                val amount = it.sumOf { dailyWater -> dailyWater.volume }
                waterAmount.postValue(amount)
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
        val record = DailyWater(null, volume, time.toString(), date.toString(), iconName)
        viewModelScope.launch(Dispatchers.IO) {
            dataBase.dailyWaterDao().addDailyWater(record)
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

    companion object{
        val drinkWareIcons = mapOf(
            "ic_drink_ware_custom" to R.drawable.ic_drink_ware_custom,
            "ic_drink_ware_50_ml" to R.drawable.ic_drink_ware_50_ml,
            "ic_drink_ware_100_ml" to R.drawable.ic_drink_ware_100_ml,
            "ic_drink_ware_125_ml" to R.drawable.ic_drink_ware_125_ml,
            "ic_drink_ware_150_ml" to R.drawable.ic_drink_ware_150_ml,
            "ic_drink_ware_175_ml" to R.drawable.ic_drink_ware_175_ml,
            "ic_drink_ware_200_ml" to R.drawable.ic_drink_ware_200_ml,
            "ic_drink_ware_250_ml" to R.drawable.ic_drink_ware_250_ml,
            "ic_drink_ware_300_ml" to R.drawable.ic_drink_ware_300_ml,
            "ic_drink_ware_400_ml" to R.drawable.ic_drink_ware_400_ml
        )
    }

}