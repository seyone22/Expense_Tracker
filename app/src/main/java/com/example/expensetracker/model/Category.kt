package com.example.expensetracker.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "CATEGORY_V1",
    indices = [
        Index(value = ["categId", "parentId"], unique = true),
        Index(value = ["categName"])
    ]
)
data class Category(
    @PrimaryKey(autoGenerate = true)
    val categId : Int = 0,
    val categName : String = "",
    val active : Int = 0,
    val parentId : Int = 0
)