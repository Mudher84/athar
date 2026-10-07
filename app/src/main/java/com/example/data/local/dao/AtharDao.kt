package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.data.local.entity.BookEntity
import com.example.data.local.entity.FavoriteHadithEntity
import com.example.data.local.entity.HadithEntity
import com.example.data.local.entity.HadithNarratorChainEntity
import com.example.data.local.entity.NarratorEntity
import com.example.domain.model.RawChainLink
import kotlinx.coroutines.flow.Flow

@Dao
interface AtharDao {

    @Query("SELECT * FROM books ORDER BY id ASC")
    fun getAllBooks(): Flow<List<BookEntity>>

    @Query("SELECT * FROM narrators ORDER BY region ASC, id ASC")
    fun getAllNarrators(): Flow<List<NarratorEntity>>

    @Query("SELECT * FROM narrators WHERE region LIKE '%' || :region || '%' ORDER BY id ASC")
    fun getNarratorsByRegion(region: String): Flow<List<NarratorEntity>>

    @Query("SELECT * FROM narrators WHERE id = :id LIMIT 1")
    fun getNarratorById(id: Int): Flow<NarratorEntity?>

    @Query("SELECT * FROM hadiths ORDER BY id ASC")
    fun getAllHadiths(): Flow<List<HadithEntity>>

    @Query("SELECT * FROM hadiths WHERE book_id = :bookId ORDER BY id ASC")
    fun getHadithsByBook(bookId: Int): Flow<List<HadithEntity>>

    @Query("SELECT * FROM hadiths WHERE chapter = :chapter ORDER BY id ASC")
    fun getHadithsByChapter(chapter: String): Flow<List<HadithEntity>>

    @Query("""
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
        ORDER BY c.hadith_id ASC, c.chain_order ASC
    """)
    fun getAllRawChainLinks(): Flow<List<RawChainLink>>

    @Query("""
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
    """)
    fun getChainLinksForHadith(hadithId: Int): Flow<List<RawChainLink>>

    @Query("SELECT hadith_id FROM favorite_hadiths")
    fun getFavoriteHadithIds(): Flow<List<Int>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favorite: FavoriteHadithEntity)

    @Query("DELETE FROM favorite_hadiths WHERE hadith_id = :hadithId")
    suspend fun deleteFavorite(hadithId: Int)

    // Insertion queries for tests & mutations
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBook(book: BookEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNarrator(narrator: NarratorEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHadith(hadith: HadithEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChainLink(link: HadithNarratorChainEntity)
}
