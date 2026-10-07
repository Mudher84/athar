package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.AtharDatabase
import com.example.util.ArabicText
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * يفتح assets/databases/athar.db كما يفعل التطبيق (createFromAsset)، فيتحقق Room من تطابق
 * بصمة المخطط، ثم نتحقق من حجم البيانات ومن أن فهرس FTS يعمل بالتوحيد نفسه في Kotlin.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class PrebuiltDatabaseTest {

    private lateinit var database: AtharDatabase

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        context.deleteDatabase(DB_NAME)
        database = Room.databaseBuilder(context, AtharDatabase::class.java, DB_NAME)
            .createFromAsset(AtharDatabase.ASSET_PATH)
            .allowMainThreadQueries()
            .build()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun corpusIsComplete() = runBlocking {
        val dao = database.atharDao()
        assertEquals(EXPECTED_HADITHS, dao.countAllHadiths().first())
        assertEquals(7, dao.getBooksWithCount().first().size)
        val narrators = dao.getNarratorsWithCount().first()
        assertTrue(narrators.size >= 40)
        assertTrue(narrators.all { it.hadithCount >= 0 })
    }

    @Test
    fun ftsFindsUnvocalizedQueries() = runBlocking {
        val dao = database.atharDao()
        val prayer = dao.countSearch(ArabicText.buildMatchQuery("الصلاة")!!, null, null, null, false).first()
        assertTrue("الصلاة: $prayer", prayer > 100)
        // بلا «ال» يطابق الصيغ المفهرسة بلا أداة التعريف
        val bare = dao.countSearch(ArabicText.buildMatchQuery("صلاة")!!, null, null, null, false).first()
        assertTrue(bare >= prayer)
        val malikNafi = dao.countSearch(ArabicText.buildMatchQuery("مالك نافع")!!, null, null, null, false).first()
        assertTrue("مالك نافع: $malikNafi", malikNafi > 100)
    }

    @Test
    fun regionCountsAddUp() = runBlocking {
        val dao = database.atharDao()
        val byRegion = listOf("المدينة", "الشام", "مشترك").sumOf { dao.countHadiths(null, it, null, false).first() }
        assertEquals(EXPECTED_HADITHS, byRegion)
    }

    private companion object {
        const val DB_NAME = "athar-test.db"
        /** يتغير فقط بإعادة بناء القاعدة عبر tools/build_athar_db.py */
        const val EXPECTED_HADITHS = 4715
    }
}
