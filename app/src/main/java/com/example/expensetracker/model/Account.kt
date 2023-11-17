package com.example.expensetracker.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "ACCOUNTLIST_V1",
    indices = [
        Index(value = ["accountType"])
    ]
)
data class Account(
    @PrimaryKey(autoGenerate = true)
    val accountId: Int = 0,
    val accountName: String,
    val accountType: String,
    val accountNum: String?,
    val status: String,
    val notes: String?,
    val heldAt: String?,
    val website: String?,
    val contactInfo: String?,
    val accessInfo: String?,
    val initialBalance: Double?,
    val initialDate: String?,
    val favoriteAccount: String,
    val currencyId: Int,
    val statementLocked: Int?,
    val statementDate: String?,
    val minimumBalance: Double?,
    val creditLimit: Double?,
    val interestRate: Double?,
    val paymentDueDate: String?,
    val minimumPayment: Double?
)