package com.example.expensetracker.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "BILLSDEPOSITS_V1",
    indices = [
        Index(value = ["accountId", "toAccountId"], name = "IDX_BILLSDEPOSITS_ACCOUNT")
    ]
)
data class BillsDeposit(
    @PrimaryKey(autoGenerate = true)
    val bdid: Long = 0,
    val accountId: Long,
    val toAccountId: Long?,
    val payeeId: Long,
    val transCode: String,
    val transAmount: Double,
    val status: String?,
    val transactionNumber: String?,
    val notes: String?,
    val categoryId: Long?,
    val transDate: String?,
    val followUpId: Long?,
    val toTransAmount: Double?,
    val repeats: Int?,
    val nextOccurrenceDate: String?,
    val numOccurrences: Int?,
    val color: Int = -1
)