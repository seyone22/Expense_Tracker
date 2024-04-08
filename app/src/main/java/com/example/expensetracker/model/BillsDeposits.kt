package com.example.expensetracker.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "BILLSDEPOSITS_V1")
data class BillsDeposits(
    @PrimaryKey(autoGenerate = true)
    val BDID: Int = 0,
    val ACCOUNTID: Int = 0,
    val TOACCOUNTID: Int? = null,
    val PAYEEID: Int = 0,
    val TRANSCODE: String = "", // Withdrawal, Deposit, Transfer
    val TRANSAMOUNT: Double = 0.0,
    val STATUS: String? = null, // None, Reconciled, Void, Follow up, Duplicate
    val TRANSACTIONNUMBER: String? = null,
    val NOTES: String? = null,
    val CATEGID: Int? = null,
    val TRANSDATE: String? = null,
    val FOLLOWUPID: Int? = null,
    val TOTRANSAMOUNT: Double? = null,
    val REPEATS: Int? = null,
    val NEXTOCCURRENCEDATE: String? = null,
    val NUMOCCURRENCES: Int? = null,
    val COLOR: Int = -1
)

enum class RepeatFrequency(val displayName: String, val numeric : Int = 0) {
    NONE("None", 0),
    WEEKLY("Weekly", 1),
    OTHER_WEEK("Every Other Week", 2),
    MONTHLY("Monthly", 3),
    OTHER_MONTH("Every Other Month", 4),
    QUARTERLY("Quarterly", 5),
    HALF_YEARLY("Every 6 Months", 6),
    YEARLY("Yearly", 7),
    FOUR_MONTHS("Every 4 months", 8),
    FOUR_WEEKS("Every 4 weeks", 9),
    DAILY("Daily", 10),
    N_DAYS("In (n) Days", 11),
    N_MONTHS("In (n) Months", 12),
    MONTHLY_LAST("Monthly (last day)", 13),
    MONTHLY_LAST_BUSINESS("Monthly (last business day)", 14)
}

fun numericOf(displayName: String) : Int {
    return enumValues<RepeatFrequency>().find { it.displayName == displayName }?.numeric ?: -1
}