package com.example.expensetracker.ui.screen.transactions

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expensetracker.data.account.AccountsRepository
import com.example.expensetracker.data.currencyFormat.CurrencyFormatsRepository
import com.example.expensetracker.data.transaction.TransactionsRepository
import com.example.expensetracker.model.Account
import com.example.expensetracker.model.CurrencyFormat
import com.example.expensetracker.model.Transaction
import com.example.expensetracker.ui.screen.operations.account.AccountDetailTransactionUiState
import com.example.expensetracker.ui.screen.operations.transaction.TransactionDetails
import com.example.expensetracker.ui.screen.operations.transaction.TransactionEntryViewModel
import com.example.expensetracker.ui.screen.operations.transaction.TransactionUiState
import com.example.expensetracker.ui.screen.operations.transaction.toTransaction
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
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
    var transactionUiState by mutableStateOf(TransactionUiState())

    var transactionsUiState: StateFlow<AccountDetailTransactionUiState> =
        transactionsRepository.getAllTransactionsStream()
            .map { transactions ->
                Log.d("TAG", ": $transactions")
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

    suspend fun deleteTransaction(transaction: Transaction) : Boolean {
        return try {
            transactionsRepository.deleteTransaction(transaction)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun editTransaction(transactionDetails: TransactionDetails) : Boolean {
        return try {
            transactionsRepository.updateTransaction(transactionDetails.toTransaction())
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    private fun validateInput(uiState: TransactionDetails = transactionUiState.transactionDetails): Boolean {
        return with(uiState) {
            transAmount.isNotBlank() && transDate.isNotBlank()&& accountId.isNotBlank() && categoryId.isNotBlank()
        }
    }

    suspend fun saveTransaction() {
        if (validateInput()) {
            transactionsRepository.insertTransaction(transactionUiState.transactionDetails.toTransaction())
        }
    }

    fun updateUiState(transactionDetails: TransactionDetails) {
        transactionUiState =
            TransactionUiState(
                transactionDetails = transactionDetails,
                isEntryValid = validateInput(transactionDetails),
            )
    }
}