package com.example

import android.content.Context
import androidx.paging.PagingSource
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.AtharDatabase
import com.example.data.local.dao.AtharDao
import com.example.data.local.entity.BookEntity
import com.example.data.local.entity.FavoriteHadithEntity
import com.example.data.local.entity.HadithEntity
import com.example.data.local.entity.HadithNarratorChainEntity
import com.example.data.local.entity.NarratorEntity
import com.example.domain.model.HadithListItem
import com.example.util.ArabicText
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/** مخطط Room واستعلامات الـDAO على قاعدة في الذاكرة ببيانات صغيرة معروفة. */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class AtharDatabaseTest {

    private lateinit var database: AtharDatabase
    private lateinit var dao: AtharDao

    @Before
    fun setup() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, AtharDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = database.atharDao()

        dao.insertBook(BookEntity(id = 1, title = "موطأ الإمام مالك", author = "مالك بن أنس الأصبحي", era = "القرن الثاني الهجري"))
        dao.insertNarrator(NarratorEntity(id = 15, name = "عبد الله بن عمر", popularName = "ابن عمر", region = "المدينة المنورة", era = "عصر الصحابة"))
        dao.insertNarrator(NarratorEntity(id = 16, name = "نافع مولى ابن عمر", popularName = "نافع", region = "المدينة المنورة", era = "عصر التابعين"))
        dao.insertNarrator(NarratorEntity(id = 17, name = "مالك بن أنس", popularName = "الإمام مالك", region = "المدينة المنورة", era = "أتباع التابعين"))
        dao.insertNarrator(NarratorEntity(id = 27, name = "عبد الرحمن بن عمرو الأوزاعي", popularName = "الأوزاعي", region = "الشام", era = "أتباع التابعين"))

        val connected = HadithEntity(
            id = 1, bookId = 1, chapter = "كتاب وقوت الصلاة", pathBadge = "مسار أهل المدينة المنورة",
            regionTag = "المدينة",
            rawSanad = "حَدَّثَنِي يَحْيَى عَنْ مَالِكٍ، عَنْ نَافِعٍ، عَنْ عَبْدِ اللَّهِ بْنِ عُمَرَ",
            matn = "أَنَّ رَسُولَ اللَّهِ صلى الله عليه وسلم قَالَ الَّذِي تَفُوتُهُ صَلاَةُ الْعَصْرِ كَأَنَّمَا وُتِرَ أَهْلَهُ وَمَالَهُ",
            sourceNumber = 21
        )
        val balagh = HadithEntity(
            id = 2, bookId = 1, chapter = "كتاب الصلاة", pathBadge = "مسار أهل الشام والدائرة الأموية",
            regionTag = "الشام", rawSanad = "وَحَدَّثَنِي عَنْ مَالِكٍ، أَنَّهُ بَلَغَهُ",
            matn = "أَنَّ الزَّكَاةَ فِي الْعَيْنِ", isMursalOrBalagh = true, transmissionNote = "بلاغ"
        )
        dao.insertHadith(connected)
        dao.insertHadith(balagh)
        dao.insertChainLink(HadithNarratorChainEntity(hadithId = 1, narratorId = 17, chainOrder = 1))
        dao.insertChainLink(HadithNarratorChainEntity(hadithId = 1, narratorId = 16, chainOrder = 2))
        dao.insertChainLink(HadithNarratorChainEntity(hadithId = 1, narratorId = 15, chainOrder = 3))
        dao.insertChainLink(HadithNarratorChainEntity(hadithId = 2, narratorId = 17, chainOrder = 1))

        // الفهرس يُملأ في القاعدة المبنية مسبقاً بالسكربت؛ هنا نملؤه يدوياً بنفس التوحيد
        val db = database.openHelper.writableDatabase
        for (h in listOf(connected, balagh)) {
            db.execSQL(
                "INSERT INTO hadith_fts(rowid, matn, sanad) VALUES (?, ?, ?)",
                arrayOf<Any>(h.id, ArabicText.normalizeForSearch(h.matn), ArabicText.normalizeForSearch(h.rawSanad))
            )
        }
    }

    @After
    fun tearDown() {
        database.close()
    }

    private fun load(source: PagingSource<Int, HadithListItem>): List<HadithListItem> = runBlocking {
        val result = source.load(PagingSource.LoadParams.Refresh(key = null, loadSize = 50, placeholdersEnabled = false))
        (result as PagingSource.LoadResult.Page).data
    }

    @Test
    fun listItemCarriesBookChainAndFlags() = runBlocking {
        val item = dao.getHadithItem(1).first()!!
        assertEquals("موطأ الإمام مالك", item.bookTitle)
        assertEquals(listOf(17, 16, 15), item.chain.map { it.narratorId })
        assertEquals("نافع مولى ابن عمر", item.chain[1].name)
        assertFalse(item.isFavorite)
        assertFalse(item.isMursalOrBalagh)
        assertEquals(21, item.sourceNumber)
    }

    @Test
    fun filtersNarrowPagesAndCounts() = runBlocking {
        assertEquals(2, load(dao.pageHadiths(null, null, null, false)).size)
        assertEquals(listOf(1), load(dao.pageHadiths(null, "المدينة", null, false)).map { it.id })
        assertEquals(listOf(1), load(dao.pageHadiths(null, null, 16, false)).map { it.id })
        assertEquals(listOf(1), load(dao.pageHadiths(null, null, null, true)).map { it.id })
        assertEquals(1, dao.countHadiths(null, "الشام", null, false).first())
        assertEquals(0, dao.countHadiths(null, "الشام", null, true).first())
    }

    @Test
    fun ftsSearchIgnoresTashkeelAndHamza() = runBlocking {
        val match = ArabicText.buildMatchQuery("صلاة العصر")!!
        assertEquals(listOf(1), load(dao.searchHadiths(match, null, null, null, false)).map { it.id })
        assertEquals(1, dao.countSearch(match, null, null, null, false).first())
        // كلمة في السند
        val bySanad = ArabicText.buildMatchQuery("نَافِع")!!
        assertEquals(1, dao.countSearch(bySanad, null, null, null, false).first())
        // البحث مع فلتر يستبعد النتيجة
        assertEquals(0, dao.countSearch(match, null, "الشام", null, false).first())
    }

    @Test
    fun favoritesPageAndSearch() = runBlocking {
        dao.insertFavorite(FavoriteHadithEntity(hadithId = 2))
        assertEquals(1, dao.countFavorites().first())
        assertTrue(dao.getHadithItem(2).first()!!.isFavorite)
        assertEquals(listOf(2), load(dao.pageFavorites()).map { it.id })
        val match = ArabicText.buildMatchQuery("الزكاة")!!
        assertEquals(listOf(2), load(dao.searchFavorites(match)).map { it.id })
        assertEquals(0, dao.countFavoritesSearch(ArabicText.buildMatchQuery("العصر")!!).first())
        dao.deleteFavorite(2)
        assertEquals(0, dao.countFavorites().first())
    }

    @Test
    fun narratorAndBookCounts() = runBlocking {
        val narrators = dao.getNarratorsWithCount().first()
        assertEquals(17, narrators.first().narrator.id) // مالك في روايتين
        assertEquals(2, narrators.first().hadithCount)
        val books = dao.getBooksWithCount().first()
        assertEquals(2, books.single().hadithCount)
    }
}
