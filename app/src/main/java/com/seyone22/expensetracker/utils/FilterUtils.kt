package com.seyone22.expensetracker.utils

import com.seyone22.expensetracker.data.model.Account
import com.seyone22.expensetracker.data.model.BillsDepositWithDetails
import com.seyone22.expensetracker.data.model.Category
import com.seyone22.expensetracker.data.model.Payee
import com.seyone22.expensetracker.data.model.TransactionCode
import com.seyone22.expensetracker.data.model.TransactionStatus
import com.seyone22.expensetracker.data.model.TransactionWithDetails
import com.seyone22.expensetracker.ui.common.TimeRangeFilter
import java.time.LocalDate
import java.time.format.DateTimeFormatter

fun filterTransactions(
    transactions: List<TransactionWithDetails>,
    timeFilter: TimeRangeFilter?,
    typeFilter: TransactionCode?,
    statusFilter: TransactionStatus?,
    payeeFilter: Payee?,
    categoryFilter: Category?,
    accountFilter: Account?,
): List<TransactionWithDetails> {
    val now = LocalDate.now()

    // Apply time-based filtering
    val timeFilteredTransactions = when (timeFilter) {
        null -> transactions

        TimeRangeFilter.CURRENT_MONTH -> transactions.filter {
            val transactionDate = LocalDate.parse(it.transDate, DateTimeFormatter.ISO_LOCAL_DATE)
            transactionDate.year == now.year && transactionDate.monthValue == now.monthValue
        }

        TimeRangeFilter.CURRENT_MONTH_TO_DATE -> transactions.filter {
            val transactionDate = LocalDate.parse(it.transDate, DateTimeFormatter.ISO_LOCAL_DATE)
            transactionDate.isBefore(now.plusDays(1)) && transactionDate.monthValue == now.monthValue && transactionDate.year == now.year
        }

        TimeRangeFilter.LAST_MONTH -> transactions.filter {
            val transactionDate = LocalDate.parse(it.transDate, DateTimeFormatter.ISO_LOCAL_DATE)
            val lastMonth = now.minusMonths(1)
            transactionDate.year == lastMonth.year && transactionDate.monthValue == lastMonth.monthValue
        }

        TimeRangeFilter.LAST_30_DAYS -> transactions.filter {
            val transactionDate = LocalDate.parse(it.transDate, DateTimeFormatter.ISO_LOCAL_DATE)
            !transactionDate.isBefore(now.minusDays(30))
        }

        TimeRangeFilter.LAST_90_DAYS -> transactions.filter {
            val transactionDate = LocalDate.parse(it.transDate, DateTimeFormatter.ISO_LOCAL_DATE)
            !transactionDate.isBefore(now.minusDays(90))
        }

        TimeRangeFilter.LAST_3_MONTHS -> transactions.filter {
            val transactionDate = LocalDate.parse(it.transDate, DateTimeFormatter.ISO_LOCAL_DATE)
            transactionDate.isAfter(
                now.minusMonths(3).withDayOfMonth(1).minusDays(1)
            ) && transactionDate.isBefore(now.plusDays(1))
        }

        TimeRangeFilter.LAST_12_MONTHS -> transactions.filter {
            val transactionDate = LocalDate.parse(it.transDate, DateTimeFormatter.ISO_LOCAL_DATE)
            transactionDate.isAfter(
                now.minusYears(1).withDayOfMonth(1).minusDays(1)
            ) && transactionDate.isBefore(now.plusDays(1))
        }

        TimeRangeFilter.CURRENT_YEAR -> transactions.filter {
            val transactionDate = LocalDate.parse(it.transDate, DateTimeFormatter.ISO_LOCAL_DATE)
            transactionDate.year == now.year
        }

        TimeRangeFilter.CURRENT_YEAR_TO_DATE -> transactions.filter {
            val transactionDate = LocalDate.parse(it.transDate, DateTimeFormatter.ISO_LOCAL_DATE)
            transactionDate.isBefore(now.plusDays(1)) && transactionDate.year == now.year
        }

        TimeRangeFilter.LAST_YEAR -> transactions.filter {
            val transactionDate = LocalDate.parse(it.transDate, DateTimeFormatter.ISO_LOCAL_DATE)
            transactionDate.year == now.year - 1
        }

        TimeRangeFilter.CURRENT_FINANCIAL_YEAR -> transactions.filter {
            val transactionDate = LocalDate.parse(it.transDate, DateTimeFormatter.ISO_LOCAL_DATE)
            val startOfFinancialYear = if (now.monthValue < 4) {
                LocalDate.of(now.year - 1, 4, 1)
            } else {
                LocalDate.of(now.year, 4, 1)
            }
            val endOfFinancialYear = startOfFinancialYear.plusYears(1).minusDays(1)
            transactionDate.isAfter(startOfFinancialYear.minusDays(1)) && transactionDate.isBefore(
                endOfFinancialYear.plusDays(1)
            )
        }

        TimeRangeFilter.CURRENT_FINANCIAL_YEAR_TO_DATE -> transactions.filter {
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

        TimeRangeFilter.LAST_FINANCIAL_YEAR -> transactions.filter {
            val transactionDate = LocalDate.parse(it.transDate, DateTimeFormatter.ISO_LOCAL_DATE)
            val startOfLastFinancialYear = if (now.monthValue < 4) {
                LocalDate.of(now.year - 2, 4, 1)
            } else {
                LocalDate.of(now.year - 1, 4, 1)
            }
            val endOfLastFinancialYear = startOfLastFinancialYear.plusYears(1).minusDays(1)
            transactionDate.isAfter(startOfLastFinancialYear.minusDays(1)) && transactionDate.isBefore(
                endOfLastFinancialYear.plusDays(1)
            )
        }

        TimeRangeFilter.OVER_TIME -> transactions // No filtering, return all transactions

        TimeRangeFilter.LAST_365_DAYS -> transactions.filter {
            val transactionDate = LocalDate.parse(it.transDate, DateTimeFormatter.ISO_LOCAL_DATE)
            !transactionDate.isBefore(now.minusDays(365))
        }

        TimeRangeFilter.CUSTOM -> transactions // Assume custom filtering is handled elsewhere
    }

    // Apply type-based filtering **after** time filtering
    val typeFilteredTransactions = if (typeFilter != null) {
        timeFilteredTransactions.filter { it.transCode == typeFilter.displayName }
    } else {
        timeFilteredTransactions
    }

    //Apply status based filtering
    val statusFilteredTransactions = if (statusFilter != null) {
        typeFilteredTransactions.filter { it.status == statusFilter.displayName }
    } else {
        typeFilteredTransactions
    }

    // Apply account based filtering
    val accountFilteredTransactions = if (accountFilter != null) {
        statusFilteredTransactions.filter { it.accountId == accountFilter.accountId }
    } else {
        statusFilteredTransactions
    }

    // Apply payee based filtering
    val payeeFilteredTransactions = if (payeeFilter != null) {
        accountFilteredTransactions.filter { it.payeeId == payeeFilter.payeeId }
    } else {
        accountFilteredTransactions
    }

    // Apply account based filtering
    val categoryFilteredTransactions = if (categoryFilter != null) {
        payeeFilteredTransactions.filter { it.categoryId == categoryFilter.categId }
    } else {
        payeeFilteredTransactions
    }

    return categoryFilteredTransactions
}

fun filterBillDeposits(
    transactions: List<BillsDepositWithDetails>,
    timeFilter: TimeRangeFilter?,
    typeFilter: TransactionCode?,
    statusFilter: TransactionStatus?,
    payeeFilter: Payee?,
    categoryFilter: Category?,
    accountFilter: Account?,
): List<BillsDepositWithDetails> {
    val now = LocalDate.now()

    // Apply time-based filtering
    val timeFilteredTransactions = when (timeFilter) {
        null -> transactions

        TimeRangeFilter.CURRENT_MONTH -> transactions.filter {
            val transactionDate = LocalDate.parse(it.TRANSDATE, DateTimeFormatter.ISO_LOCAL_DATE)
            transactionDate.year == now.year && transactionDate.monthValue == now.monthValue
        }

        TimeRangeFilter.CURRENT_MONTH_TO_DATE -> transactions.filter {
            val transactionDate = LocalDate.parse(it.TRANSDATE, DateTimeFormatter.ISO_LOCAL_DATE)
            transactionDate.isBefore(now.plusDays(1)) && transactionDate.monthValue == now.monthValue && transactionDate.year == now.year
        }

        TimeRangeFilter.LAST_MONTH -> transactions.filter {
            val transactionDate = LocalDate.parse(it.TRANSDATE, DateTimeFormatter.ISO_LOCAL_DATE)
            val lastMonth = now.minusMonths(1)
            transactionDate.year == lastMonth.year && transactionDate.monthValue == lastMonth.monthValue
        }

        TimeRangeFilter.LAST_30_DAYS -> transactions.filter {
            val transactionDate = LocalDate.parse(it.TRANSDATE, DateTimeFormatter.ISO_LOCAL_DATE)
            !transactionDate.isBefore(now.minusDays(30))
        }

        TimeRangeFilter.LAST_90_DAYS -> transactions.filter {
            val transactionDate = LocalDate.parse(it.TRANSDATE, DateTimeFormatter.ISO_LOCAL_DATE)
            !transactionDate.isBefore(now.minusDays(90))
        }

        TimeRangeFilter.LAST_3_MONTHS -> transactions.filter {
            val transactionDate = LocalDate.parse(it.TRANSDATE, DateTimeFormatter.ISO_LOCAL_DATE)
            transactionDate.isAfter(
                now.minusMonths(3).withDayOfMonth(1).minusDays(1)
            ) && transactionDate.isBefore(now.plusDays(1))
        }

        TimeRangeFilter.LAST_12_MONTHS -> transactions.filter {
            val transactionDate = LocalDate.parse(it.TRANSDATE, DateTimeFormatter.ISO_LOCAL_DATE)
            transactionDate.isAfter(
                now.minusYears(1).withDayOfMonth(1).minusDays(1)
            ) && transactionDate.isBefore(now.plusDays(1))
        }

        TimeRangeFilter.CURRENT_YEAR -> transactions.filter {
            val transactionDate = LocalDate.parse(it.TRANSDATE, DateTimeFormatter.ISO_LOCAL_DATE)
            transactionDate.year == now.year
        }

        TimeRangeFilter.CURRENT_YEAR_TO_DATE -> transactions.filter {
            val transactionDate = LocalDate.parse(it.TRANSDATE, DateTimeFormatter.ISO_LOCAL_DATE)
            transactionDate.isBefore(now.plusDays(1)) && transactionDate.year == now.year
        }

        TimeRangeFilter.LAST_YEAR -> transactions.filter {
            val transactionDate = LocalDate.parse(it.TRANSDATE, DateTimeFormatter.ISO_LOCAL_DATE)
            transactionDate.year == now.year - 1
        }

        TimeRangeFilter.CURRENT_FINANCIAL_YEAR -> transactions.filter {
            val transactionDate = LocalDate.parse(it.TRANSDATE, DateTimeFormatter.ISO_LOCAL_DATE)
            val startOfFinancialYear = if (now.monthValue < 4) {
                LocalDate.of(now.year - 1, 4, 1)
            } else {
                LocalDate.of(now.year, 4, 1)
            }
            val endOfFinancialYear = startOfFinancialYear.plusYears(1).minusDays(1)
            transactionDate.isAfter(startOfFinancialYear.minusDays(1)) && transactionDate.isBefore(
                endOfFinancialYear.plusDays(1)
            )
        }

        TimeRangeFilter.CURRENT_FINANCIAL_YEAR_TO_DATE -> transactions.filter {
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

        TimeRangeFilter.LAST_FINANCIAL_YEAR -> transactions.filter {
            val transactionDate = LocalDate.parse(it.TRANSDATE, DateTimeFormatter.ISO_LOCAL_DATE)
            val startOfLastFinancialYear = if (now.monthValue < 4) {
                LocalDate.of(now.year - 2, 4, 1)
            } else {
                LocalDate.of(now.year - 1, 4, 1)
            }
            val endOfLastFinancialYear = startOfLastFinancialYear.plusYears(1).minusDays(1)
            transactionDate.isAfter(startOfLastFinancialYear.minusDays(1)) && transactionDate.isBefore(
                endOfLastFinancialYear.plusDays(1)
            )
        }

        TimeRangeFilter.OVER_TIME -> transactions // No filtering, return all transactions

        TimeRangeFilter.LAST_365_DAYS -> transactions.filter {
            val transactionDate = LocalDate.parse(it.TRANSDATE, DateTimeFormatter.ISO_LOCAL_DATE)
            !transactionDate.isBefore(now.minusDays(365))
        }

        TimeRangeFilter.CUSTOM -> transactions // Assume custom filtering is handled elsewhere
    }

    // Apply type-based filtering **after** time filtering
    val typeFilteredTransactions = if (typeFilter != null) {
        timeFilteredTransactions.filter { it.TRANSCODE == typeFilter.displayName }
    } else {
        timeFilteredTransactions
    }

    //Apply status based filtering
    val statusFilteredTransactions = if (statusFilter != null) {
        typeFilteredTransactions.filter { it.STATUS == statusFilter.displayName }
    } else {
        typeFilteredTransactions
    }

    // Apply account based filtering
    val accountFilteredTransactions = if (accountFilter != null) {
        statusFilteredTransactions.filter { it.ACCOUNTID == accountFilter.accountId }
    } else {
        statusFilteredTransactions
    }

    return accountFilteredTransactions
}
