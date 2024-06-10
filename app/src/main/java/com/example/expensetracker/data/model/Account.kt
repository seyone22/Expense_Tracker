package com.example.expensetracker.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "ACCOUNTLIST_V1",
    indices = [
        Index(value = ["accountType"])
    ]
)
data class Account(
    @PrimaryKey(autoGenerate = true)
    val accountId: Int = 0,
    val accountName: String = "",
    val accountType: String = "",
    val accountNum: String? = "",
    val status: String = "",
    val notes: String? = "",
    val heldAt: String? = "",
    val website: String? = "",
    val contactInfo: String? = "",
    val accessInfo: String? = "",
    val initialBalance: Double? = 0.0,
    val initialDate: String? = "",
    val favoriteAccount: String = "",
    val currencyId: Int = 0,
    val statementLocked: Int? = 0,
    val statementDate: String? = "",
    val minimumBalance: Double? = 0.0,
    val creditLimit: Double? = 0.0,
    val interestRate: Double? = 0.0,
    val paymentDueDate: String? = "",
    val minimumPayment: Double? = 0.0
)

enum class AccountTypes(val displayName: String) {
    CASH("Cash"),
    CHECKING("Checking"),
    CREDIT_CARD("Credit Card"),
    LOAN("Loan"),
    TERM("Term"),
    INVESTMENT("Investment"),
    ASSET("Asset"),
    SHARES("Shares")
}