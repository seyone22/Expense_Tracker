package com.example.expensetracker.ui.screen.accounts

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expensetracker.data.account.AccountsRepository
import com.example.expensetracker.data.currencyFormat.CurrencyFormatsRepository
import com.example.expensetracker.data.metadata.MetadataRepository
import com.example.expensetracker.data.transaction.BalanceResult
import com.example.expensetracker.data.transaction.TransactionsRepository
import com.example.expensetracker.model.Account
import com.example.expensetracker.model.AccountTypes
import com.example.expensetracker.model.CurrencyFormat
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

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
    private val expensesFlow: Flow<Double> =
        transactionsRepository.getTotalBalanceByCode("Withdrawal")

    // Flow for income
    private val incomeFlow: Flow<Double> = transactionsRepository.getTotalBalanceByCode("Deposit")

    // Flow for total
    private val totalFlow: Flow<Double> = transactionsRepository.getTotalBalance()

    val isUsed =
        metadataRepository.getMetadataByNameStream("ISUSED")
            .map { info ->
                info?.infoValue ?: "FALSE"
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = ""
            )

    // Combine the flows and calculate the totals
    val totals: Flow<Totals> =
        combine(expensesFlow, incomeFlow, totalFlow) { expenses, income, total ->
            Totals(expenses, income, total)
        }

    val baseCurrencyId =
        metadataRepository.getMetadataByNameStream("BASECURRENCYID")
            .map { info ->
                info?.infoValue ?: "-1"
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = "-1"
            )

    val accountsUiState: StateFlow<AccountsUiState> =
        accountsRepository.getAllAccountsStream()
            .map { accounts ->
                val transformedList = accounts.map { account ->
                    val balance = accountsRepository.getAccountBalance(account.accountId).firstOrNull()?.balance
                    Pair(account, balance ?: 0.0) // Default value for balance if it's null
                }
                AccountsUiState(transformedList)
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = AccountsUiState()
            )


    val data: StateFlow<Balances> =
        transactionsRepository.getBalanceByAccountId()
            .map { pairs ->
                var totalBalance = 0.0
                var balanceAndInitialBalance : List<BalanceResult>
                for (balanceResult in pairs) {
                    totalBalance += balanceResult.balance
                    accountsRepository.getAccountStream(balanceResult.accountId).first()
                }
                Balances(pairs, totalBalance)
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = Balances()
            )


    val accountBalances: StateFlow<Balances> =
        accountsRepository.getAllActiveAccountsStream()
            .map { accountList ->
                val balanceData : MutableList<BalanceResult> = mutableListOf()
                var totalBalance = 0.0

                accountList.forEach {
                    val accountBalance = accountsRepository.getAccountBalance(it.accountId).firstOrNull()?.balance
                    val balance = it.initialBalance?.plus(accountBalance ?: 0.0)

                    balanceData.add(BalanceResult(it.accountId, balance ?: 0.0))
                    if (balance != null) {
                        totalBalance += balance
                    }
                }
                Balances(balanceData, totalBalance)
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = Balances()
            )

    //TODO : FIX
    suspend fun getBaseCurrencyInfo(baseCurrencyId: Int): CurrencyFormat {
        val x = currencyFormatsRepository.getCurrencyFormatStream(baseCurrencyId)
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

data class Balances(
    val balancesList: List<BalanceResult> = emptyList(),
    val grandTotal: Double = 0.0
)

data class Totals(
    val expenses: Double = 0.0,
    val income: Double = 0.0,
    val total: Double = 0.0
)