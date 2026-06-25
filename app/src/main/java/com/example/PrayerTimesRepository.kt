package com.example

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.sqrt

data class PrayerTimes(
    val fajr: String = "--:--",
    val shuruq: String = "--:--",
    val dhuhr: String = "--:--",
    val asr: String = "--:--",
    val maghrib: String = "--:--",
    val isha: String = "--:--"
)

data class OfficialPrayerCity(
    val id: Int,
    val name: String,
    val lat: Double,
    val lon: Double
)

val officialPrayerCities = listOf(
    OfficialPrayerCity(80, "الدار البيضاء", 33.5731, -7.5898),
    OfficialPrayerCity(69, "الرباط سلا", 34.0209, -6.8416),
    OfficialPrayerCity(69, "سلا", 34.0333, -6.8000),
    OfficialPrayerCity(32, "فاس", 34.0331, -5.0003),
    OfficialPrayerCity(2, "وجدة", 34.6814, -1.9086),
    OfficialPrayerCity(84, "مراكش", 31.6295, -7.9811),
    OfficialPrayerCity(95, "أكادير", 30.4202, -9.5982),
    OfficialPrayerCity(48, "طنجة", 35.7595, -5.8340),
    OfficialPrayerCity(43, "مكناس", 33.8935, -5.5473),
    OfficialPrayerCity(67, "القنيطرة", 34.2610, -6.5802),
    OfficialPrayerCity(40, "تطوان", 35.5785, -5.3684),
    OfficialPrayerCity(70, "خريبكة", 32.8811, -6.9063),
    OfficialPrayerCity(97, "الصويرة", 31.5085, -9.7595),
    OfficialPrayerCity(92, "آسفي", 32.2994, -9.2372),
    OfficialPrayerCity(88, "الجديدة", 33.2316, -8.5007),
    OfficialPrayerCity(62, "بني ملال", 32.3373, -6.3498),
    OfficialPrayerCity(17, "تازة", 34.2100, -4.0100),
    OfficialPrayerCity(8, "الناظور", 35.1681, -2.9300),
    OfficialPrayerCity(71, "ورزازات", 30.9189, -6.8934),
    OfficialPrayerCity(103, "العيون", 27.1253, -13.1625),
    OfficialPrayerCity(105, "الداخلة", 23.6848, -15.9575),
    OfficialPrayerCity(98, "كلميم", 28.9869, -10.0573),
    OfficialPrayerCity(23, "الراشيدية", 31.9314, -4.4244),
    OfficialPrayerCity(15, "الحسيمة", 35.2472, -3.9322),
    OfficialPrayerCity(90, "تارودانت", 30.4703, -8.8770),
    OfficialPrayerCity(46, "سيدي قاسم", 34.2264, -5.7033),
    OfficialPrayerCity(45, "خنيفرة", 32.9395, -5.6687),
    OfficialPrayerCity(50, "القصر الكبير", 34.9965, -5.9017),
    OfficialPrayerCity(55, "الخميسات", 33.8151, -6.0663),
    OfficialPrayerCity(35, "إفران", 33.5228, -5.1051)
)

fun distanceKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val p = 0.017453292519943295 // Math.PI / 180
    val a = 0.5 - cos((lat2 - lat1) * p) / 2 +
            cos(lat1 * p) * cos(lat2 * p) *
            (1 - cos((lon2 - lon1) * p)) / 2
    return 12742 * asin(sqrt(a)) // 2 * R; R = 6371 km
}

class PrayerTimesRepository(private val context: Context? = null) {

    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    suspend fun getPrayerTimes(
        latitude: Double = 33.5731,
        longitude: Double = -7.5898,
        cityName: String = "الدار البيضاء",
        context: Context? = null
    ): Result<PrayerTimes> = withContext(Dispatchers.IO) {
        try {
            val targetCity = resolveOfficialCity(latitude, longitude, cityName)
            val today = LocalDate.now()
            val effectiveContext = context ?: this@PrayerTimesRepository.context

            if (effectiveContext != null) {
                syncUpcomingMonths(
                    context = effectiveContext,
                    latitude = latitude,
                    longitude = longitude,
                    cityName = cityName,
                    forceRefresh = false
                )

                PrayerTimesCacheStore.getPrayerTimesForDate(effectiveContext, today)?.let { cached ->
                    return@withContext Result.success(cached)
                }
            }

            fetchMonthSchedule(targetCity.id, today.year, today.monthValue, effectiveContext)?.get(today.dayOfMonth)?.let {
                return@withContext Result.success(it)
            }

            Result.failure(IllegalStateException("No official prayer times available"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun syncUpcomingMonths(
        context: Context,
        latitude: Double,
        longitude: Double,
        cityName: String,
        forceRefresh: Boolean
    ): Result<OfficialPrayerCity> = withContext(Dispatchers.IO) {
        try {
            val targetCity = resolveOfficialCity(latitude, longitude, cityName)
            val startDate = LocalDate.now().withDayOfMonth(1)
            val monthsToSync = (0..2).map { startDate.plusMonths(it.toLong()) }

            monthsToSync.forEach { monthDate ->
                val shouldFetch = forceRefresh || !PrayerTimesCacheStore.hasMonthSchedule(context, monthDate.year, monthDate.monthValue)
                if (shouldFetch) {
                    val schedule = fetchMonthSchedule(targetCity.id, monthDate.year, monthDate.monthValue, context)
                    if (schedule != null && schedule.isNotEmpty()) {
                        PrayerTimesCacheStore.saveMonthSchedule(
                            context = context,
                            cityId = targetCity.id,
                            cityName = targetCity.name,
                            year = monthDate.year,
                            month = monthDate.monthValue,
                            days = schedule
                        )
                    }
                }
            }

            PrayerTimesCacheStore.pruneOldMonths(context, LocalDate.now().minusMonths(1).withDayOfMonth(1))
            Result.success(targetCity)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun resolveOfficialCity(latitude: Double, longitude: Double, cityName: String): OfficialPrayerCity {
        val normalizedInput = normalizeCityName(cityName)
        officialPrayerCities.firstOrNull { normalizeCityName(it.name) == normalizedInput }?.let { return it }
        return officialPrayerCities.minByOrNull { distanceKm(latitude, longitude, it.lat, it.lon) }
            ?: officialPrayerCities.first()
    }

    private fun normalizeCityName(value: String): String {
        return value
            .trim()
            .lowercase()
            .replace("é", "e")
            .replace("è", "e")
            .replace("ê", "e")
            .replace("â", "a")
            .replace("î", "i")
            .replace("ô", "o")
            .replace("û", "u")
            .replace("أ", "ا")
            .replace("إ", "ا")
            .replace("آ", "ا")
            .replace("ى", "ي")
            .replace("ة", "ه")
            .replace("\\s+".toRegex(), "")
    }

    private fun parseMonthSchedule(jsonString: String): Map<Int, PrayerTimes> {
        val root = JSONObject(jsonString)
        val result = mutableMapOf<Int, PrayerTimes>()

        root.keys().forEach { dayKey ->
            val day = dayKey.toIntOrNull() ?: return@forEach
            val dayJson = root.optJSONObject(dayKey) ?: return@forEach
            result[day] = PrayerTimes(
                fajr = dayJson.optString("fajr", "--:--"),
                shuruq = dayJson.optString("sunrise", "--:--"),
                dhuhr = dayJson.optString("dohr", "--:--"),
                asr = dayJson.optString("asr", "--:--"),
                maghrib = dayJson.optString("maghreb", "--:--"),
                isha = dayJson.optString("ichaa", "--:--")
            )
        }

        return result
    }

    private fun fetchMonthSchedule(cityId: Int, year: Int, month: Int, context: Context?): Map<Int, PrayerTimes>? {
        // 1. Try Network
        val urls = listOf(
            "https://raw.githubusercontent.com/ZakariaMahmoud/Morocco-Prayer-Times-API/master/$year/$cityId/$month.json",
            "https://www.prayertimes.mahmoud.ma/api/$cityId/$year/$month"
        )

        urls.forEach { url ->
            try {
                val request = Request.Builder().url(url).build()
                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) return@use
                    val body = response.body?.string().orEmpty()
                    if (body.isNotBlank() && body.trim() != "0") {
                        val parsed = parseMonthSchedule(body)
                        if (parsed.isNotEmpty()) {
                            return parsed
                        }
                    }
                }
            } catch (_: Exception) {
            }
        }

        // 2. Fallback to Offline Assets
        if (context != null) {
            return loadOfflineSchedule(cityId, year, month, context)
        }

        return null
    }

    private fun loadOfflineSchedule(cityId: Int, year: Int, month: Int, context: Context): Map<Int, PrayerTimes>? {
        return try {
            val json = context.assets.open("prayer_times_offline.json").bufferedReader().use { it.readText() }
            val root = JSONObject(json)
            val cityObj = root.optJSONObject(cityId.toString()) ?: return null
            val yearObj = cityObj.optJSONObject(year.toString()) ?: return null
            val monthObj = yearObj.optJSONObject(month.toString()) ?: return null
            
            val result = mutableMapOf<Int, PrayerTimes>()
            monthObj.keys().forEach { dayKey ->
                val day = dayKey.toIntOrNull() ?: return@forEach
                val dayJson = monthObj.optJSONObject(dayKey) ?: return@forEach
                result[day] = PrayerTimes(
                    fajr = dayJson.optString("fajr", "--:--"),
                    shuruq = dayJson.optString("sunrise", "--:--"),
                    dhuhr = dayJson.optString("dohr", "--:--"),
                    asr = dayJson.optString("asr", "--:--"),
                    maghrib = dayJson.optString("maghreb", "--:--"),
                    isha = dayJson.optString("ichaa", "--:--")
                )
            }
            result
        } catch (e: Exception) {
            null
        }
    }
}
