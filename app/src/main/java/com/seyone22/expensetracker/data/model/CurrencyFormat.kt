package com.seyone22.expensetracker.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "CURRENCYFORMATS_V1",
    indices = [
        Index(value = ["currency_symbol"])
    ]
)
data class CurrencyFormat(
    @PrimaryKey(autoGenerate = true)
    val currencyId: Int = 0,
    var currencyName: String = "",
    val pfx_symbol: String = "",
    val sfx_symbol: String = "",
    val decimal_point: String = "",
    val group_seperator: String = "",
    val unit_name: String = "",
    val cent_name: String = "",
    val scale: Int = 0,
    val baseConvRate: Double = 0.0,
    val currency_symbol: String = "",
    val currency_type: String = ""
)