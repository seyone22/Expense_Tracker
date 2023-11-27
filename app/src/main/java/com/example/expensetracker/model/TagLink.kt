package com.example.expensetracker.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "TAGLINK_V1",
    indices = [Index(value = ["REFTYPE", "REFID", "TAGID"], unique = true, name = "IDX_TAGLINK")],
    foreignKeys = [
        ForeignKey(
            entity = Tag::class,
            parentColumns = ["TAGID"],
            childColumns = ["TAGID"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class TagLink(
    @PrimaryKey
    @ColumnInfo(name = "TAGLINKID")
    val tagLinkId: Long,

    @ColumnInfo(name = "REFTYPE")
    val refType: String,

    @ColumnInfo(name = "REFID")
    val refId: Long,

    @ColumnInfo(name = "TAGID")
    val tagId: Long
)
