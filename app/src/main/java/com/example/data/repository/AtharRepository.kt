package com.example.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.data.local.dao.AtharDao
import com.example.data.local.entity.FavoriteHadithEntity
import com.example.domain.model.BookWithCount
import com.example.domain.model.HadithFilters
import com.example.domain.model.HadithListItem
import com.example.domain.model.NarratorWithCount
import com.example.util.ArabicText
import kotlinx.coroutines.flow.Flow

/** مصدر البيانات الوحيد للواجهة؛ كل شيء من القاعدة المحلية دون شبكة. */
interface AtharRepository {
    fun hadiths(query: String, filters: HadithFilters): Flow<PagingData<HadithListItem>>
    fun countHadiths(query: String, filters: HadithFilters): Flow<Int>
    fun favorites(query: String): Flow<PagingData<HadithListItem>>
    fun countFavorites(query: String): Flow<Int>
    fun books(): Flow<List<BookWithCount>>
    fun narrators(): Flow<List<NarratorWithCount>>
    suspend fun setFavorite(hadithId: Int, favorite: Boolean)
}

class AtharRepositoryImpl(private val dao: AtharDao) : AtharRepository {

    private val pagingConfig = PagingConfig(
        pageSize = PAGE_SIZE,
        prefetchDistance = PAGE_SIZE / 2,
        enablePlaceholders = false,
        initialLoadSize = PAGE_SIZE * 2
    )

    override fun hadiths(query: String, filters: HadithFilters): Flow<PagingData<HadithListItem>> {
        val match = ArabicText.buildMatchQuery(query)
        return Pager(pagingConfig) {
            if (match == null) {
                dao.pageHadiths(filters.bookId, filters.region?.tag, filters.narratorId, filters.connectedOnly)
            } else {
                dao.searchHadiths(match, filters.bookId, filters.region?.tag, filters.narratorId, filters.connectedOnly)
            }
        }.flow
    }

    override fun countHadiths(query: String, filters: HadithFilters): Flow<Int> {
        val match = ArabicText.buildMatchQuery(query)
        return if (match == null) {
            dao.countHadiths(filters.bookId, filters.region?.tag, filters.narratorId, filters.connectedOnly)
        } else {
            dao.countSearch(match, filters.bookId, filters.region?.tag, filters.narratorId, filters.connectedOnly)
        }
    }

    override fun favorites(query: String): Flow<PagingData<HadithListItem>> {
        val match = ArabicText.buildMatchQuery(query)
        return Pager(pagingConfig) {
            if (match == null) dao.pageFavorites() else dao.searchFavorites(match)
        }.flow
    }

    override fun countFavorites(query: String): Flow<Int> {
        val match = ArabicText.buildMatchQuery(query)
        return if (match == null) dao.countFavorites() else dao.countFavoritesSearch(match)
    }

    override fun books(): Flow<List<BookWithCount>> = dao.getBooksWithCount()

    override fun narrators(): Flow<List<NarratorWithCount>> = dao.getNarratorsWithCount()

    override suspend fun setFavorite(hadithId: Int, favorite: Boolean) {
        if (favorite) dao.insertFavorite(FavoriteHadithEntity(hadithId)) else dao.deleteFavorite(hadithId)
    }

    private companion object {
        const val PAGE_SIZE = 20
    }
}
