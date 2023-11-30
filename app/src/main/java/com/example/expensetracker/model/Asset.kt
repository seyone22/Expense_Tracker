package com.example.expensetracker.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "ASSETS_V1",
    indices = [
        Index(value = ["ASSETTYPE"])
    ]
)
data class Asset(
    @PrimaryKey(autoGenerate = true)
    val ASSETID: Int = 0,
    val STARTDATE: String = "",
    val ASSETNAME: String = "",
    val ASSETSTATUS: String? = "",
    val CURRENCYID: Int = -1,
    val VALUECHANGEMODE: String = "",
    val VALUE: Double = 0.0,
    val VALUECHANGE: String = "",
    val NOTES: String = "",
    val VALUECHANGERATE: Double = 0.0,
    val ASSETTYPE: String = "",
)