package com.example.domain.model

import com.example.data.local.entity.BookEntity
import com.example.data.local.entity.HadithEntity

data class NarratorInChain(
    val narratorId: Int,
    val name: String,
    val popularName: String?,
    val region: String,
    val era: String,
    val isTrusted: Boolean,
    val sectAffiliation: String?,
    val notes: String?,
    val chainOrder: Int
)

data class HadithDetail(
    val hadith: HadithEntity,
    val book: BookEntity,
    val chain: List<NarratorInChain>,
    val isFavorite: Boolean = false
)

data class RawChainLink(
    val hadithId: Int,
    val chainOrder: Int,
    val narratorId: Int,
    val name: String,
    val popularName: String?,
    val region: String,
    val era: String,
    val isTrusted: Boolean,
    val sectAffiliation: String?,
    val notes: String?
)
