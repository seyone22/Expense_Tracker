package com.seyone22.expensetracker.ui.screen.home

import androidx.lifecycle.viewModelScope
import com.seyone22.expensetracker.BaseViewModel
import com.seyone22.expensetracker.data.model.Account
import com.seyone22.expensetracker.data.model.AccountTypes
import com.seyone22.expensetracker.data.model.TransactionWithDetails
import com.seyone22.expensetracker.data.repository.account.AccountsRepository
import com.seyone22.expensetracker.data.repository.currencyFormat.CurrencyFormatsRepository
import com.seyone22.expensetracker.data.repository.metadata.MetadataRepository
import com.seyone22.expensetracker.data.repository.transaction.TransactionsRepository
import com.seyone22.expensetracker.utils.getCurrentWeekNumber
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate

/**
 * ViewModel for the Home screen, responsible for managing UI state
 * related to accounts, transactions, and metadata.
 */
class HomeViewModel(
    private val accountsRepository: AccountsRepository,
    private val transactionsRepository: TransactionsRepository,
    private val metadataRepository: MetadataRepository,
    private val currencyFormatsRepository: CurrencyFormatsRepository
) : BaseViewModel() {

    // Flow for different transaction types (expenses, income, total)
    private val expensesFlow = transactionsRepository.getTotalBalanceByCode("Withdrawal")
    private val incomeFlow = transactionsRepository.getTotalBalanceByCode("Deposit")
    private val totalFlow = transactionsRepository.getTotalBalance()

    // Direct access from frontend composable
    val expensesByWeekFlow = transactionsRepository.getTotalExpensesForWeek(getCurrentWeekNumber())

    // Combine flows for expenses, income, and total into one flow
    private val totalsFlow =
        combine(expensesFlow, incomeFlow, totalFlow) { expenses, income, total ->
            Totals(expenses, income, total)
        }

    // Home UI state, includes account details and transactions
    val accountsUiState: StateFlow<HomeUiState> = combine(
        accountsRepository.getAllAccountsStream(), transactionsRepository.getAllTransactionsStream()
    ) { accounts, transactions ->
        // Retrieve account balances and create a list of account-summary pairs
        val transformedList = accounts.map { account ->
            val balance = transactionsRepository.getBalanceByAccountId().firstOrNull()
                ?.find { it.accountId == account.accountId }?.balance ?: 0.0
            account to balance
        }

        // Return the HomeUiState with both the transformed account list and transactions
        HomeUiState(
            accountList = transformedList,
            transactionSample = transactions.take(5) // Adjust how many transactions you want to show as a sample
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(TIMEOUT_MILLIS), HomeUiState())

    /**
     * Count the number of accounts of a given type.
     */
    fun countInType(accountType: AccountTypes, accountList: List<Pair<Account, Double>>): Int {
        return accountList.count { it.first.accountType == accountType.displayName }
    }

    /**
     * Filter the totals based on a specified filter criteria (e.g., "All", "Current Month").
     */
    fun getFilteredTotal(filter: String): Flow<Totals> {
        return when (filter) {
            "Current Month" -> {
                val currentMonth = LocalDate.now().monthValue
                val currentYear = LocalDate.now().year
                val newExpensesFlow = transactionsRepository.getTotalBalanceByCodeAndDate(
                    "Withdrawal", month = currentMonth, year = currentYear
                )
                val newIncomeFlow = transactionsRepository.getTotalBalanceByCodeAndDate(
                    "Deposit", month = currentMonth, year = currentYear
                )
                val newTotalFlow =
                    transactionsRepository.getTotalBalanceByDate("Total", currentMonth, currentYear)

                combine(newExpensesFlow, newIncomeFlow, newTotalFlow) { expenses, income, total ->
                    Totals(expenses, income, total)
                }
            }

            else -> totalsFlow // Default case, returns all totals
        }
    }
}

/**
 * UI state for the Home screen.
 */
data class HomeUiState(
    val accountList: List<Pair<Account, Double>> = emptyList(), // Account info with balance
    val grandTotal: Double = 0.0, // Total balance for all accounts
    val transactionSample: List<TransactionWithDetails> = emptyList() // List of transaction details
)

/**
 * Data class to hold totals for expenses, income, and overall balance.
 */
data class Totals(
    val expenses: Double = 0.0, val income: Double = 0.0, val total: Double = 0.0
)
