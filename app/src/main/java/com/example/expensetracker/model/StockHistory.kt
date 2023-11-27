package com.example.expensetracker.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/*    uniqueIndices = [UniqueConstraint(
        name = "UNIQUE_STOCKHISTORY_SYMBOL_DATE",
        columnNames = ["SYMBOL", "DATE"]
    ])*/

@Entity(
    tableName = "STOCKHISTORY_V1",
    indices = [Index(value = ["SYMBOL"], name = "IDX_STOCKHISTORY_SYMBOL")],
)
data class StockHistory(
    @PrimaryKey
    @ColumnInfo(name = "HISTID")
    val histId: Long,

    @ColumnInfo(name = "SYMBOL")
    val symbol: String,

    @ColumnInfo(name = "DATE")
    val date: String,

    @ColumnInfo(name = "VALUE")
    val value: Double,

    @ColumnInfo(name = "UPDTYPE")
    val updType: Int?
)
