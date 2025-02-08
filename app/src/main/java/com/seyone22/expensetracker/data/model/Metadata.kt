package com.seyone22.expensetracker.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "INFOTABLE_V1",
    indices = [
        Index(value = ["infoName"]),
    ]
)
data class Metadata(
    @PrimaryKey(autoGenerate = true)
    val infoId: Int = 1,
    val infoName: String,
    val infoValue: String
)