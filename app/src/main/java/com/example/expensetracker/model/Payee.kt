package com.example.expensetracker.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "PAYEE_V1",
    indices = [
        Index(value = ["payeeName"])
    ]
)
data class Payee (
    @PrimaryKey(autoGenerate = true)
    val payeeId : Int = 0,
    val payeeName : String,
    val categId : Int,
    val number : Int,
    val website : String,
    val notes : String,
    val active : Int
)