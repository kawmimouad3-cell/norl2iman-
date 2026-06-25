package com.example

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class PrayerMaintenanceReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        PrayerTimesSyncScheduler.ensureScheduled(context)
        PrayerTimesSyncScheduler.triggerImmediateSync(context)
        PrayerAlarmScheduler.scheduleUpcomingAlarms(context)
    }
}
