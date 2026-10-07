package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.local.dao.AtharDao
import com.example.data.local.entity.BookEntity
import com.example.data.local.entity.FavoriteHadithEntity
import com.example.data.local.entity.HadithEntity
import com.example.data.local.entity.HadithFtsEntity
import com.example.data.local.entity.HadithNarratorChainEntity
import com.example.data.local.entity.NarratorEntity

/**
 * قاعدة «أثر» المحلية بالكامل.
 *
 * البيانات (الكتب، الرواة، المرويات، سلاسل الإسناد، فهرس البحث) مبنية مسبقاً في
 * assets/databases/athar.db عبر tools/build_athar_db.py من مخطط Room المصدَّر في
 * app/schemas/، وتُنسخ إلى الجهاز عند أول تشغيل دون أي اتصال بالشبكة.
 */
@Database(
    entities = [
        BookEntity::class,
        NarratorEntity::class,
        HadithEntity::class,
        HadithNarratorChainEntity::class,
        FavoriteHadithEntity::class,
        HadithFtsEntity::class
    ],
    version = AtharDatabase.VERSION,
    exportSchema = true
)
abstract class AtharDatabase : RoomDatabase() {

    abstract fun atharDao(): AtharDao

    companion object {
        /** يجب أن يساوي PRAGMA user_version في athar.db المبني مسبقاً */
        const val VERSION = 3
        const val ASSET_PATH = "databases/athar.db"
        private const val DATABASE_NAME = "athar.db"

        @Volatile
        private var INSTANCE: AtharDatabase? = null

        fun getInstance(context: Context): AtharDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AtharDatabase::class.java,
                    DATABASE_NAME
                )
                    .createFromAsset(ASSET_PATH)
                    // نسخة أقدم على الجهاز (بيانات تجريبية) تُستبدل بالقاعدة المبنية مسبقاً
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
