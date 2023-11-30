package com.example.expensetracker.ui.screen.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expensetracker.data.transaction.TransactionsRepository
import com.example.expensetracker.ui.screen.operations.account.AccountDetailTransactionUiState
import com.example.expensetracker.ui.screen.operations.transaction.TransactionEntryViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/**
 * ViewModel to retrieve all items in the Room database.
 */
class TransactionsViewModel(
    private val transactionsRepository: TransactionsRepository
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
}