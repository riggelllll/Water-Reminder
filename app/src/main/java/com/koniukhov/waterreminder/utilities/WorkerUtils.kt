package com.koniukhov.waterreminder.utilities

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.koniukhov.waterreminder.MainActivity
import com.koniukhov.waterreminder.R
import com.koniukhov.waterreminder.viewmodels.StarterViewModel
import com.koniukhov.waterreminder.workers.NotificationWorker
import com.koniukhov.waterreminder.workers.NotificationWorker.Companion.NAME
import java.time.LocalTime
import java.util.concurrent.TimeUnit

object WorkerUtils{
    private const val VERBOSE_NOTIFICATION_CHANNEL_NAME =
        "Verbose WorkManager Notifications"
    private const val VERBOSE_NOTIFICATION_CHANNEL_DESCRIPTION =
        "Shows notifications whenever work starts"
    private const val CHANNEL_ID = "VERBOSE_NOTIFICATION"
    private const val NOTIFICATION_ID = 1

    fun makeNotification(context: Context) {
        val name = VERBOSE_NOTIFICATION_CHANNEL_NAME
        val description = VERBOSE_NOTIFICATION_CHANNEL_DESCRIPTION
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(CHANNEL_ID, name, importance)
        channel.description = description

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?

        notificationManager?.createNotificationChannel(channel)

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.notification_icon)
            .setContentTitle(context.getString(R.string.notification_title))
            .setContentText(context.getString(R.string.notification_text))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVibrate(LongArray(0))
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, builder.build())
            }
        }else{
            NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, builder.build())
        }
    }

    @SuppressLint("RestrictedApi")
    fun registerNotification(workManager: WorkManager, reminderInterval: Long, wakeUpTime: LocalTime, bedTime: LocalTime){
        val periodicWork = PeriodicWorkRequest.Builder(NotificationWorker::class.java, reminderInterval, TimeUnit.HOURS)
        val constraints = Constraints.Builder()
            .setRequiresBatteryNotLow(true)
            .build()

        val data = Data.Builder()
        with(data) {
            putInt(StarterViewModel.WAKE_UP_EXTRA, wakeUpTime.toSecondOfDay())
            putInt(StarterViewModel.BED_TIME_EXTRA, bedTime.toSecondOfDay())
        }

        periodicWork.setInputData(data.build())
        periodicWork.setConstraints(constraints)
        periodicWork.setInitialDelay(reminderInterval, TimeUnit.HOURS)
        workManager.enqueueUniquePeriodicWork(NAME, ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE, periodicWork.build())
    }
}




