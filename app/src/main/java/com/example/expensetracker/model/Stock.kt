package com.example.expensetracker.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "STOCK_V1",
    indices = [Index(value = ["HELDAT"], name = "IDX_STOCK_HELDAT")]
)
data class Stock(
    @PrimaryKey
    @ColumnInfo(name = "STOCKID")
    val stockId: Long,

    @ColumnInfo(name = "HELDAT")
    val heldAt: Long,

    @ColumnInfo(name = "PURCHASEDATE")
    val purchaseDate: String,

    @ColumnInfo(name = "STOCKNAME")
    val stockName: String,

    @ColumnInfo(name = "SYMBOL")
    val symbol: String?,

    @ColumnInfo(name = "NUMSHARES")
    val numShares: Double?,

    @ColumnInfo(name = "PURCHASEPRICE")
    val purchasePrice: Double,

    @ColumnInfo(name = "NOTES")
    val notes: String?,

    @ColumnInfo(name = "CURRENTPRICE")
    val currentPrice: Double,

    @ColumnInfo(name = "VALUE")
    val value: Double?,

    @ColumnInfo(name = "COMMISSION")
    val commission: Double?
)
