package com.koniukhov.waterreminder.workers

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.koniukhov.waterreminder.utilities.WorkerUtils.Companion.makeNotification
import com.koniukhov.waterreminder.utilities.isBetweenLocalTime
import com.koniukhov.waterreminder.viewmodels.StarterViewModel.Companion.BED_TIME_EXTRA
import com.koniukhov.waterreminder.viewmodels.StarterViewModel.Companion.DEFAULT_BED_HOUR
import com.koniukhov.waterreminder.viewmodels.StarterViewModel.Companion.DEFAULT_MINUTE
import com.koniukhov.waterreminder.viewmodels.StarterViewModel.Companion.DEFAULT_WAKE_UP_HOUR
import com.koniukhov.waterreminder.viewmodels.StarterViewModel.Companion.WAKE_UP_EXTRA
import java.time.LocalTime

const val TAG = "NotificationWorker"
class NotificationWorker(ctx: Context, params: WorkerParameters) : Worker(ctx, params) {
    private val appContext = applicationContext
    override fun doWork(): Result {

        val now = LocalTime.now()
        val wakeUpTime = LocalTime.ofSecondOfDay(inputData.getInt(WAKE_UP_EXTRA,
            LocalTime.of(DEFAULT_WAKE_UP_HOUR, DEFAULT_MINUTE).toSecondOfDay()).toLong())

        val bedTime = LocalTime.ofSecondOfDay(inputData.getInt(BED_TIME_EXTRA,
            LocalTime.of(DEFAULT_BED_HOUR, DEFAULT_MINUTE).toSecondOfDay()).toLong())

        if (isBetweenLocalTime(now, wakeUpTime, bedTime)){
            makeNotification(appContext)
        }

        return Result.success()
    }
}