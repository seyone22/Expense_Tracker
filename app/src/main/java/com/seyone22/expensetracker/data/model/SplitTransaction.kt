package com.seyone22.expensetracker.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "SPLITTRANSACTIONS_V1",
    indices = [
        Index(value = ["TRANSID"], name = "IDX_SPLITTRANSACTIONS_TRANSID")
    ]
)
data class SplitTransaction(
    @PrimaryKey(autoGenerate = true)
    val SPLITTRANSID: Int = 0,
    val TRANSID: Int,
    val CATEGID: Int?,
    val SPLITTRANSAMOUNT: Double?,
    val NOTES: String? = ""
)
