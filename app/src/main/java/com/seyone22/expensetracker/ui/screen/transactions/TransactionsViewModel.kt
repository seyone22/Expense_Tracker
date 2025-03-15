package com.seyone22.expensetracker.ui.screen.transactions

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.seyone22.expensetracker.BaseViewModel
import com.seyone22.expensetracker.data.model.Account
import com.seyone22.expensetracker.data.model.BillsDepositWithDetails
import com.seyone22.expensetracker.data.model.Transaction
import com.seyone22.expensetracker.data.model.TransactionWithDetails
import com.seyone22.expensetracker.data.repository.account.AccountsRepository
import com.seyone22.expensetracker.data.repository.billsDeposit.BillsDepositsRepository
import com.seyone22.expensetracker.data.repository.transaction.TransactionsRepository
import com.seyone22.expensetracker.ui.common.SortOption
import com.seyone22.expensetracker.ui.screen.operations.transaction.BillsDepositsDetails
import com.seyone22.expensetracker.ui.screen.operations.transaction.TransactionDetails
import com.seyone22.expensetracker.ui.screen.operations.transaction.TransactionEntryViewModel
import com.seyone22.expensetracker.ui.screen.operations.transaction.TransactionUiState
import com.seyone22.expensetracker.ui.screen.operations.transaction.toTransaction
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel to retrieve all items in the Room database.
 */
class TransactionsViewModel(
    private val transactionsRepository: TransactionsRepository,
    private val billsDepositsRepository: BillsDepositsRepository,
    private val accountsRepository: AccountsRepository,
) : BaseViewModel() {
    var transactionUiState by mutableStateOf(TransactionUiState())

    private val _transactionsFlow = MutableStateFlow<List<TransactionWithDetails>>(emptyList())
    val transactionsFlow: StateFlow<List<TransactionWithDetails>> get() = _transactionsFlow

    private val billsDepositsFlow = billsDepositsRepository.getAllTransactionsStream()

    val transactionsUiState: StateFlow<TransactionScreenUiState> = combine(
        transactionsFlow,
        billsDepositsFlow,
    ) { transactions, billsDeposits ->
        Log.d("TransactionsViewModel", "Transactions: $transactions, BillsDeposits: $billsDeposits")
        TransactionScreenUiState(
            transactions = transactions,
            billsDeposits = billsDeposits,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(TransactionEntryViewModel.TIMEOUT_MILLIS),
        initialValue = TransactionScreenUiState()
    )

    init {
        refreshTransactions()
    }

    private fun refreshTransactions(
        sortField: String = "transDate",
        sortDirection: String = "DESC"
    ) {
        viewModelScope.launch {
            _transactionsFlow.value = transactionsRepository.getAllTransactionsStream(
                sortField = sortField,
                sortDirection = sortDirection
            ).firstOrNull() ?: emptyList()
        }
    }

    suspend fun getAccountFromId(accountId: Int): Account? {
        return accountsRepository.getAccountStream(accountId).firstOrNull()
    }

    suspend fun deleteTransaction(transaction: Transaction): Boolean {
        return try {
            transactionsRepository.deleteTransaction(transaction)
            refreshTransactions()
            true
        } catch (e: Exception) {
            Log.e("TransactionsViewModel", "Error deleting transaction", e)
            false
        }
    }

    suspend fun editTransaction(transactionDetails: TransactionDetails): Boolean {
        return try {
            transactionsRepository.updateTransaction(transactionDetails.toTransaction())
            refreshTransactions()
            true
        } catch (e: Exception) {
            Log.e("TransactionsViewModel", "Error updating transaction", e)
            false
        }
    }

    private fun validateInput(uiState: TransactionDetails = transactionUiState.transactionDetails): Boolean {
        return listOf(
            uiState.transAmount,
            uiState.transDate,
            uiState.accountId,
            uiState.categoryId
        ).all { it.isNotBlank() }
    }

    fun updateUiState(
        transactionDetails: TransactionDetails,
        billsDepositsDetails: BillsDepositsDetails
    ) {
        transactionUiState =
            TransactionUiState(
                transactionDetails = transactionDetails,
                billsDepositsDetails = billsDepositsDetails,
                isEntryValid = validateInput(transactionDetails),
            )
    }

    fun sortTransactions(selectedSort: SortOption) {
        refreshTransactions(sortField = selectedSort.key, sortDirection = selectedSort.order)
    }
}

/**
 * UI State to hold transactions and bills deposits.
 */
data class TransactionScreenUiState(
    val transactions: List<TransactionWithDetails> = emptyList(),
    val billsDeposits: List<BillsDepositWithDetails> = emptyList()
)
