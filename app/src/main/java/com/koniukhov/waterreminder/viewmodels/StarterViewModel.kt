package com.koniukhov.waterreminder.viewmodels

import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.koniukhov.waterreminder.R
import com.koniukhov.waterreminder.data.user.Sex
import com.koniukhov.waterreminder.data.user.UserDataStore
import com.koniukhov.waterreminder.utilities.WaterHelper
import com.koniukhov.waterreminder.workers.NotificationWorker
import com.koniukhov.waterreminder.workers.TAG
import kotlinx.coroutines.launch
import java.time.LocalTime
import java.util.LinkedList
import java.util.Queue
import java.util.concurrent.TimeUnit

class StarterViewModel(private val navController: NavController,
                       private val dataStore: UserDataStore,
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

    private var _maleLayoutAlpha = MutableLiveData(DEFAULT_SEX_ALPHA)
    val maleLayoutAlpha: LiveData<Float>
    get() = _maleLayoutAlpha
    private var _femaleLayoutAlpha = MutableLiveData(DEFAULT_SEX_ALPHA)
    val femaleLayoutAlpha: LiveData<Float>
        get() = _femaleLayoutAlpha

    private var _isIntroLayoutVisible = MutableLiveData(true)
    val isIntroLayoutVisible: LiveData<Boolean>
    get() = _isIntroLayoutVisible

    private var _isSexLayoutVisible = MutableLiveData(false)
    val isSexLayoutVisible: LiveData<Boolean>
        get() = _isSexLayoutVisible

    private var _isWeightLayoutVisible = MutableLiveData(false)
    val isWeightLayoutVisible: LiveData<Boolean>
        get() = _isWeightLayoutVisible

    private var _isTimeLayoutVisible = MutableLiveData(false)
    val isTimeLayoutVisible: LiveData<Boolean>
        get() = _isTimeLayoutVisible

    private val actionsQueue: Queue<() ->Unit> = LinkedList()


    private val actionHideIntroShowSex: (() -> Unit) = {
        _isIntroLayoutVisible.value = false
        _isSexLayoutVisible.value = true
    }

    private val actionHideSexSHowWeight: (() -> Unit) = {
        _isSexLayoutVisible.value = false
        _isWeightLayoutVisible.value = true
    }

    private val actionHideWeightShowTime: (() -> Unit) = {
        _isWeightLayoutVisible.value = false
        _isTimeLayoutVisible.value = true
    }

    private val actionGoToHomeFragment: (() -> Unit) = {
        viewModelScope.launch{
            val limitPerDay = WaterHelper.calculateWaterAmount(_sex, _weight)
            dataStore.saveUser(_weight, _sex, _wakeUpTime, _bedTime, limitPerDay)
            registerNotification(_wakeUpTime, _bedTime)
            navController.navigate(R.id.action_starterFragment_to_homeFragment)
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
            _maleLayoutAlpha.value = 1f
        }else{
            _femaleLayoutAlpha.value = 1f
        }
    }

    private fun resetSexLayoutsAlpha(){
        _maleLayoutAlpha.value = 0.5f
        _femaleLayoutAlpha.value = 0.5f
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

    @SuppressLint("RestrictedApi")
    private fun registerNotification(wakeUpTime:LocalTime, bedTime: LocalTime){
        val periodicWork = PeriodicWorkRequest.Builder(NotificationWorker::class.java, DEFAULT_REMINDER_INTERVAL.toLong(), TimeUnit.HOURS)
        val constraints = Constraints.Builder()
            .setRequiresBatteryNotLow(true)
            .build()

        val data = Data.Builder()
        with(data) {
            putInt(WAKE_UP_EXTRA, wakeUpTime.toSecondOfDay())
            putInt(BED_TIME_EXTRA, bedTime.toSecondOfDay())
        }

        periodicWork.setInputData(data.build())
        periodicWork.setConstraints(constraints)
        periodicWork.setInitialDelay(DEFAULT_REMINDER_INTERVAL.toLong(), TimeUnit.HOURS)
        workManager.enqueueUniquePeriodicWork(TAG, ExistingPeriodicWorkPolicy.KEEP, periodicWork.build())
    }

    class StarterViewModelFactory(private val navController: NavController,
                                  private val dataStore: UserDataStore,
                                  private val application: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(StarterViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return StarterViewModel(navController, dataStore, application) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

}