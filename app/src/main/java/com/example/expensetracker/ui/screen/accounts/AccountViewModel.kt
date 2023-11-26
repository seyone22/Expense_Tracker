package com.example.expensetracker.ui.screen.accounts

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expensetracker.data.account.AccountsRepository
import com.example.expensetracker.data.currencyFormat.CurrencyFormatsRepository
import com.example.expensetracker.data.metadata.MetadataRepository
import com.example.expensetracker.data.transaction.TransactionDao
import com.example.expensetracker.data.transaction.TransactionsRepository
import com.example.expensetracker.data.userPreferences.UserPreferencesRepository
import com.example.expensetracker.model.Account
import com.example.expensetracker.model.AccountTypes
import com.example.expensetracker.model.CurrencyFormat
import com.example.expensetracker.model.TransactionCode
import com.example.expensetracker.model.TransactionStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.forEach
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel to retrieve all items in the Room database.
 */
class AccountViewModel(
    private val accountsRepository: AccountsRepository,
    private val transactionsRepository: TransactionsRepository,
    private val metadataRepository: MetadataRepository,
    private val currencyFormatsRepository: CurrencyFormatsRepository,
) : ViewModel() {
    // Flow for expenses
    private val expensesFlow: Flow<Double> = transactionsRepository.getTotalBalanceByCode("Withdrawal")

    // Flow for income
    private val incomeFlow: Flow<Double> = transactionsRepository.getTotalBalanceByCode("Deposit")

    // Flow for total
    private val totalFlow: Flow<Double> = transactionsRepository.getTotalBalance()

    // Combine the flows and calculate the totals
    val totals: Flow<Totals> = combine(expensesFlow, incomeFlow, totalFlow) { expenses, income, total ->
        Totals(expenses, income, total)
    }

    val baseCurrencyId =
        metadataRepository.getMetadataByNameStream("BASECURRENCYID")
            .map { info ->
                info?.infoValue?.toString() ?: "-1"
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = "-1"
            )
    val isUsed =
        metadataRepository.getMetadataByNameStream("ISUSED")
            .map { info ->
                info?.infoValue?.toString() ?: "FALSE"
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = 0
            )

    val accountsUiState: StateFlow<AccountsUiState> =
        accountsRepository.getAllAccountsStream()
            //.onEach { Log.d("DEBUG", ": flow emitted $it") }
            .map { accounts ->
                val transformedList = accounts.map { account ->
                    Pair(account, 0.0)
                }
                AccountsUiState(transformedList, 0.0)
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = AccountsUiState()
            )

    val data : StateFlow<AccountsUiStateOne> =
        transactionsRepository.getBalanceByAccountId()
            .map { pairs ->
                AccountsUiStateOne(pairs)
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = AccountsUiStateOne()
            )

    //TODO : FIX
    suspend fun getBaseCurrencyInfo(baseCurrencyId: Int): CurrencyFormat {
        val x = currencyFormatsRepository.getCurrencyFormatsStream(baseCurrencyId)
            .firstOrNull() ?: CurrencyFormat()
        return x
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

    fun countInType(accountType: AccountTypes, accountList: List<Pair<Account, Double>>): Int {
        var counter = 0
        accountList.forEach {
            if (it.first.accountType == accountType.displayName) {
                counter++
            }
        }
        return counter
    }
}

/**
 * Ui State for HomeScreen
 */
data class AccountsUiState(
    val accountList: List<Pair<Account, Double>> = emptyList(),
    val grandTotal: Double = 0.0
)

data class AccountsUiStateOne(
    val balancesList: List<TransactionDao.BalanceResult> = emptyList(),
    val grandTotal: Double = 0.0
)

data class Totals(
    val expenses: Double = 0.0,
    val income: Double = 0.0,
    val total : Double = 0.0
)

