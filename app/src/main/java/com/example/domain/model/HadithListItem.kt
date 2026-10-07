package com.example.domain.model

import androidx.compose.runtime.Immutable
import androidx.room.Embedded
import com.example.data.local.entity.BookEntity
import com.example.data.local.entity.NarratorEntity

/**
 * رواية كما تُعرض في القوائم، محمّلة باستعلام واحد (AtharDao.SELECT_ITEM).
 * @Immutable حتى لا تُعاد تركيب عناصر LazyColumn ما لم تتغير بياناتها فعلاً.
 */
@Immutable
data class HadithListItem(
    val id: Int,
    val bookId: Int,
    val bookTitle: String,
    val chapter: String,
    val pathBadge: String,
    val regionTag: String,
    val rawSanad: String,
    val matn: String,
    val isMursalOrBalagh: Boolean,
    val transmissionNote: String?,
    val sourceNumber: Int?,
    /** «id|الاسم» مفصولة بـ «¦» بترتيب السند */
    val chainPacked: String?,
    val isFavorite: Boolean
) {
    /** رواة المسار بترتيب السند */
    val chain: List<ChainNode>
        get() = chainPacked.orEmpty()
            .split('¦')
            .mapNotNull { part ->
                val sep = part.indexOf('|')
                if (sep <= 0) null
                else part.substring(0, sep).toIntOrNull()?.let { ChainNode(it, part.substring(sep + 1)) }
            }
}

@Immutable
data class ChainNode(val narratorId: Int, val name: String)

data class NarratorWithCount(
    @Embedded val narrator: NarratorEntity,
    val hadithCount: Int
)

data class BookWithCount(
    @Embedded val book: BookEntity,
    val hadithCount: Int
)
