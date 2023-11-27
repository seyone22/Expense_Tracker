package com.example.expensetracker.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "TAG_V1",
    indices = [Index(value = ["TAGNAME"], name = "IDX_TAGNAME")]
)
data class Tag(
    @PrimaryKey
    @ColumnInfo(name = "TAGID")
    val tagId: Long,

    @ColumnInfo(name = "TAGNAME")
    val tagName: String,

    @ColumnInfo(name = "ACTIVE")
    val active: Int
)
