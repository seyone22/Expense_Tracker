package com.example.expensetracker.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "CURRENCYFORMATS_V1")
data class CurrencyFormat (
    @PrimaryKey(autoGenerate = true)
    val currencyId : Int = 0,
    val currencyName : String,
    val pfx_symbol : String,
    val sfx_symbol : String,
    val decimal_point : String,
    val group_seperator : String,
    val unit_name : String,
    val cent_name : String,
    val scale : Int,
    val baseConvRate : Double,
    val currency_symbol : String,
    val currency_type : String
)