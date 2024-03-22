package com.example.expensetracker.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "BILLSDEPOSITS_V1")
data class BillsDeposits(
    @PrimaryKey(autoGenerate = true)
    val BDID: Int,
    val ACCOUNTID: Int,
    val TOACCOUNTID: Int?,
    val PAYEEID: Int,
    val TRANSCODE: String, // Withdrawal, Deposit, Transfer
    val TRANSAMOUNT: Double,
    val STATUS: String?, // None, Reconciled, Void, Follow up, Duplicate
    val TRANSACTIONNUMBER: String?,
    val NOTES: String?,
    val CATEGID: Int?,
    val TRANSDATE: String?,
    val FOLLOWUPID: Int?,
    val TOTRANSAMOUNT: Double?,
    val REPEATS: Int?,
    val NEXTOCCURRENCEDATE: String?,
    val NUMOCCURRENCES: Int?,
    val COLOR: Int = -1
)
