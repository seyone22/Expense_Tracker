package com.seyone22.expensetracker.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "TAGLINK_V1",
    foreignKeys = [
        androidx.room.ForeignKey(
            entity = Tag::class,
            parentColumns = ["tagId"],
            childColumns = ["tagId"],
            onDelete = androidx.room.ForeignKey.NO_ACTION
        )
    ],
    indices = [androidx.room.Index(value = ["refType", "refId", "tagId"], unique = true, name = "IDX_TAGLINK")]
)
data class TagLink(
    @PrimaryKey(autoGenerate = true)
    val tagLinkId: Int = 0,
    val refType: String,
    val refId: Int,
    val tagId: Int
)