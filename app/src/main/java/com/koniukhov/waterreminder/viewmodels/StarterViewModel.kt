package com.koniukhov.waterreminder.viewmodels

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.work.WorkManager
import com.koniukhov.waterreminder.data.user.Sex
import com.koniukhov.waterreminder.data.user.UserDataStore
import com.koniukhov.waterreminder.utilities.WaterHelper
import com.koniukhov.waterreminder.utilities.WorkerUtils
import kotlinx.coroutines.launch
import java.time.LocalTime
import java.util.LinkedList
import java.util.Queue

class StarterViewModel(private val dataStore: UserDataStore,
                       application: Application) : ViewModel() {

    companion object{
        const val DEFAULT_REMINDER_INTERVAL = 2
        const val DEFAULT_WEIGHT = 60
        const val DEFAULT_WAKE_UP_HOUR = 6
        const val DEFAULT_BED_HOUR = 22
        const val DEFAULT_MINUTE = 0

        const val HOUR_MIN_VALUE = 0
        const val HOUR_MAX_VALUE = 23
        const val MINUTE_MAX_VALUE = 59

        const val WEIGHT_MIN = 1
        const val WEIGHT_MAX = 200

        const val WAKE_UP_EXTRA = "wakeUpTime"
        const val BED_TIME_EXTRA = "bedTime"

        const val DEFAULT_SEX_ALPHA = 0.5f
    }

    private val workManager = WorkManager.getInstance(application)
    private var _sex: Sex = Sex.MALE
    private var _weight: Int = DEFAULT_WEIGHT
    private var _wakeUpTime: LocalTime = LocalTime.of(DEFAULT_WAKE_UP_HOUR, DEFAULT_MINUTE)
    private var _bedTime: LocalTime = LocalTime.of(DEFAULT_BED_HOUR, DEFAULT_MINUTE)

    var maleLayoutAlpha = MutableLiveData(DEFAULT_SEX_ALPHA)
    private set
    var femaleLayoutAlpha = MutableLiveData(DEFAULT_SEX_ALPHA)
    private set
    var isIntroLayoutVisible = MutableLiveData(true)
    private set
    var isSexLayoutVisible = MutableLiveData(false)
    private set
    var isWeightLayoutVisible = MutableLiveData(false)
    private set
    var isTimeLayoutVisible = MutableLiveData(false)
    private set

    private val actionsQueue: Queue<() ->Unit> = LinkedList()

    private val actionHideIntroShowSex: (() -> Unit) = {
        isIntroLayoutVisible.value = false
        isSexLayoutVisible.value = true
    }

    private val actionHideSexSHowWeight: (() -> Unit) = {
        isSexLayoutVisible.value = false
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
            val limitPerDay = WaterHelper.calculateWaterAmount(_sex, _weight)
            dataStore.saveUser(_weight, _sex, _wakeUpTime, _bedTime, limitPerDay)
            WorkerUtils.registerNotification(workManager, DEFAULT_REMINDER_INTERVAL.toLong(), _wakeUpTime, _bedTime)
            hasToNavigate.value = true
        }
    }

    init {
        initActionsQueue()
    }

    private fun initActionsQueue(){
        actionsQueue.add(actionHideIntroShowSex)
        actionsQueue.add(actionHideSexSHowWeight)
        actionsQueue.add(actionHideWeightShowTime)
        actionsQueue.add(actionGoToHomeFragment)
    }

    fun processButton(){
        actionsQueue.poll()?.invoke()
    }

    fun setSex(sex: Sex){
        _sex = sex
        resetSexLayoutsAlpha()

        if (sex == Sex.MALE){
            maleLayoutAlpha.value = 1f
        }else{
            femaleLayoutAlpha.value = 1f
        }
    }

    private fun resetSexLayoutsAlpha(){
        maleLayoutAlpha.value = 0.5f
        femaleLayoutAlpha.value = 0.5f
    }

    fun setWeight(value: Int){
        _weight = value
    }

    fun setWakeUpHour(hour: Int){
        _wakeUpTime = _wakeUpTime.withHour(hour)
    }

    fun setWakeUpMinute(minute: Int){
        _wakeUpTime = _wakeUpTime.withMinute(minute)
    }

    fun setBedTimeHour(hour: Int){
        _bedTime = _bedTime.withHour(hour)
    }

    fun setBedTimeMinute(minute: Int){
        _bedTime = _bedTime.withMinute(minute)
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