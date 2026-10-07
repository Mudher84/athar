package com.example.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Fts4
import androidx.room.PrimaryKey

/**
 * فهرس البحث النصي الكامل. rowid = hadiths.id.
 * النصوص مخزّنة موحّدة: بلا تشكيل، والألفات والتاء المربوطة والألف المقصورة موحّدة،
 * وعلامات الترقيم مستبدلة بمسافات (مُقطّع FTS4 الافتراضي لا يفصل «،» العربية).
 * يُملأ مسبقاً في assets/databases/athar.db ولا يُكتب إليه وقت التشغيل.
 */
@Fts4
@Entity(tableName = "hadith_fts")
data class HadithFtsEntity(
    @PrimaryKey
    @ColumnInfo(name = "rowid")
    val rowId: Int,

    @ColumnInfo(name = "matn")
    val matn: String,

    @ColumnInfo(name = "sanad")
    val sanad: String
)
