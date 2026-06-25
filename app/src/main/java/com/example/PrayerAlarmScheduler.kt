package com.example

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.aistudio.quran.mwkpqz.R
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId

class PrayerNotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val prayerName = intent.getStringExtra("extra_prayer_name") ?: "الصلاة"
        val minutesBefore = intent.getIntExtra("extra_minutes_before", 0)
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "PRAYER_ALERTS_CHANNEL"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "أوقات الصلاة - تذكيرات",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "قناة لإشعارات أوقات الصلاة وتنبيهات الأذان"
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(channel)
        }

        val title = if (minutesBefore > 0) "تبقى $minutesBefore دقائق!" else "حان الآن!"
        val message = if (minutesBefore > 0) {
            "بقي $minutesBefore دقائق على وقت صلاة $prayerName. استعد للوضوء والصلاة."
        } else {
            "حان الآن وقت صلاة $prayerName. أقم صلاتك يرحمك الله."
        }

        val notifyIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            notifyIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_prayer_alert)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        val notificationId = (prayerName.hashCode() * 31) + minutesBefore
        notificationManager.notify(notificationId, notification)

        if (minutesBefore == 0) {
            val prefs = context.getSharedPreferences("prayer_settings", Context.MODE_PRIVATE)
            val selectedAdhanKey = prefs.getString("selected_muezzin_key", AdhanAudioCatalog.defaultOption().key)
                ?: AdhanAudioCatalog.defaultOption().key
            val serviceIntent = Intent(context, AdhanPlaybackService::class.java).apply {
                putExtra(AdhanPlaybackService.EXTRA_PRAYER_NAME, prayerName)
                putExtra(AdhanPlaybackService.EXTRA_ADHAN_KEY, selectedAdhanKey)
            }
            ContextCompat.startForegroundService(context, serviceIntent)
        }

        PrayerAlarmScheduler.scheduleUpcomingAlarms(context)
    }
}

object PrayerAlarmScheduler {
    private const val ACTION_PRAYER_NOTIFICATION = "com.example.ACTION_PRAYER_NOTIFICATION"
    private const val TRACKING_PREFS = "scheduled_prayer_alarm_state"
    private const val TRACKING_KEY = "request_codes"

    fun scheduleUpcomingAlarms(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        cancelTrackedAlarms(context, alarmManager)

        val prefs = context.getSharedPreferences("prayer_settings", Context.MODE_PRIVATE)
        val minutesBefore = prefs.getInt("pre_prayer_alarm_minutes", 5)
        val trackedCodes = mutableSetOf<String>()

        val upcomingDates = (0L..14L).map { LocalDate.now().plusDays(it) }
        upcomingDates.forEach { date ->
            val dayTimes = PrayerTimesCacheStore.getPrayerTimesForDate(context, date) ?: return@forEach
            val prayers = listOf(
                Triple("fajr", "الفجر", dayTimes.fajr),
                Triple("dhuhr", "الظهر", dayTimes.dhuhr),
                Triple("asr", "العصر", dayTimes.asr),
                Triple("maghrib", "المغرب", dayTimes.maghrib),
                Triple("isha", "العشاء", dayTimes.isha)
            )

            prayers.forEach { (key, displayName, timeStr) ->
                if (!prefs.getBoolean("enable_$key", true)) return@forEach
                val time = parseTimeSafely(timeStr) ?: return@forEach

                scheduleSingleAlarm(context, alarmManager, displayName, date, time, 0)?.let {
                    trackedCodes += it.toString()
                }
                if (minutesBefore > 0) {
                    scheduleSingleAlarm(context, alarmManager, displayName, date, time, minutesBefore)?.let {
                        trackedCodes += it.toString()
                    }
                }
            }
        }

        context.getSharedPreferences(TRACKING_PREFS, Context.MODE_PRIVATE)
            .edit()
            .putStringSet(TRACKING_KEY, trackedCodes)
            .apply()
    }

    private fun cancelTrackedAlarms(context: Context, alarmManager: AlarmManager) {
        val storedCodes = context.getSharedPreferences(TRACKING_PREFS, Context.MODE_PRIVATE)
            .getStringSet(TRACKING_KEY, emptySet())
            .orEmpty()

        storedCodes.forEach { stored ->
            val requestCode = stored.toIntOrNull() ?: return@forEach
            val intent = Intent(context, PrayerNotificationReceiver::class.java).apply {
                action = ACTION_PRAYER_NOTIFICATION
            }
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            alarmManager.cancel(pendingIntent)
        }
    }

    private fun scheduleSingleAlarm(
        context: Context,
        alarmManager: AlarmManager,
        prayerName: String,
        date: LocalDate,
        time: LocalTime,
        minutesBefore: Int
    ): Int? {
        var alarmTime = LocalDateTime.of(date, time)
        if (minutesBefore > 0) {
            alarmTime = alarmTime.minusMinutes(minutesBefore.toLong())
        }

        if (alarmTime.isBefore(LocalDateTime.now())) {
            return null
        }

        val requestCode = "${date}_${prayerName}_${minutesBefore}".hashCode()
        val intent = Intent(context, PrayerNotificationReceiver::class.java).apply {
            action = ACTION_PRAYER_NOTIFICATION
            putExtra("extra_prayer_name", prayerName)
            putExtra("extra_minutes_before", minutesBefore)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val zonedDateTime = alarmTime.atZone(ZoneId.systemDefault())
        val epochMilli = zonedDateTime.toInstant().toEpochMilli()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, epochMilli, pendingIntent)
        } else {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, epochMilli, pendingIntent)
        }
        return requestCode
    }

    private fun parseTimeSafely(timeStr: String): LocalTime? {
        return try {
            LocalTime.parse(timeStr.replace(" ", "").trim())
        } catch (e: Exception) {
            null
        }
    }
}
