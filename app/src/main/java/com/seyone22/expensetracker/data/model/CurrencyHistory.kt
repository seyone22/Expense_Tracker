package com.seyone22.expensetracker.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "CURRENCYHISTORY_V1")
data class CurrencyHistory(
    @PrimaryKey(autoGenerate = true)
    val currHistId: Int = 0, //make this into an autonumber of some sort.
    val currencyId: Int,
    val currDate: String,
    val currValue: Double,
    val currUpdType: Int
)