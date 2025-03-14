package com.seyone22.expensetracker.utils

import android.util.Log
import com.seyone22.expensetracker.data.model.BillsDepositWithDetails
import com.seyone22.expensetracker.data.model.TransactionCode
import com.seyone22.expensetracker.data.model.TransactionStatus
import com.seyone22.expensetracker.data.model.TransactionWithDetails
import com.seyone22.expensetracker.ui.common.FilterOption
import java.time.LocalDate
import java.time.format.DateTimeFormatter

fun filterTransactions(
    transactions: List<TransactionWithDetails>,
    timeFilter: FilterOption?,
    typeFilter: TransactionCode?,
    selectedStatusFilter: TransactionStatus?
): List<TransactionWithDetails> {
    val now = LocalDate.now()

    // Apply time-based filtering
    val timeFilteredTransactions = when (timeFilter) {
        null -> transactions

        FilterOption.CURRENT_MONTH -> transactions.filter {
            val transactionDate = LocalDate.parse(it.transDate, DateTimeFormatter.ISO_LOCAL_DATE)
            transactionDate.year == now.year && transactionDate.monthValue == now.monthValue
        }

        FilterOption.CURRENT_MONTH_TO_DATE -> transactions.filter {
            val transactionDate = LocalDate.parse(it.transDate, DateTimeFormatter.ISO_LOCAL_DATE)
            transactionDate.isBefore(now.plusDays(1)) && transactionDate.monthValue == now.monthValue && transactionDate.year == now.year
        }

        FilterOption.LAST_MONTH -> transactions.filter {
            val transactionDate = LocalDate.parse(it.transDate, DateTimeFormatter.ISO_LOCAL_DATE)
            val lastMonth = now.minusMonths(1)
            transactionDate.year == lastMonth.year && transactionDate.monthValue == lastMonth.monthValue
        }

        FilterOption.LAST_30_DAYS -> transactions.filter {
            val transactionDate = LocalDate.parse(it.transDate, DateTimeFormatter.ISO_LOCAL_DATE)
            !transactionDate.isBefore(now.minusDays(30))
        }

        FilterOption.LAST_90_DAYS -> transactions.filter {
            val transactionDate = LocalDate.parse(it.transDate, DateTimeFormatter.ISO_LOCAL_DATE)
            !transactionDate.isBefore(now.minusDays(90))
        }

        FilterOption.LAST_3_MONTHS -> transactions.filter {
            val transactionDate = LocalDate.parse(it.transDate, DateTimeFormatter.ISO_LOCAL_DATE)
            transactionDate.isAfter(now.minusMonths(3).withDayOfMonth(1).minusDays(1)) &&
                    transactionDate.isBefore(now.plusDays(1))
        }

        FilterOption.LAST_12_MONTHS -> transactions.filter {
            val transactionDate = LocalDate.parse(it.transDate, DateTimeFormatter.ISO_LOCAL_DATE)
            transactionDate.isAfter(now.minusYears(1).withDayOfMonth(1).minusDays(1)) &&
                    transactionDate.isBefore(now.plusDays(1))
        }

        FilterOption.CURRENT_YEAR -> transactions.filter {
            val transactionDate = LocalDate.parse(it.transDate, DateTimeFormatter.ISO_LOCAL_DATE)
            transactionDate.year == now.year
        }

        FilterOption.CURRENT_YEAR_TO_DATE -> transactions.filter {
            val transactionDate = LocalDate.parse(it.transDate, DateTimeFormatter.ISO_LOCAL_DATE)
            transactionDate.isBefore(now.plusDays(1)) && transactionDate.year == now.year
        }

        FilterOption.LAST_YEAR -> transactions.filter {
            val transactionDate = LocalDate.parse(it.transDate, DateTimeFormatter.ISO_LOCAL_DATE)
            transactionDate.year == now.year - 1
        }

        FilterOption.CURRENT_FINANCIAL_YEAR -> transactions.filter {
            val transactionDate = LocalDate.parse(it.transDate, DateTimeFormatter.ISO_LOCAL_DATE)
            val startOfFinancialYear = if (now.monthValue < 4) {
                LocalDate.of(now.year - 1, 4, 1)
            } else {
                LocalDate.of(now.year, 4, 1)
            }
            val endOfFinancialYear = startOfFinancialYear.plusYears(1).minusDays(1)
            transactionDate.isAfter(startOfFinancialYear.minusDays(1)) &&
                    transactionDate.isBefore(endOfFinancialYear.plusDays(1))
        }

        FilterOption.CURRENT_FINANCIAL_YEAR_TO_DATE -> transactions.filter {
            val transactionDate = LocalDate.parse(it.transDate, DateTimeFormatter.ISO_LOCAL_DATE)
            val startOfFinancialYear = if (now.monthValue < 4) {
                LocalDate.of(now.year - 1, 4, 1)
            } else {
                LocalDate.of(now.year, 4, 1)
            }
            transactionDate.isAfter(startOfFinancialYear.minusDays(1)) && transactionDate.isBefore(
                now.plusDays(1)
            )
        }

        FilterOption.LAST_FINANCIAL_YEAR -> transactions.filter {
            val transactionDate = LocalDate.parse(it.transDate, DateTimeFormatter.ISO_LOCAL_DATE)
            val startOfLastFinancialYear = if (now.monthValue < 4) {
                LocalDate.of(now.year - 2, 4, 1)
            } else {
                LocalDate.of(now.year - 1, 4, 1)
            }
            val endOfLastFinancialYear = startOfLastFinancialYear.plusYears(1).minusDays(1)
            transactionDate.isAfter(startOfLastFinancialYear.minusDays(1)) &&
                    transactionDate.isBefore(endOfLastFinancialYear.plusDays(1))
        }

        FilterOption.OVER_TIME -> transactions // No filtering, return all transactions

        FilterOption.LAST_365_DAYS -> transactions.filter {
            val transactionDate = LocalDate.parse(it.transDate, DateTimeFormatter.ISO_LOCAL_DATE)
            !transactionDate.isBefore(now.minusDays(365))
        }

        FilterOption.CUSTOM -> transactions // Assume custom filtering is handled elsewhere
    }

    // Apply type-based filtering **after** time filtering
    val typeFilteredTransactions = if (typeFilter != null) {
        timeFilteredTransactions.filter { it.transCode == typeFilter.displayName }
    } else {
        timeFilteredTransactions
    }

    //Apply status based filtering
    val statusFilteredTransactions = if (selectedStatusFilter != null) {
        typeFilteredTransactions.filter { it.status == selectedStatusFilter.displayName }
    } else {
        typeFilteredTransactions
    }

    Log.d("TAG", "filterTransactions: Page load!")
    Log.d("TAG", "filterTransactions: $transactions")
    Log.d("TAG", "filterTransactions: $timeFilteredTransactions")
    Log.d("TAG", "filterTransactions: $typeFilteredTransactions")

    return statusFilteredTransactions
}

fun filterBillDeposits(
    transactions: List<BillsDepositWithDetails>,
    timeFilter: FilterOption?,
    typeFilter: TransactionCode?,
    selectedStatusFilter: TransactionStatus?
): List<BillsDepositWithDetails> {
    val now = LocalDate.now()

    // Apply time-based filtering
    val timeFilteredTransactions = when (timeFilter) {
        null -> transactions

        FilterOption.CURRENT_MONTH -> transactions.filter {
            val transactionDate = LocalDate.parse(it.TRANSDATE, DateTimeFormatter.ISO_LOCAL_DATE)
            transactionDate.year == now.year && transactionDate.monthValue == now.monthValue
        }

        FilterOption.CURRENT_MONTH_TO_DATE -> transactions.filter {
            val transactionDate = LocalDate.parse(it.TRANSDATE, DateTimeFormatter.ISO_LOCAL_DATE)
            transactionDate.isBefore(now.plusDays(1)) && transactionDate.monthValue == now.monthValue && transactionDate.year == now.year
        }

        FilterOption.LAST_MONTH -> transactions.filter {
            val transactionDate = LocalDate.parse(it.TRANSDATE, DateTimeFormatter.ISO_LOCAL_DATE)
            val lastMonth = now.minusMonths(1)
            transactionDate.year == lastMonth.year && transactionDate.monthValue == lastMonth.monthValue
        }

        FilterOption.LAST_30_DAYS -> transactions.filter {
            val transactionDate = LocalDate.parse(it.TRANSDATE, DateTimeFormatter.ISO_LOCAL_DATE)
            !transactionDate.isBefore(now.minusDays(30))
        }

        FilterOption.LAST_90_DAYS -> transactions.filter {
            val transactionDate = LocalDate.parse(it.TRANSDATE, DateTimeFormatter.ISO_LOCAL_DATE)
            !transactionDate.isBefore(now.minusDays(90))
        }

        FilterOption.LAST_3_MONTHS -> transactions.filter {
            val transactionDate = LocalDate.parse(it.TRANSDATE, DateTimeFormatter.ISO_LOCAL_DATE)
            transactionDate.isAfter(now.minusMonths(3).withDayOfMonth(1).minusDays(1)) &&
                    transactionDate.isBefore(now.plusDays(1))
        }

        FilterOption.LAST_12_MONTHS -> transactions.filter {
            val transactionDate = LocalDate.parse(it.TRANSDATE, DateTimeFormatter.ISO_LOCAL_DATE)
            transactionDate.isAfter(now.minusYears(1).withDayOfMonth(1).minusDays(1)) &&
                    transactionDate.isBefore(now.plusDays(1))
        }

        FilterOption.CURRENT_YEAR -> transactions.filter {
            val transactionDate = LocalDate.parse(it.TRANSDATE, DateTimeFormatter.ISO_LOCAL_DATE)
            transactionDate.year == now.year
        }

        FilterOption.CURRENT_YEAR_TO_DATE -> transactions.filter {
            val transactionDate = LocalDate.parse(it.TRANSDATE, DateTimeFormatter.ISO_LOCAL_DATE)
            transactionDate.isBefore(now.plusDays(1)) && transactionDate.year == now.year
        }

        FilterOption.LAST_YEAR -> transactions.filter {
            val transactionDate = LocalDate.parse(it.TRANSDATE, DateTimeFormatter.ISO_LOCAL_DATE)
            transactionDate.year == now.year - 1
        }

        FilterOption.CURRENT_FINANCIAL_YEAR -> transactions.filter {
            val transactionDate = LocalDate.parse(it.TRANSDATE, DateTimeFormatter.ISO_LOCAL_DATE)
            val startOfFinancialYear = if (now.monthValue < 4) {
                LocalDate.of(now.year - 1, 4, 1)
            } else {
                LocalDate.of(now.year, 4, 1)
            }
            val endOfFinancialYear = startOfFinancialYear.plusYears(1).minusDays(1)
            transactionDate.isAfter(startOfFinancialYear.minusDays(1)) &&
                    transactionDate.isBefore(endOfFinancialYear.plusDays(1))
        }

        FilterOption.CURRENT_FINANCIAL_YEAR_TO_DATE -> transactions.filter {
            val transactionDate = LocalDate.parse(it.TRANSDATE, DateTimeFormatter.ISO_LOCAL_DATE)
            val startOfFinancialYear = if (now.monthValue < 4) {
                LocalDate.of(now.year - 1, 4, 1)
            } else {
                LocalDate.of(now.year, 4, 1)
            }
            transactionDate.isAfter(startOfFinancialYear.minusDays(1)) && transactionDate.isBefore(
                now.plusDays(1)
            )
        }

        FilterOption.LAST_FINANCIAL_YEAR -> transactions.filter {
            val transactionDate = LocalDate.parse(it.TRANSDATE, DateTimeFormatter.ISO_LOCAL_DATE)
            val startOfLastFinancialYear = if (now.monthValue < 4) {
                LocalDate.of(now.year - 2, 4, 1)
            } else {
                LocalDate.of(now.year - 1, 4, 1)
            }
            val endOfLastFinancialYear = startOfLastFinancialYear.plusYears(1).minusDays(1)
            transactionDate.isAfter(startOfLastFinancialYear.minusDays(1)) &&
                    transactionDate.isBefore(endOfLastFinancialYear.plusDays(1))
        }

        FilterOption.OVER_TIME -> transactions // No filtering, return all transactions

        FilterOption.LAST_365_DAYS -> transactions.filter {
            val transactionDate = LocalDate.parse(it.TRANSDATE, DateTimeFormatter.ISO_LOCAL_DATE)
            !transactionDate.isBefore(now.minusDays(365))
        }

        FilterOption.CUSTOM -> transactions // Assume custom filtering is handled elsewhere
    }

    // Apply type-based filtering **after** time filtering
    val typeFilteredTransactions = if (typeFilter != null) {
        timeFilteredTransactions.filter { it.TRANSCODE == typeFilter.displayName }
    } else {
        timeFilteredTransactions
    }

    //Apply some other filtering

    return typeFilteredTransactions
}
