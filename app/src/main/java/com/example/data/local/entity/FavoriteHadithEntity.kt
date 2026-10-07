package com.example.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "favorite_hadiths",
    foreignKeys = [
        ForeignKey(
            entity = HadithEntity::class,
            parentColumns = ["id"],
            childColumns = ["hadith_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class FavoriteHadithEntity(
    @PrimaryKey
    @ColumnInfo(name = "hadith_id")
    val hadithId: Int,

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
)
