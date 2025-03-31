package com.seyone22.expensetracker.ui.screen.home

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.seyone22.expensetracker.BaseViewModel
import com.seyone22.expensetracker.data.model.Account
import com.seyone22.expensetracker.data.model.AccountTypes
import com.seyone22.expensetracker.data.model.TransactionWithDetails
import com.seyone22.expensetracker.data.repository.account.AccountsRepository
import com.seyone22.expensetracker.data.repository.currencyFormat.CurrencyFormatsRepository
import com.seyone22.expensetracker.data.repository.metadata.MetadataRepository
import com.seyone22.expensetracker.data.repository.transaction.BalanceResult
import com.seyone22.expensetracker.data.repository.transaction.TransactionsRepository
import com.seyone22.expensetracker.utils.getEndOfCurrentWeek
import com.seyone22.expensetracker.utils.getStartOfPreviousWeek
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

class HomeViewModel(
    private val accountsRepository: AccountsRepository,
    private val transactionsRepository: TransactionsRepository,
    private val metadataRepository: MetadataRepository,
    private val currencyFormatsRepository: CurrencyFormatsRepository
) : BaseViewModel() {

    private val _currentStartDate = mutableStateOf(getStartOfPreviousWeek())
    private val _currentEndDate = mutableStateOf(getEndOfCurrentWeek())
    val currentStartDate: State<String> get() = _currentStartDate
    val currentEndDate: State<String> get() = _currentEndDate

    private val expensesFlow = transactionsRepository.getTotalBalanceByCode("Withdrawal")
    private val incomeFlow = transactionsRepository.getTotalBalanceByCode("Deposit")
    private val totalFlow = transactionsRepository.getTotalBalance()

    private val _expensesByWeekFlow = MutableStateFlow<List<BalanceResult>>(emptyList())
    val expensesByWeekFlow: StateFlow<List<BalanceResult>> = _expensesByWeekFlow

    private val _currentWeekSum = MutableStateFlow(0.0)
    val currentWeekSum: StateFlow<Double> = _currentWeekSum

    private val _previousWeekSum = MutableStateFlow(0.0)
    val previousWeekSum: StateFlow<Double> = _previousWeekSum

    private val _seriesState = MutableStateFlow(List(7) { 0.0 })
    val seriesState: StateFlow<List<Double>> = _seriesState

    val percentageChange: StateFlow<Double> =
        combine(_currentWeekSum, _previousWeekSum) { current, previous ->
            if (previous != 0.0) ((current - previous) / previous) * 100 else if (current == 0.0) 0.0 else 100.0
        }.stateIn(viewModelScope, SharingStarted.Lazily, 0.0)

    private val totalsFlow =
        combine(expensesFlow, incomeFlow, totalFlow) { expenses, income, total ->
            Totals(expenses * -1, income, total)
        }

    init {
        viewModelScope.launch {
            fetchTransactionsForWeek()
        }
    }

    private suspend fun fetchTransactionsForWeek() {
        val entries = transactionsRepository.getExpensesForDateRange(
            _currentStartDate.value, _currentEndDate.value
        ).firstOrNull() ?: emptyList()
        _expensesByWeekFlow.value = entries
    }

    fun getPreviousWeek() {
        updateWeek(-1)
    }

    fun getNextWeek() {
        updateWeek(1)
    }

    private fun updateWeek(weeks: Long) {
        _currentStartDate.value = LocalDate.parse(_currentStartDate.value)
            .plusWeeks(weeks)
            .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
            .toString()

        _currentEndDate.value = LocalDate.parse(_currentEndDate.value)
            .plusWeeks(weeks)
            .with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY))
            .toString()

        viewModelScope.launch {
            fetchTransactionsForWeek()
        }
    }

    val accountsUiState: StateFlow<HomeUiState> = combine(
        accountsRepository.getAllAccountsStream(),
        transactionsRepository.getAllTransactionsStream("DESC", "TransDate"),
        expensesByWeekFlow
    ) { accounts, transactions, expensesByWeek ->
        val transformedList = accounts.map { account ->
            val balance = transactionsRepository.getBalanceByAccountId().firstOrNull()
                ?.find { it.accountId == account.accountId }?.balance ?: 0.0
            account to balance
        }

        HomeUiState(
            accountList = transformedList,
            transactionSample = transactions.take(5),
            expensesByWeek = expensesByWeek.map { it.copy(balance = it.balance * -1) }
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HomeUiState())

    fun countInType(accountType: AccountTypes, accountList: List<Pair<Account, Double>>): Int {
        return accountList.count { it.first.accountType == accountType.displayName }
    }

    fun getFilteredTotal(filter: String): Flow<Totals> {
        return when (filter) {
            "Current Month" -> {
                val currentYear = LocalDate.now().year.toString()
                val monthString = LocalDate.now().monthValue.toString().padStart(2, '0')
                val newExpensesFlow = transactionsRepository.getTotalBalanceByCodeAndDate(
                    "Withdrawal", month = monthString, year = currentYear
                )
                val newIncomeFlow = transactionsRepository.getTotalBalanceByCodeAndDate(
                    "Deposit", month = monthString, year = currentYear
                )
                val newTotalFlow =
                    transactionsRepository.getTotalBalanceByDate("Total", monthString, currentYear)
                combine(newExpensesFlow, newIncomeFlow, newTotalFlow) { expenses, income, total ->
                    Totals(expenses, income, total)
                }
            }

            else -> totalsFlow
        }
    }
}

data class HomeUiState(
    val accountList: List<Pair<Account, Double>> = emptyList(),
    val transactionSample: List<TransactionWithDetails> = emptyList(),
    val expensesByWeek: List<BalanceResult> = emptyList()
)

data class Totals(
    val expenses: Double = 0.0,
    val income: Double = 0.0,
    val total: Double = 0.0
)
