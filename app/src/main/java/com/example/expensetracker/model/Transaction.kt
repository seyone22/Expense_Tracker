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

enum class TransactionStatus(val displayName: String) {
    R("Reconciled"),
    V("Void"),
    F("Follow Up"),
    D("Duplicate"),
    U("Unreconciled")
}

enum class TransactionCode(val displayName: String) {
    WITHDRAWAL("Withdrawal"),
    DEPOSIT("Deposit"),
    TRANSFER("Transfer")
}

data class TransactionWithDetails(
    val transId: Int,
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
    val color: Int,
    val payeeName: String,  // Include payeeName from PAYEE_V1
    val categName: String  // Include categoryName from CATEGORY_V1
)
