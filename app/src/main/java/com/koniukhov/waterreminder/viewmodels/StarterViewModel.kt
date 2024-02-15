package com.koniukhov.waterreminder.viewmodels

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.work.WorkManager
import com.koniukhov.waterreminder.data.user.Gender
import com.koniukhov.waterreminder.data.user.UserDataStore
import com.koniukhov.waterreminder.utilities.Constants.DEFAULT_BED_HOUR
import com.koniukhov.waterreminder.utilities.Constants.DEFAULT_MINUTE
import com.koniukhov.waterreminder.utilities.Constants.DEFAULT_REMINDER_INTERVAL
import com.koniukhov.waterreminder.utilities.Constants.DEFAULT_SEX_ALPHA
import com.koniukhov.waterreminder.utilities.Constants.DEFAULT_WAKE_UP_HOUR
import com.koniukhov.waterreminder.utilities.Constants.DEFAULT_WEIGHT
import com.koniukhov.waterreminder.utilities.WaterHelper
import com.koniukhov.waterreminder.utilities.WorkerUtils
import kotlinx.coroutines.launch
import java.time.LocalTime
import java.util.LinkedList
import java.util.Queue

class StarterViewModel(private val dataStore: UserDataStore,
                       application: Application) : ViewModel() {

    private val workManager = WorkManager.getInstance(application)
    private var gender: Gender = Gender.MALE
    private var weight: Int = DEFAULT_WEIGHT
    private var wakeUpTime: LocalTime = LocalTime.of(DEFAULT_WAKE_UP_HOUR, DEFAULT_MINUTE)
    private var bedTime: LocalTime = LocalTime.of(DEFAULT_BED_HOUR, DEFAULT_MINUTE)

    var maleLayoutAlpha = MutableLiveData(DEFAULT_SEX_ALPHA)
    private set
    var femaleLayoutAlpha = MutableLiveData(DEFAULT_SEX_ALPHA)
    private set
    var isIntroLayoutVisible = MutableLiveData(true)
    private set
    var isGenderLayoutVisible = MutableLiveData(false)
    private set
    var isWeightLayoutVisible = MutableLiveData(false)
    private set
    var isTimeLayoutVisible = MutableLiveData(false)
    private set

    private val actionsQueue: Queue<() -> Unit> = LinkedList()

    private val actionHideIntroShowGender: (() -> Unit) = {
        isIntroLayoutVisible.value = false
        isGenderLayoutVisible.value = true
    }

    private val actionHideGenderShowWeight: (() -> Unit) = {
        isGenderLayoutVisible.value = false
        isWeightLayoutVisible.value = true
    }

    private val actionHideWeightShowTime: (() -> Unit) = {
        isWeightLayoutVisible.value = false
        isTimeLayoutVisible.value = true
    }

    var hasToNavigate: MutableLiveData<Boolean> = MutableLiveData(false)
    private set

    private val actionGoToHomeFragment: (() -> Unit) = {
        viewModelScope.launch{
            val limitPerDay = WaterHelper.calculateWaterAmount(gender, weight)
            dataStore.saveUser(weight, gender, wakeUpTime, bedTime, limitPerDay)
            WorkerUtils.registerNotification(workManager, DEFAULT_REMINDER_INTERVAL.toLong(), wakeUpTime, bedTime)
            hasToNavigate.value = true
        }
    }

    init {
        initActionsQueue()
    }

    private fun initActionsQueue(){
        actionsQueue.add(actionHideIntroShowGender)
        actionsQueue.add(actionHideGenderShowWeight)
        actionsQueue.add(actionHideWeightShowTime)
        actionsQueue.add(actionGoToHomeFragment)
    }

    fun processButton(){
        actionsQueue.poll()?.invoke()
    }

    fun setGender(gender: Gender){
        this.gender = gender
        resetGenderLayoutsAlpha()

        if (gender == Gender.MALE){
            maleLayoutAlpha.value = 1f
        }else{
            femaleLayoutAlpha.value = 1f
        }
    }

    private fun resetGenderLayoutsAlpha(){
        maleLayoutAlpha.value = 0.5f
        femaleLayoutAlpha.value = 0.5f
    }

    fun setWeight(value: Int){
        weight = value
    }

    fun setWakeUpHour(hour: Int){
        wakeUpTime = wakeUpTime.withHour(hour)
    }

    fun setWakeUpMinute(minute: Int){
        wakeUpTime = wakeUpTime.withMinute(minute)
    }

    fun setBedTimeHour(hour: Int){
        bedTime = bedTime.withHour(hour)
    }

    fun setBedTimeMinute(minute: Int){
        bedTime = bedTime.withMinute(minute)
    }

    class StarterViewModelFactory(private val dataStore: UserDataStore,
                                  private val application: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(StarterViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return StarterViewModel(dataStore, application) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}