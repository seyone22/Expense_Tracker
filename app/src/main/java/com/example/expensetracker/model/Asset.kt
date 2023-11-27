package com.example.expensetracker.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "ASSETS_V1",
    indices = [
        Index(value = ["assetType"])
    ]
)
data class Asset(
    @PrimaryKey(autoGenerate = true)
    val assetId: Int = 0,
    val startDate: String = "",
    val assetName: String = "",
    val assetStatus: String = "",
    val currencyId: Int = -1,
    val valueChangeMode: String = "",
    val value: Double = 0.0,
    val valueChange: String = "",
    val notes: String = "",
    val valueChangeRate: Double = 0.0,
    val assetType: String = "",
)