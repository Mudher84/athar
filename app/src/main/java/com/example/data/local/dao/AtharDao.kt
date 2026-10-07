package com.example.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entity.BookEntity
import com.example.data.local.entity.FavoriteHadithEntity
import com.example.data.local.entity.HadithEntity
import com.example.data.local.entity.HadithNarratorChainEntity
import com.example.data.local.entity.NarratorEntity
import com.example.domain.model.BookWithCount
import com.example.domain.model.HadithListItem
import com.example.domain.model.NarratorWithCount
import com.example.domain.model.RawChainLink
import kotlinx.coroutines.flow.Flow

@Dao
interface AtharDao {

    // ---------------------------------------------------------------- المرويات

    /** صفحات المرويات مع الفلاتر؛ كل وسيط null يعني «الكل». */
    @Query(
        SELECT_ITEM + """
        WHERE (:bookId IS NULL OR h.book_id = :bookId)
          AND (:region IS NULL OR h.region_tag = :region)
          AND (:narratorId IS NULL OR h.id IN
                (SELECT hadith_id FROM hadith_narrator_chain WHERE narrator_id = :narratorId))
          AND (:connectedOnly = 0 OR h.is_mursal_or_balagh = 0)
        ORDER BY h.id ASC
        """
    )
    fun pageHadiths(
        bookId: Int?,
        region: String?,
        narratorId: Int?,
        connectedOnly: Boolean
    ): PagingSource<Int, HadithListItem>

    /**
     * البحث النصي الكامل في المتن والسند معاً (FTS4).
     * [match] تعبير MATCH مبني من نص موحّد بلا تشكيل، مثل: `مالك* نافع*`.
     */
    @Query(
        SELECT_ITEM + """
        WHERE h.id IN (SELECT rowid FROM hadith_fts WHERE hadith_fts MATCH :match)
          AND (:bookId IS NULL OR h.book_id = :bookId)
          AND (:region IS NULL OR h.region_tag = :region)
          AND (:narratorId IS NULL OR h.id IN
                (SELECT hadith_id FROM hadith_narrator_chain WHERE narrator_id = :narratorId))
          AND (:connectedOnly = 0 OR h.is_mursal_or_balagh = 0)
        ORDER BY h.id ASC
        """
    )
    fun searchHadiths(
        match: String,
        bookId: Int?,
        region: String?,
        narratorId: Int?,
        connectedOnly: Boolean
    ): PagingSource<Int, HadithListItem>

    @Query(
        SELECT_ITEM + """
        INNER JOIN favorite_hadiths fav ON fav.hadith_id = h.id
        ORDER BY fav.created_at DESC
        """
    )
    fun pageFavorites(): PagingSource<Int, HadithListItem>

    @Query(SELECT_ITEM + " WHERE h.id = :hadithId")
    fun getHadithItem(hadithId: Int): Flow<HadithListItem?>

    @Query("SELECT COUNT(*) FROM hadiths")
    fun countHadiths(): Flow<Int>

    // ---------------------------------------------------------------- السند

    @Query(
        """
        SELECT
            c.hadith_id AS hadithId,
            c.chain_order AS chainOrder,
            n.id AS narratorId,
            n.name AS name,
            n.popular_name AS popularName,
            n.region AS region,
            n.era AS era,
            n.is_trusted AS isTrusted,
            n.sect_affiliation AS sectAffiliation,
            n.notes AS notes
        FROM hadith_narrator_chain c
        INNER JOIN narrators n ON c.narrator_id = n.id
        WHERE c.hadith_id = :hadithId
        ORDER BY c.chain_order ASC
        """
    )
    fun getChainLinksForHadith(hadithId: Int): Flow<List<RawChainLink>>

    // ---------------------------------------------------------------- الرواة والكتب

    @Query(
        """
        SELECT n.*, (SELECT COUNT(*) FROM hadith_narrator_chain c WHERE c.narrator_id = n.id) AS hadithCount
        FROM narrators n
        ORDER BY hadithCount DESC, n.id ASC
        """
    )
    fun getNarratorsWithCount(): Flow<List<NarratorWithCount>>

    @Query("SELECT * FROM narrators ORDER BY id ASC")
    fun getAllNarrators(): Flow<List<NarratorEntity>>

    @Query("SELECT * FROM narrators WHERE id = :id LIMIT 1")
    fun getNarratorById(id: Int): Flow<NarratorEntity?>

    @Query(
        """
        SELECT b.*, (SELECT COUNT(*) FROM hadiths h WHERE h.book_id = b.id) AS hadithCount
        FROM books b
        ORDER BY b.id ASC
        """
    )
    fun getBooksWithCount(): Flow<List<BookWithCount>>

    @Query("SELECT * FROM books ORDER BY id ASC")
    fun getAllBooks(): Flow<List<BookEntity>>

    // ---------------------------------------------------------------- المحفوظات

    @Query("SELECT hadith_id FROM favorite_hadiths")
    fun getFavoriteHadithIds(): Flow<List<Int>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favorite: FavoriteHadithEntity)

    @Query("DELETE FROM favorite_hadiths WHERE hadith_id = :hadithId")
    suspend fun deleteFavorite(hadithId: Int)

    // ---------------------------------------------------------------- للاختبارات

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBook(book: BookEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNarrator(narrator: NarratorEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHadith(hadith: HadithEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChainLink(link: HadithNarratorChainEntity)

    companion object {
        /**
         * عنصر القائمة كاملاً في استعلام واحد: عنوان الكتاب، وسلسلة الرواة مرتبة
         * («id|الاسم» مفصولة بـ «¦»)، وحالة الحفظ.
         */
        const val SELECT_ITEM = """
        SELECT
            h.id AS id,
            h.book_id AS bookId,
            b.title AS bookTitle,
            h.chapter AS chapter,
            h.path_badge AS pathBadge,
            h.region_tag AS regionTag,
            h.raw_sanad AS rawSanad,
            h.matn AS matn,
            h.is_mursal_or_balagh AS isMursalOrBalagh,
            h.transmission_note AS transmissionNote,
            h.source_number AS sourceNumber,
            (SELECT group_concat(link, '¦') FROM (
                SELECT n.id || '|' || n.name AS link
                FROM hadith_narrator_chain c
                INNER JOIN narrators n ON n.id = c.narrator_id
                WHERE c.hadith_id = h.id
                ORDER BY c.chain_order ASC
            )) AS chainPacked,
            EXISTS(SELECT 1 FROM favorite_hadiths f WHERE f.hadith_id = h.id) AS isFavorite
        FROM hadiths h
        INNER JOIN books b ON b.id = h.book_id
        """
    }
}
