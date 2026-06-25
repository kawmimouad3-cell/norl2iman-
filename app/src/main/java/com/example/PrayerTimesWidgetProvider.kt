package com.example

import com.aistudio.quran.mwkpqz.R
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import android.widget.RemoteViews
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class PrayerTimesWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    companion object {
        fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
            val prefs = context.getSharedPreferences("widget_prefs", Context.MODE_PRIVATE)
            val fajr = prefs.getString("fajr", null)
            val dhuhr = prefs.getString("dhuhr", null)
            val asr = prefs.getString("asr", null)
            val maghrib = prefs.getString("maghrib", null)
            val isha = prefs.getString("isha", null)
            val city = prefs.getString("city", "جاري التحديد...")

            val views = RemoteViews(context.packageName, R.layout.widget_prayer_times)

            // Setup click intent to open app
            val intent = Intent(context, MainActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
            views.setOnClickPendingIntent(R.id.widget_title, pendingIntent)
            views.setOnClickPendingIntent(R.id.widget_countdown, pendingIntent)
            views.setOnClickPendingIntent(R.id.widget_city, pendingIntent)

            if (fajr != null && dhuhr != null && asr != null && maghrib != null && isha != null) {
                val now = LocalTime.now()
                val parsedTimes = listOf(
                    "الفجر" to parseTimeSafely(fajr),
                    "الظهر" to parseTimeSafely(dhuhr),
                    "العصر" to parseTimeSafely(asr),
                    "المغرب" to parseTimeSafely(maghrib),
                    "العشاء" to parseTimeSafely(isha)
                )

                var nextPrayerName = "الفجر"
                var nextPrayerTime: LocalTime? = null

                for (pt in parsedTimes) {
                    if (pt.second != null && now.isBefore(pt.second)) {
                        nextPrayerName = pt.first
                        nextPrayerTime = pt.second
                        break
                    }
                }

                var isNextDay = false
                if (nextPrayerTime == null) {
                    nextPrayerName = parsedTimes.first().first
                    nextPrayerTime = parsedTimes.first().second
                    isNextDay = true
                }

                if (nextPrayerTime != null) {
                    views.setTextViewText(R.id.widget_title, "الصلاة القادمة: $nextPrayerName")
                    views.setTextViewText(R.id.widget_city, city)

                    views.setTextViewText(R.id.widget_fajr, fajr)
                    views.setTextViewText(R.id.widget_dhuhr, dhuhr)
                    views.setTextViewText(R.id.widget_asr, asr)
                    views.setTextViewText(R.id.widget_maghrib, maghrib)
                    views.setTextViewText(R.id.widget_isha, isha)

                    val secondsFromNowToMidnight = ChronoUnit.SECONDS.between(now, LocalTime.MAX)
                    val secondsFromMidnightToPrayer = nextPrayerTime.toSecondOfDay()
                    
                    val secondsRemaining = if (isNextDay) {
                        secondsFromNowToMidnight + secondsFromMidnightToPrayer
                    } else {
                        ChronoUnit.SECONDS.between(now, nextPrayerTime)
                    }

                    // For the Chronometer to count down
                    val timeMillis = SystemClock.elapsedRealtime() + (secondsRemaining * 1000)
                    views.setChronometer(R.id.widget_countdown, timeMillis, "%s", true)
                }
            } else {
                views.setTextViewText(R.id.widget_title, "افتح التطبيق لتحديث الأوقات")
                views.setTextViewText(R.id.widget_countdown, "--:--:--")
                views.setTextViewText(R.id.widget_city, city)
            }

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }

        private fun parseTimeSafely(timeStr: String): LocalTime? {
            return try {
                val cleanTime = timeStr.trim().split(" ").first()
                LocalTime.parse(cleanTime)
            } catch (e: Exception) {
                null
            }
        }
    }
}
