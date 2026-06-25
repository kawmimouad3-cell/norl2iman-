package com.example

import android.content.Context
import androidx.room.*

@Entity(tableName = "verse_cache")
data class VerseCacheEntity(
    @PrimaryKey val verseId: Int,
    val translation: String,
    val translationEn: String,
    val tafsir: String
)

@Dao
interface VerseCacheDao {
    @Query("SELECT * FROM verse_cache WHERE verseId = :verseId")
    suspend fun getVerseCache(verseId: Int): VerseCacheEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVerseCache(entity: VerseCacheEntity)
    
    @Query("SELECT COUNT(*) FROM verse_cache")
    suspend fun getCacheCount(): Int
}

@Database(entities = [VerseCacheEntity::class], version = 2, exportSchema = false)
abstract class QuranOfflineDatabase : RoomDatabase() {
    abstract fun verseCacheDao(): VerseCacheDao

    companion object {
        @Volatile
        private var INSTANCE: QuranOfflineDatabase? = null

        fun getDatabase(context: Context): QuranOfflineDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    QuranOfflineDatabase::class.java,
                    "quran_offline_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
