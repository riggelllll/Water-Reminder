package com.koniukhov.waterreminder.workers

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.koniukhov.waterreminder.utilities.Constants.BED_TIME_EXTRA
import com.koniukhov.waterreminder.utilities.Constants.DEFAULT_BED_HOUR
import com.koniukhov.waterreminder.utilities.Constants.DEFAULT_MINUTE
import com.koniukhov.waterreminder.utilities.Constants.DEFAULT_WAKE_UP_HOUR
import com.koniukhov.waterreminder.utilities.Constants.WAKE_UP_EXTRA
import com.koniukhov.waterreminder.utilities.WorkerUtils.makeNotification
import com.koniukhov.waterreminder.utilities.isBetweenLocalTime
import java.time.LocalTime


class NotificationWorker(ctx: Context, params: WorkerParameters) : Worker(ctx, params) {

    private val appContext = applicationContext
    override fun doWork(): Result {

        if (!isStopped){
            return try {
                val now = LocalTime.now()
                val wakeUpTime = LocalTime.ofSecondOfDay(inputData.getInt(WAKE_UP_EXTRA,
                    LocalTime.of(DEFAULT_WAKE_UP_HOUR, DEFAULT_MINUTE).toSecondOfDay()).toLong())

                val bedTime = LocalTime.ofSecondOfDay(inputData.getInt(BED_TIME_EXTRA,
                    LocalTime.of(DEFAULT_BED_HOUR, DEFAULT_MINUTE).toSecondOfDay()).toLong())

                if (isBetweenLocalTime(now, wakeUpTime, bedTime)){
                    makeNotification(appContext)
                }

                Result.success()
            }catch (e: Exception){
                Result.failure()
            }
        }else{
           return Result.failure()
        }
    }

    companion object{
        const val NAME = "NotificationWorker"
    }
}