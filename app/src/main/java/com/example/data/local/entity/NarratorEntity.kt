package com.example.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "narrators",
    indices = [
        Index(value = ["region"], name = "idx_narrator_region")
    ]
)
data class NarratorEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 0,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "popular_name")
    val popularName: String? = null,

    @ColumnInfo(name = "region")
    val region: String,

    @ColumnInfo(name = "era")
    val era: String,

    @ColumnInfo(name = "is_trusted", defaultValue = "1")
    val isTrusted: Boolean = true,

    @ColumnInfo(name = "sect_affiliation", defaultValue = "'سني-أهل المدينة'")
    val sectAffiliation: String? = "سني-أهل المدينة",

    @ColumnInfo(name = "notes")
    val notes: String? = null
)
