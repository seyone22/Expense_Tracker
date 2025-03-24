package com.seyone22.expensetracker.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "CHECKINGACCOUNT_V1",
    indices = [
        Index(value = ["accountId", "toAccountId"]),
        Index(value = ["transDate"])
    ]
)
data class Transaction(
    @PrimaryKey(autoGenerate = true)
    val transId: Int = 0,
    val accountId: Int = 0,
    val toAccountId: Int? = 0,
    val payeeId: Int = 0,
    val transCode: String = "",
    val transAmount: Double = 0.0,
    val status: String? = "",
    val transactionNumber: String? = "",
    val notes: String? = "",
    val categoryId: Int? = 0,
    val transDate: String? = "",
    val lastUpdatedTime: String? = "",
    val deletedTime: String? = "",
    val followUpId: Int? = 0,
    val toTransAmount: Double? = 0.0,
    val color: Int = -1
)

enum class TransactionStatus(val displayName: String) {
    U("Unreconciled"),
    R("Reconciled"),
    V("Void"),
    F("Follow Up"),
    D("Duplicate"),
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
    val payeeName: String?,  // Include payeeName from PAYEE_V1, NULL when Transfer
    val categName: String  // Include categoryName from CATEGORY_V1
)

fun TransactionWithDetails.toTransaction(): Transaction {
    return Transaction(
        transId = transId,
        accountId = accountId,
        toAccountId = toAccountId,
        payeeId = payeeId,
        transCode = transCode,
        transAmount = transAmount,
        status = status,
        transactionNumber = transactionNumber,
        notes = notes,
        categoryId = categoryId,
        transDate = transDate,
        lastUpdatedTime = lastUpdatedTime,
        deletedTime = deletedTime,
        followUpId = followUpId,
        toTransAmount = toTransAmount,
        color = color
    )
}