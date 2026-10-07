package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.AtharDatabase
import com.example.data.local.dao.AtharDao
import com.example.data.local.entity.BookEntity
import com.example.data.local.entity.FavoriteHadithEntity
import com.example.data.local.entity.HadithEntity
import com.example.data.local.entity.HadithNarratorChainEntity
import com.example.data.local.entity.NarratorEntity
import com.example.data.repository.AtharRepositoryImpl
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class AtharDatabaseTest {

    private lateinit var database: AtharDatabase
    private lateinit var dao: AtharDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, AtharDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = database.atharDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun testInsertAndRetrieveExactSchema() = runBlocking {
        // 1. Insert Book
        val bookId = dao.insertBook(
            BookEntity(
                id = 1,
                title = "موطأ الإمام مالك",
                author = "مالك بن أنس",
                era = "أموي/أوائل العباسي"
            )
        ).toInt()

        // 2. Insert Narrators
        val malikId = dao.insertNarrator(
            NarratorEntity(
                id = 1,
                name = "مالك بن أنس الأصبحي",
                popularName = "الإمام مالك",
                region = "المدينة",
                era = "أموي/أوائل العباسي",
                isTrusted = true,
                sectAffiliation = "سني مدني",
                notes = "إمام دار الهجرة"
            )
        ).toInt()

        val nafiId = dao.insertNarrator(
            NarratorEntity(
                id = 2,
                name = "نافع مولى عبد الله بن عمر",
                popularName = "نافع",
                region = "المدينة",
                era = "أموي",
                isTrusted = true,
                sectAffiliation = "سني مدني",
                notes = "سلسلة الذهب"
            )
        ).toInt()

        val ibnOmarId = dao.insertNarrator(
            NarratorEntity(
                id = 3,
                name = "عبد الله بن عمر بن الخطاب",
                popularName = "ابن عمر",
                region = "المدينة",
                era = "صحابي",
                isTrusted = true,
                sectAffiliation = "صحابي",
                notes = "صحابي جليل"
            )
        ).toInt()

        // 3. Insert Hadith
        val hadithId = dao.insertHadith(
            HadithEntity(
                id = 1,
                bookId = bookId,
                chapter = "كتاب وقوت الصلاة",
                pathBadge = "سند مدني خالص",
                rawSanad = "مالك عن نافع عن عبد الله بن عمر",
                matn = "أن رسول الله ﷺ قال: الذي تفوته صلاة العصر كأنما وُتر أهله وماله."
            )
        ).toInt()

        // 4. Insert Chain
        dao.insertChainLink(HadithNarratorChainEntity(hadithId = hadithId, narratorId = malikId, chainOrder = 1))
        dao.insertChainLink(HadithNarratorChainEntity(hadithId = hadithId, narratorId = nafiId, chainOrder = 2))
        dao.insertChainLink(HadithNarratorChainEntity(hadithId = hadithId, narratorId = ibnOmarId, chainOrder = 3))

        // Assert Hadiths
        val hadiths = dao.getAllHadiths().first()
        assertEquals(1, hadiths.size)
        assertEquals("كتاب وقوت الصلاة", hadiths[0].chapter)
        assertEquals("سند مدني خالص", hadiths[0].pathBadge)

        // Assert Chain Links
        val chain = dao.getChainLinksForHadith(hadithId).first()
        assertEquals(3, chain.size)
        assertEquals("مالك بن أنس الأصبحي", chain[0].name)
        assertEquals("نافع مولى عبد الله بن عمر", chain[1].name)
        assertEquals("عبد الله بن عمر بن الخطاب", chain[2].name)

        // Assert Region Filter
        val madinahNarrators = dao.getNarratorsByRegion("المدينة").first()
        assertEquals(3, madinahNarrators.size)

        // Assert Favorite
        dao.insertFavorite(FavoriteHadithEntity(hadithId = hadithId))
        val favs = dao.getFavoriteHadithIds().first()
        assertEquals(1, favs.size)
        assertEquals(1, favs[0])

        dao.deleteFavorite(hadithId)
        val afterDel = dao.getFavoriteHadithIds().first()
        assertTrue(afterDel.isEmpty())
    }

    @Test
    fun testArabicNormalization() {
        val raw = "أَهْلُ المَدِينَةِ وَالشَّامِ وَالأَنْدَلُسِ"
        val normalized = AtharRepositoryImpl.normalizeArabic(raw)
        assertEquals("اهل المدينه والشام والاندلس", normalized)
    }
}
