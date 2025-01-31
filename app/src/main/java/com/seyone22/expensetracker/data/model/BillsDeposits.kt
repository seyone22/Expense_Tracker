package com.seyone22.expensetracker.data.model

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

enum class RepeatFrequency(val displayName: String, val numeric: Int = 0, val dayCount: Int = 0) {
    NONE("None", 0, 0),
    WEEKLY("Weekly", 1, 7),
    OTHER_WEEK("Every Other Week", 14),
    MONTHLY("Monthly", 3, -1),
    OTHER_MONTH("Every Other Month", -2),
    QUARTERLY("Quarterly", 5, -3),
    HALF_YEARLY("Every 6 Months", 6, -6),
    YEARLY("Yearly", 7, -12),
    FOUR_MONTHS("Every 4 months", 8, -4),
    FOUR_WEEKS("Every 4 weeks", 9, 28),
    DAILY("Daily", 10, 1),
    N_DAYS("In (n) Days", 11, 0),
    N_MONTHS("In (n) Months", 12, 0),
    MONTHLY_LAST("Monthly (last day)", 13, 0),
    MONTHLY_LAST_BUSINESS("Monthly (last business day)", 14, 0)
}

fun numericOf(displayName: String): Int {
    return enumValues<RepeatFrequency>().find { it.displayName == displayName }?.numeric ?: -1
}


data class BillsDepositWithDetails(
    val BDID: Int,
    val ACCOUNTID: Int,
    val TOACCOUNTID: Int?,
    val PAYEEID: Int,
    val TRANSCODE: String,
    val TRANSAMOUNT: Double,
    val STATUS: String?,
    val TRANSACTIONNUMBER: String?,
    val NOTES: String?,
    val CATEGID: Int?,
    val TRANSDATE: String?,
    val FOLLOWUPID: Int?,
    val TOTRANSAMOUNT: Double?,
    val REPEATS: Int?,
    val NEXTOCCURRENCEDATE: String?,
    val NUMOCCURRENCES: Int?,
    val COLOR: Int,
    val payeeName: String?,  // Include payeeName from PAYEE_V1, NULL when Transfer
    val categName: String  // Include categoryName from CATEGORY_V1
)

fun BillsDepositWithDetails.toBillsDeposit(): BillsDeposits {
    return BillsDeposits(
        BDID = BDID,
        ACCOUNTID = ACCOUNTID,
        TOACCOUNTID = TOACCOUNTID,
        PAYEEID = PAYEEID,
        TRANSCODE = TRANSCODE,
        TRANSAMOUNT = TRANSAMOUNT,
        STATUS = STATUS,
        TRANSACTIONNUMBER = TRANSACTIONNUMBER,
        NOTES = NOTES,
        CATEGID = CATEGID,
        TRANSDATE = TRANSDATE,
        FOLLOWUPID = FOLLOWUPID,
        TOTRANSAMOUNT = TOTRANSAMOUNT,
        REPEATS = REPEATS,
        NEXTOCCURRENCEDATE = NEXTOCCURRENCEDATE,
        NUMOCCURRENCES = NUMOCCURRENCES,
        COLOR = COLOR
    )
}

