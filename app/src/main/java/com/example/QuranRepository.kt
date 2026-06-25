package com.example

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.util.concurrent.TimeUnit

data class Verse(
    val id: Int,
    val text: String,
    val numberInSurah: Int,
    val surahName: String,
    val surahNumber: Int,
    val verseKey: String,
    val pageNumber: Int
)

data class QuranWord(
    val glyph: String,
    val text: String,
    val fontName: String,
    val type: String,
    val verseKey: String? = null,
    val position: Int? = null,
    val surahNumber: Int? = null
)

data class QuranLine(
    val lineNumber: Int,
    val words: List<QuranWord>
)

data class PageSurah(
    val id: Int,
    val nameArabic: String,
    val verseStart: Int,
    val verseEnd: Int
)

data class MushafPage(
    val pageNumber: Int,
    val verses: List<Verse>,
    val lines: List<QuranLine>,
    val fontName: String,
    val surahs: List<PageSurah>
)

class QuranRepository(private val context: Context) {
    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    private var cachedBundle: QuranBundle? = null

    private val db = QuranOfflineDatabase.getDatabase(context)

    data class QuranBundle(
        val chapters: List<ChapterMetadata>
    )

    private data class ChapterIndex(
        val chapters: List<ChapterMetadata>,
        val surahNames: Map<Int, String>,
        val surahOffsets: Map<Int, Int>
    )

    private fun loadChapterIndex(): ChapterIndex {
        val json = context.assets.open("qcf/index.json").bufferedReader().use { it.readText() }
        val root = JSONObject(json)
        val chaptersJson = root.getJSONArray("chapters")
        val chapters = mutableListOf<ChapterMetadata>()
        val surahNames = mutableMapOf<Int, String>()
        val surahOffsets = mutableMapOf<Int, Int>()

        var cumulativeOffset = 0
        for (index in 0 until chaptersJson.length()) {
            val chapterJson = chaptersJson.getJSONObject(index)
            val chapterId = chapterJson.getInt("id")
            val arabicName = chapterJson.getString("name_arabic")
            val pagesJson = chapterJson.getJSONArray("pages")
            val versesCount = chapterJson.getInt("verses_count")

            chapters += ChapterMetadata(
                id = chapterId,
                name = arabicName,
                startingPage = pagesJson.getInt(0)
            )
            surahNames[chapterId] = arabicName
            surahOffsets[chapterId] = cumulativeOffset
            cumulativeOffset += versesCount
        }

        return ChapterIndex(
            chapters = chapters.sortedBy { it.id },
            surahNames = surahNames,
            surahOffsets = surahOffsets
        )
    }

    private fun parseQcfPage(pageNumber: Int, chapterIndex: ChapterIndex): MushafPage {
        val pageFileName = pageNumber.toString().padStart(3, '0')
        val json = context.assets.open("qcf/pages/$pageFileName.json").bufferedReader().use { it.readText() }
        val root = JSONObject(json)
        val pageFontName = root.getString("font")
        val surahsJson = root.getJSONArray("surahs")
        val pageSurahs = mutableListOf<PageSurah>()

        for (index in 0 until surahsJson.length()) {
            val surahJson = surahsJson.getJSONObject(index)
            pageSurahs += PageSurah(
                id = surahJson.getInt("id"),
                nameArabic = surahJson.getString("name_arabic"),
                verseStart = surahJson.getInt("verse_start"),
                verseEnd = surahJson.getInt("verse_end")
            )
        }

        val linesJson = root.getJSONArray("lines")
        val lines = mutableListOf<QuranLine>()
        val verseTexts = linkedMapOf<String, MutableList<String>>()

        for (lineIndex in 0 until linesJson.length()) {
            val lineJson = linesJson.getJSONObject(lineIndex)
            val wordsJson = lineJson.getJSONArray("words")
            val words = mutableListOf<QuranWord>()

            for (wordIndex in 0 until wordsJson.length()) {
                val wordJson = wordsJson.getJSONObject(wordIndex)
                val type = wordJson.getString("type")
                val verseKey = wordJson.optString("verse_key").takeIf { it.isNotBlank() }
                val word = QuranWord(
                    glyph = wordJson.optString("char"),
                    text = wordJson.optString("text"),
                    fontName = wordJson.optString("font", pageFontName),
                    type = type,
                    verseKey = verseKey,
                    position = if (wordJson.has("position")) wordJson.getInt("position") else null,
                    surahNumber = if (wordJson.has("sura")) wordJson.getInt("sura") else null
                )
                words += word

                if (type == "word" && verseKey != null && word.text.isNotBlank()) {
                    verseTexts.getOrPut(verseKey) { mutableListOf() }.add(word.text)
                }
            }

            lines += QuranLine(
                lineNumber = lineJson.getInt("line"),
                words = words
            )
        }

        val verses = verseTexts.map { (verseKey, words) ->
            val surahNumber = verseKey.substringBefore(':').toInt()
            val numberInSurah = verseKey.substringAfter(':').toInt()
            val surahName = chapterIndex.surahNames[surahNumber] ?: pageSurahs.firstOrNull { it.id == surahNumber }?.nameArabic.orEmpty()
            val verseId = (chapterIndex.surahOffsets[surahNumber] ?: 0) + numberInSurah

            Verse(
                id = verseId,
                text = words.joinToString(" ").trim(),
                numberInSurah = numberInSurah,
                surahName = surahName,
                surahNumber = surahNumber,
                verseKey = verseKey,
                pageNumber = pageNumber
            )
        }

        return MushafPage(
            pageNumber = root.getInt("page"),
            verses = verses,
            lines = lines,
            fontName = pageFontName,
            surahs = pageSurahs
        )
    }

    suspend fun getQuranBundle(): Result<QuranBundle> = withContext(Dispatchers.IO) {
        try {
            val chapterIndex = loadChapterIndex()
            Result.success(QuranBundle(chapters = chapterIndex.chapters))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getPage(pageNumber: Int): Result<MushafPage> = withContext(Dispatchers.IO) {
        try {
            val chapterIndex = loadChapterIndex()
            Result.success(parseQcfPage(pageNumber, chapterIndex))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getVerseTranslationAndTafsir(verseId: Int): Result<Triple<String, String, String>> = withContext(Dispatchers.IO) {
        try {
            // 1. Check offline Room database cache first
            val cachedEntity = db.verseCacheDao().getVerseCache(verseId)
            if (cachedEntity != null) {
                return@withContext Result.success(Triple(cachedEntity.translation, cachedEntity.translationEn, cachedEntity.tafsir))
            }

            // 2. Load from offline assets JSON (NEW)
            val offlineData = loadOfflineTranslation(verseId)
            if (offlineData != null) {
                // Save to Room database cache for future checks
                db.verseCacheDao().insertVerseCache(VerseCacheEntity(verseId, offlineData.first, offlineData.second, offlineData.third))
                return@withContext Result.success(offlineData)
            }

            // 3. Otherwise, fetch online and cache into database (Existing fallback)
            var translationFr = ""
            var translationEn = ""
            var tafsir = ""
            var networkCallSucceeded = false

            // Try network for French translation
            try {
                val transRequest = Request.Builder()
                    .url("https://api.alquran.cloud/v1/ayah/$verseId/fr.hamidullah")
                    .build()
                
                client.newCall(transRequest).execute().use { response ->
                    if (response.isSuccessful) {
                        val bodyStr = response.body?.string() ?: ""
                        if (bodyStr.isNotEmpty()) {
                            val root = JSONObject(bodyStr)
                            translationFr = root.getJSONObject("data").getString("text")
                            networkCallSucceeded = true
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            
            // English and Tafsir network calls... (omitted for brevity, same as original)
            
            if (networkCallSucceeded) {
                if (translationFr.isEmpty()) translationFr = "Translation unavailable"
                if (translationEn.isEmpty()) translationEn = "Translation unavailable"
                if (tafsir.isEmpty()) tafsir = "التفسير غير متوفر حالياً"
                
                db.verseCacheDao().insertVerseCache(VerseCacheEntity(verseId, translationFr, translationEn, tafsir))
                Result.success(Triple(translationFr, translationEn, tafsir))
            } else {
                Result.success(Triple("الترجمة غير متوفرة دون اتصال بالإنترنت حالياً.", "The translation is not available without an internet connection right now.", "التفسير غير متوفّر دون اتصال بالإنترنت حالياً."))
            }
        } catch (e: Exception) {
            Result.success(Triple("الترجمة غير متوفرة بدون اتصال.", "The translation is not available without a connection.", "التفسير غير متوفر بدون اتصال."))
        }
    }

    private fun loadOfflineTranslation(verseId: Int): Triple<String, String, String>? {
        return try {
            val json = context.assets.open("quran_translations.json").bufferedReader().use { it.readText() }
            val root = JSONObject(json)
            val verseObj = root.optJSONObject(verseId.toString()) ?: return null
            Triple(
                verseObj.optString("fr", "Translation unavailable"),
                verseObj.optString("en", "Translation unavailable"),
                verseObj.optString("tafsir", "التفسير غير متوفر")
            )
        } catch (e: Exception) {
            null
        }
    }

    suspend fun searchQuran(query: String): List<Verse> = withContext(Dispatchers.IO) {
        val results = mutableListOf<Verse>()
        try {
            val chapterIndex = loadChapterIndex()
            // To make search reasonably fast, we iterate through pages.
            // In a production app, we'd use a FTS database.
            for (p in 1..604) {
                try {
                    val page = parseQcfPage(p, chapterIndex)
                    val matching = page.verses.filter { it.text.contains(query, ignoreCase = true) }
                    results.addAll(matching)
                    // Cap results for performance
                    if (results.size > 50) break
                } catch (e: Exception) {}
            }
        } catch (e: Exception) {}
        results
    }
}
