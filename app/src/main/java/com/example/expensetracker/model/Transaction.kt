package com.example.expensetracker.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "CHECKINGACCOUNT_V1",
    indices = [
        Index(value = ["accountId", "toAccountId"]),
        Index(value = ["transDate"])
    ]
)
data class Transaction(
    @PrimaryKey(autoGenerate = true)
    val transId: Int = 0,
    val accountId: Int,
    val toAccountId: Int?,
    val payeeId: Int,
    val transCode: String,
    val transAmount: Double,
    val status: String?,
    val transactionNumber: String?,
    val notes: String?,
    val categoryId: Int?,
    val transDate: String?,
    val lastUpdatedTime: String?,
    val deletedTime: String?,
    val followUpId: Int?,
    val toTransAmount: Double?,
    val color: Int = -1
)