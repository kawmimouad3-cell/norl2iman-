package com.example

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.aistudio.quran.mwkpqz.R
class AdhanPlaybackService : Service() {
    private var mediaPlayer: MediaPlayer? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val prayerName = intent?.getStringExtra(EXTRA_PRAYER_NAME) ?: "الصلاة"
        val adhanKey = intent?.getStringExtra(EXTRA_ADHAN_KEY)
        val option = AdhanAudioCatalog.findByKey(adhanKey)

        startForeground(NOTIFICATION_ID, buildNotification(prayerName, option.label))
        play(option.rawResId)
        return START_NOT_STICKY
    }

    private fun play(rawResId: Int) {
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer.create(this, rawResId)?.apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
            setOnCompletionListener {
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
            }
            start()
        }
    }

    private fun buildNotification(prayerName: String, adhanLabel: String): Notification {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "الأذان الجاري",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "تشغيل الأذان عند دخول وقت الصلاة"
            }
            notificationManager.createNotificationChannel(channel)
        }

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_prayer_alert)
            .setContentTitle("حان وقت صلاة $prayerName")
            .setContentText("يتم الآن تشغيل $adhanLabel")
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()
    }

    override fun onDestroy() {
        mediaPlayer?.release()
        mediaPlayer = null
        super.onDestroy()
    }

    companion object {
        const val EXTRA_PRAYER_NAME = "extra_prayer_name"
        const val EXTRA_ADHAN_KEY = "extra_adhan_key"
        private const val CHANNEL_ID = "ADHAN_PLAYBACK_CHANNEL"
        private const val NOTIFICATION_ID = 7001
    }
}
