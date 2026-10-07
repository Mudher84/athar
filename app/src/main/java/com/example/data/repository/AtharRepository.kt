package com.example.data.repository

import com.example.data.local.dao.AtharDao
import com.example.data.local.entity.BookEntity
import com.example.data.local.entity.FavoriteHadithEntity
import com.example.data.local.entity.NarratorEntity
import com.example.domain.model.HadithDetail
import com.example.domain.model.NarratorInChain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

interface AtharRepository {
    fun getAllHadithDetails(): Flow<List<HadithDetail>>
    fun getAllNarrators(): Flow<List<NarratorEntity>>
    fun getAllBooks(): Flow<List<BookEntity>>
    suspend fun toggleFavorite(hadithId: Int, currentFav: Boolean)
}

class AtharRepositoryImpl(
    private val dao: AtharDao
) : AtharRepository {

    override fun getAllHadithDetails(): Flow<List<HadithDetail>> {
        return combine(
            dao.getAllHadiths(),
            dao.getAllBooks(),
            dao.getAllRawChainLinks(),
            dao.getFavoriteHadithIds()
        ) { hadiths, books, chainLinks, favIds ->
            val bookMap = books.associateBy { it.id }
            val favSet = favIds.toSet()

            // Group chain links by hadithId and order by chainOrder
            val chainsByHadith = chainLinks.groupBy { it.hadithId }.mapValues { entry ->
                entry.value.sortedBy { it.chainOrder }.map { raw ->
                    NarratorInChain(
                        narratorId = raw.narratorId,
                        name = raw.name,
                        popularName = raw.popularName,
                        region = raw.region,
                        era = raw.era,
                        isTrusted = raw.isTrusted,
                        sectAffiliation = raw.sectAffiliation,
                        notes = raw.notes,
                        chainOrder = raw.chainOrder
                    )
                }
            }

            hadiths.map { hadith ->
                val book = bookMap[hadith.bookId] ?: BookEntity(
                    id = hadith.bookId,
                    title = "مصدر غير معروف",
                    author = "غير معروف",
                    era = "الحقبة الأموية"
                )
                val chain = chainsByHadith[hadith.id] ?: emptyList()
                val isFav = favSet.contains(hadith.id)

                HadithDetail(
                    hadith = hadith,
                    book = book,
                    chain = chain,
                    isFavorite = isFav
                )
            }
        }
    }

    override fun getAllNarrators(): Flow<List<NarratorEntity>> = dao.getAllNarrators()

    override fun getAllBooks(): Flow<List<BookEntity>> = dao.getAllBooks()

    override suspend fun toggleFavorite(hadithId: Int, currentFav: Boolean) {
        if (currentFav) {
            dao.deleteFavorite(hadithId)
        } else {
            dao.insertFavorite(FavoriteHadithEntity(hadithId = hadithId))
        }
    }

    companion object {
        fun normalizeArabic(input: String): String {
            if (input.isEmpty()) return ""
            var result = input
            // Strip tashkeel
            val tashkeelRegex = Regex("[\\u0617-\\u061A\\u064B-\\u0652]")
            result = tashkeelRegex.replace(result, "")
            // Normalize Alefs
            result = result.replace(Regex("[إأآ]"), "ا")
            // Normalize Taa Marbuta
            result = result.replace("ة", "ه")
            // Normalize Alif Maksura
            result = result.replace("ى", "ي")
            return result
        }
    }
}
