package com.example.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "hadith_narrator_chain",
    primaryKeys = ["hadith_id", "chain_order"],
    foreignKeys = [
        ForeignKey(
            entity = HadithEntity::class,
            parentColumns = ["id"],
            childColumns = ["hadith_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = NarratorEntity::class,
            parentColumns = ["id"],
            childColumns = ["narrator_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["hadith_id"], name = "idx_chain_hadith"),
        Index(value = ["narrator_id"], name = "idx_chain_narrator")
    ]
)
data class HadithNarratorChainEntity(
    @ColumnInfo(name = "hadith_id")
    val hadithId: Int,

    @ColumnInfo(name = "narrator_id")
    val narratorId: Int,

    @ColumnInfo(name = "chain_order")
    val chainOrder: Int
)
