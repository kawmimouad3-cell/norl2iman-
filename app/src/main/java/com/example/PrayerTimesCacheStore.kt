package com.example

import android.content.Context
import org.json.JSONObject
import java.io.File
import java.time.LocalDate

object PrayerTimesCacheStore {
    private const val CACHE_FILE_NAME = "prayer_times_cache.json"

    private fun cacheFile(context: Context): File = File(context.filesDir, CACHE_FILE_NAME)

    private fun readRoot(context: Context): JSONObject {
        val file = cacheFile(context)
        if (!file.exists()) {
            return JSONObject().apply {
                put("months", JSONObject())
            }
        }

        return try {
            val content = file.readText()
            if (content.isBlank()) {
                JSONObject().apply { put("months", JSONObject()) }
            } else {
                JSONObject(content)
            }
        } catch (_: Exception) {
            JSONObject().apply { put("months", JSONObject()) }
        }
    }

    private fun writeRoot(context: Context, root: JSONObject) {
        cacheFile(context).writeText(root.toString())
    }

    private fun toMonthKey(year: Int, month: Int): String = "%04d-%02d".format(year, month)

    private fun prayerTimesToJson(prayerTimes: PrayerTimes): JSONObject {
        return JSONObject().apply {
            put("fajr", prayerTimes.fajr)
            put("shuruq", prayerTimes.shuruq)
            put("dhuhr", prayerTimes.dhuhr)
            put("asr", prayerTimes.asr)
            put("maghrib", prayerTimes.maghrib)
            put("isha", prayerTimes.isha)
        }
    }

    private fun jsonToPrayerTimes(json: JSONObject): PrayerTimes {
        return PrayerTimes(
            fajr = json.optString("fajr", "--:--"),
            shuruq = json.optString("shuruq", "--:--"),
            dhuhr = json.optString("dhuhr", "--:--"),
            asr = json.optString("asr", "--:--"),
            maghrib = json.optString("maghrib", "--:--"),
            isha = json.optString("isha", "--:--")
        )
    }

    fun saveMonthSchedule(
        context: Context,
        cityId: Int,
        cityName: String,
        year: Int,
        month: Int,
        days: Map<Int, PrayerTimes>
    ) {
        val root = readRoot(context)
        val months = root.optJSONObject("months") ?: JSONObject()
        val monthJson = JSONObject()

        days.toSortedMap().forEach { (day, prayerTimes) ->
            monthJson.put(day.toString(), prayerTimesToJson(prayerTimes))
        }

        months.put(toMonthKey(year, month), monthJson)
        root.put("months", months)
        root.put("city_id", cityId)
        root.put("city_name", cityName)
        root.put("updated_at", System.currentTimeMillis())
        writeRoot(context, root)
    }

    fun hasMonthSchedule(context: Context, year: Int, month: Int): Boolean {
        val months = readRoot(context).optJSONObject("months") ?: return false
        return months.has(toMonthKey(year, month))
    }

    fun getPrayerTimesForDate(context: Context, date: LocalDate): PrayerTimes? {
        val months = readRoot(context).optJSONObject("months") ?: return null
        val monthJson = months.optJSONObject(toMonthKey(date.year, date.monthValue)) ?: return null
        val dayJson = monthJson.optJSONObject(date.dayOfMonth.toString()) ?: return null
        return jsonToPrayerTimes(dayJson)
    }

    fun getCachedCityId(context: Context): Int? {
        val root = readRoot(context)
        return if (root.has("city_id")) root.optInt("city_id") else null
    }

    fun getCachedCityName(context: Context): String? {
        return readRoot(context).optString("city_name").takeIf { it.isNotBlank() }
    }

    fun pruneOldMonths(context: Context, minimumMonthToKeep: LocalDate) {
        val root = readRoot(context)
        val months = root.optJSONObject("months") ?: return
        val keysToRemove = mutableListOf<String>()

        months.keys().forEach { key ->
            val year = key.substringBefore("-").toIntOrNull() ?: return@forEach
            val month = key.substringAfter("-").toIntOrNull() ?: return@forEach
            if (year < minimumMonthToKeep.year || (year == minimumMonthToKeep.year && month < minimumMonthToKeep.monthValue)) {
                keysToRemove += key
            }
        }

        keysToRemove.forEach(months::remove)
        root.put("months", months)
        writeRoot(context, root)
    }
}
