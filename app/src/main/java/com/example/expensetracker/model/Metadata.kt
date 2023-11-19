package com.example.expensetracker.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "INFOTABLE_V1")
data class Metadata (
    @PrimaryKey(autoGenerate = true)
    val infoId : Int = 1,
    val infoName : String,
    val infoValue : String
)