package com.example.expensetracker

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expensetracker.data.account.AccountsRepository
import com.example.expensetracker.data.transaction.TransactionsRepository
import com.example.expensetracker.model.Account
import com.example.expensetracker.model.TransactionStatus
import com.example.expensetracker.ui.account.AccountUiState
import com.example.expensetracker.ui.transaction.TransactionUiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.toList

/**
 * ViewModel to retrieve all items in the Room database.
 */
class AccountViewModel(
    private val accountsRepository: AccountsRepository,
    private val transactionsRepository: TransactionsRepository
) : ViewModel() {

    /**
     * Holds home ui state. The list of items are retrieved from [AccountsRepository] and mapped to
     * [AccountUiState]
     */
    //TODO: ERROR HERE
    val accountsUiState: StateFlow<AccountsUiState> =
        accountsRepository.getAllAccountsStream()
            .onEach { Log.d("DEBUG", ": flow emitted $it") }
            .map { accounts ->
            val transformedList = accounts.map { account ->
                Pair(account, calculateBalance(account.accountId))
            }
            AccountsUiState(transformedList)
        }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = AccountsUiState()
            )

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

    suspend fun calculateBalance(accountId: Int): Double {
        var balance = 0.0
        var reconciledBalance = 0.0

        transactionsRepository.getTransactionsFromAccountStream(accountId)
            .collect { transaction ->
                // Iterate over the list of transactions
                transaction.forEach {
                    // Add to total
                    balance += it.transAmount
                    if (it.status == TransactionStatus.R.displayName) {
                        reconciledBalance += it.transAmount
                    }
                }
            }

        return balance
    }

    suspend fun calculateGrandBalance() {
        //Should account for different currincies
        //TODO : Convert to base currency, then calculate
    }
}

/**
 * Ui State for HomeScreen
 */
data class AccountsUiState(
    val accountList: List<Pair<Account, Double>> = emptyList()
)
