package com.example.expensetracker.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "TAGS_V1")
data class Tag(
    @PrimaryKey(autoGenerate = true)
    val tagId: Int = 0,
    val tagName: String,
    val active: Boolean?
)
