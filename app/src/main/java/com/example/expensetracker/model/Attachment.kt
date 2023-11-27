package com.example.expensetracker.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "ATTACHMENT_V1",
    indices = [
        Index(value = ["refType","refId"])
    ]
)
data class Attachment(
    @PrimaryKey(autoGenerate = true)
    val attachmentId: Int = 0,
    val refType: String = "",
    val refId : Int = -1,
    val description : String = "",
    val fileName : String = ""
)