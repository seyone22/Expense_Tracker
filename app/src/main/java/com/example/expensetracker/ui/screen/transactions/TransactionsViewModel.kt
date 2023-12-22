package com.example.expensetracker.ui.screen.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expensetracker.data.account.AccountsRepository
import com.example.expensetracker.data.currencyFormat.CurrencyFormatsRepository
import com.example.expensetracker.data.transaction.TransactionsRepository
import com.example.expensetracker.model.Account
import com.example.expensetracker.model.CurrencyFormat
import com.example.expensetracker.ui.screen.operations.account.AccountDetailTransactionUiState
import com.example.expensetracker.ui.screen.operations.transaction.TransactionEntryViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/**
 * ViewModel to retrieve all items in the Room database.
 */
class TransactionsViewModel(
    private val transactionsRepository: TransactionsRepository,
    private val accountsRepository: AccountsRepository,
    private val currencyFormatsRepository: CurrencyFormatsRepository
) : ViewModel() {
    var transactionsUiState: StateFlow<AccountDetailTransactionUiState> =
        transactionsRepository.getAllTransactionsStream()
            //.onEach { Log.d("DEBUG", ": flow emitted $it") }
            .map { transactions ->
                AccountDetailTransactionUiState(
                    transactions = transactions
                )
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TransactionEntryViewModel.TIMEOUT_MILLIS),
                initialValue = AccountDetailTransactionUiState()
            )

    suspend fun getAccountFromId(accountId: Int) : Account? {
        return accountsRepository.getAccountStream(accountId).firstOrNull()
    }

    suspend fun getCurrencyFormatById(currencyId: Int) : CurrencyFormat? {
        return currencyFormatsRepository.getCurrencyFormatStream(currencyId).firstOrNull()
    }
}