package com.example

import android.content.Context
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import java.util.concurrent.TimeUnit

class PrayerTimesSyncWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    private val repository = PrayerTimesRepository()

    override suspend fun doWork(): Result {
        val prefs = applicationContext.getSharedPreferences("app_settings", Context.MODE_PRIVATE)
        val lat = prefs.getFloat("last_lat", 33.5731f).toDouble()
        val lon = prefs.getFloat("last_lon", -7.5898f).toDouble()
        val city = prefs.getString("last_city", "الدار البيضاء") ?: "الدار البيضاء"

        return repository.syncUpcomingMonths(
            context = applicationContext,
            latitude = lat,
            longitude = lon,
            cityName = city,
            forceRefresh = true
        ).fold(
            onSuccess = {
                PrayerAlarmScheduler.scheduleUpcomingAlarms(applicationContext)
                Result.success()
            },
            onFailure = {
                Result.retry()
            }
        )
    }
}

object PrayerTimesSyncScheduler {
    private const val PERIODIC_WORK_NAME = "prayer_times_weekly_sync"
    private const val IMMEDIATE_WORK_NAME = "prayer_times_immediate_sync"

    fun ensureScheduled(context: Context) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val periodicWork = PeriodicWorkRequestBuilder<PrayerTimesSyncWorker>(7, TimeUnit.DAYS)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            PERIODIC_WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            periodicWork
        )
    }

    fun triggerImmediateSync(context: Context) {
        val work = OneTimeWorkRequestBuilder<PrayerTimesSyncWorker>().build()
        WorkManager.getInstance(context).enqueueUniqueWork(
            IMMEDIATE_WORK_NAME,
            ExistingWorkPolicy.REPLACE,
            work
        )
    }
}
