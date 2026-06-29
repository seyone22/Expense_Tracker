package com.seyone22.expensetracker.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "ATTACHMENT_V1",
    indices = [
        Index(value = ["REFTYPE", "REFID"], name = "IDX_ATTACHMENT_REF")
    ]
)
data class Attachment(
    @PrimaryKey(autoGenerate = true)
    val ATTACHMENTID: Int = 0,
    val REFTYPE: String, // e.g. "Transaction"
    val REFID: Int,
    val DESCRIPTION: String? = "",
    val FILENAME: String // Local absolute or relative path
)
