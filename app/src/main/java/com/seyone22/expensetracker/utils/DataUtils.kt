package com.seyone22.expensetracker.utils

import android.util.Log
import com.seyone22.expensetracker.data.model.BudgetEntry

fun getMonthlyValue(budgetEntry: BudgetEntry): Double {
    val amount = budgetEntry.amount  // Default to 0 if amount is null

    val periodMultiplier = when (budgetEntry.period) {
        "None", null -> 0.0
        "Daily" -> 30.0
        "Weekly" -> 4.0
        "Every 2 Weeks" -> 2.0
        "Monthly" -> 1.0
        "Every 2 Months" -> 0.5
        "Quarterly" -> 1.0 / 3.0
        "Half-yearly" -> 1.0 / 6.0
        "Yearly" -> 1.0 / 12.0
        else -> 0.0 // Default to 0 if the period is unknown
    }

    return amount * periodMultiplier
}

fun convertBudgetValue(budgetEntry: BudgetEntry?, targetPeriod: String?): Double {
    val amount = budgetEntry?.amount ?: return 0.0 // Default to 0 if amount is null

    Log.d("convertBudgetValue", "targetPeriod: $targetPeriod")
    Log.d("convertBudgetValue", "amount: $amount")

    // Define period multipliers in terms of conversions to monthly and yearly values
    val toMonthlyMultiplier = mapOf(
        "None" to 0.0,
        "Daily" to 30.0,
        "Weekly" to 4.0,         // 4 weeks in a month
        "Every 2 Weeks" to 2.0,  // 2 half-months in a month
        "Monthly" to 1.0,
        "Every 2 Months" to 0.5, // Half a month per cycle
        "Quarterly" to 1.0 / 3.0, // 3 months in a quarter
        "Half-yearly" to 1.0 / 6.0, // 6 months in a half-year
        "Yearly" to 1.0 / 12.0 // 12 months in a year
    )

    val toYearlyMultiplier = mapOf(
        "None" to 0.0,
        "Daily" to 365.0,
        "Weekly" to 52.0,        // 52 weeks in a year
        "Every 2 Weeks" to 26.0, // 26 two-week periods in a year
        "Monthly" to 12.0,       // 12 months in a year
        "Every 2 Months" to 6.0, // 6 two-month periods in a year
        "Quarterly" to 4.0,      // 4 quarters in a year
        "Half-yearly" to 2.0,    // 2 half-years in a year
        "Yearly" to 1.0
    )

    val sourcePeriod = budgetEntry.period
    if (sourcePeriod !in toMonthlyMultiplier || targetPeriod !in toMonthlyMultiplier) return 0.0

    val multiplier = when (targetPeriod) {
        "Monthly" -> toMonthlyMultiplier[sourcePeriod] ?: 1.0
        "Yearly" -> toYearlyMultiplier[sourcePeriod] ?: 1.0
        else -> return amount // If invalid period, return unchanged
    }

    Log.d("convertBudgetValue", "Multiplier: $multiplier")
    Log.d("convertBudgetValue", "Converted Amount: ${amount * multiplier}")

    return amount * multiplier
}

