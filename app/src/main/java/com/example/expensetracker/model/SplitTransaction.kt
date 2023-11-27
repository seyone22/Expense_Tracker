package com.example.expensetracker.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "SPLITTRANSACTIONS_V1",
    indices = [Index(value = ["TRANSID"], name = "IDX_SPLITTRANSACTIONS_TRANSID")]
)
data class SplitTransaction(
    @PrimaryKey
    @ColumnInfo(name = "SPLITTRANSID")
    val splitTransId: Long,

    @ColumnInfo(name = "TRANSID")
    val transId: Long,

    @ColumnInfo(name = "CATEGID")
    val categoryId: Long?,

    @ColumnInfo(name = "SPLITTRANSAMOUNT")
    val splitTransAmount: Double?,

    @ColumnInfo(name = "NOTES")
    val notes: String?
)
