package com.example.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "hadiths",
    foreignKeys = [
        ForeignKey(
            entity = BookEntity::class,
            parentColumns = ["id"],
            childColumns = ["book_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["book_id"]),
        Index(value = ["path_badge"]),
        Index(value = ["chapter"]),
        Index(value = ["region_tag"])
    ]
)
data class HadithEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 0,

    @ColumnInfo(name = "book_id")
    val bookId: Int,

    @ColumnInfo(name = "chapter")
    val chapter: String,

    @ColumnInfo(name = "path_badge")
    val pathBadge: String,

    /** «المدينة» أو «الشام» أو «مشترك» */
    @ColumnInfo(name = "region_tag", defaultValue = "'المدينة'")
    val regionTag: String = "المدينة",

    @ColumnInfo(name = "raw_sanad")
    val rawSanad: String,

    @ColumnInfo(name = "matn")
    val matn: String,

    /** كشفٌ آلي للمراسيل والبلاغات حتى لا تُعرض كرواية متصلة */
    @ColumnInfo(name = "is_mursal_or_balagh", defaultValue = "0")
    val isMursalOrBalagh: Boolean = false,

    /** «مرسل» أو «بلاغ» أو null */
    @ColumnInfo(name = "transmission_note")
    val transmissionNote: String? = null,

    /** رقم الرواية في نسخة المصدر (AhmedBaset/hadith-json) للتوثيق */
    @ColumnInfo(name = "source_number")
    val sourceNumber: Int? = null
)
