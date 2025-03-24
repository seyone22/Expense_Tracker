package com.seyone22.expensetracker.ui.screen.transactions

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.seyone22.expensetracker.BaseViewModel
import com.seyone22.expensetracker.data.model.Account
import com.seyone22.expensetracker.data.model.BillsDepositWithDetails
import com.seyone22.expensetracker.data.model.BillsDeposits
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
import com.seyone22.expensetracker.ui.screen.operations.transaction.toBillsDeposits
import com.seyone22.expensetracker.ui.screen.operations.transaction.toTransaction
import com.seyone22.expensetracker.ui.screen.transactions.composables.TransactionFilters
import com.seyone22.expensetracker.utils.filterTransactions
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
    init {
        refreshTransactions(SortOption.default)
    }
    var transactionUiState by mutableStateOf(TransactionUiState())

    // Filter and sort options
    private val _filters = MutableStateFlow(TransactionFilters())
    val filters: StateFlow<TransactionFilters> = _filters

    private val _sortOption = MutableStateFlow(SortOption.default)
    val sortOption: StateFlow<SortOption> = _sortOption

    // Transactions from db
    private val _transactionsFlow = MutableStateFlow<List<TransactionWithDetails>>(emptyList())
    private val _billsDepositsFlow = billsDepositsRepository.getAllTransactionsStream()

    val transactionsUiState: StateFlow<TransactionScreenUiState> = combine(
        _transactionsFlow,
        _billsDepositsFlow,
    ) { transactions, billsDeposits ->
        TransactionScreenUiState(
            transactions = transactions,
            billsDeposits = billsDeposits,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(TransactionEntryViewModel.TIMEOUT_MILLIS),
        initialValue = TransactionScreenUiState()
    )

    // Computed filtered and sorted transactions
    val filteredTransactions: StateFlow<List<TransactionWithDetails>> = combine(
        _transactionsFlow, _filters, _sortOption
    ) { transactions, filters, sortOption ->
        filterTransactions(
            transactions,
            filters.timeFilter,
            filters.typeFilter,
            filters.statusFilter,
            filters.payeeFilter,
            filters.categoryFilter,
            filters.accountFilter,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    // Function to apply filters
    fun setFilters(newFilters: TransactionFilters) {
        _filters.value = newFilters
    }

    // Function to set sorting
    fun setSortOption(newSortOption: SortOption) {
        _sortOption.value = newSortOption
        refreshTransactions(newSortOption)
    }

    private fun refreshTransactions(sortOption: SortOption) {
        viewModelScope.launch {
            // Fetch sorted transactions
            val sortedTransactions = transactionsRepository.getAllTransactionsStream(
                sortField = sortOption.key, sortDirection = sortOption.order
            ).firstOrNull() ?: emptyList()

            // Update the flow with the new transactions
            _transactionsFlow.value = sortedTransactions // Using `setValue()` or direct assignment
            Log.d("TAG", "refreshTransactions: $sortedTransactions")
        }
    }

    suspend fun getAccountFromId(accountId: Int): Account? {
        return accountsRepository.getAccountStream(accountId).firstOrNull()
    }

    suspend fun deleteTransaction(transaction: Transaction): Boolean {
        return try {
            transactionsRepository.deleteTransaction(transaction)
            true
        } catch (e: Exception) {
            Log.e("TransactionsViewModel", "Error deleting transaction", e)
            false
        }
    }

    suspend fun editTransaction(transactionDetails: TransactionDetails): Boolean {
        return try {
            transactionsRepository.updateTransaction(transactionDetails.toTransaction())
            true
        } catch (e: Exception) {
            Log.e("TransactionsViewModel", "Error updating transaction", e)
            false
        }
    }

    suspend fun deleteBillDeposit(billsDeposit: BillsDeposits): Boolean {
        return try {
            billsDepositsRepository.deleteBillsDeposit(billsDeposit)
            true
        } catch (e: Exception) {
            return false
        }
    }

    suspend fun editBillsDeposit(billsDepositsDetails: BillsDepositsDetails): Boolean {
        return try {
            billsDepositsRepository.updateBillsDeposit(billsDepositsDetails.toBillsDeposits())
            true
        } catch (e: Exception) {
            Log.e("TransactionsViewModel", "Error updating transaction", e)
            false
        }
    }


    private fun validateInput(uiState: TransactionDetails = transactionUiState.transactionDetails): Boolean {
        return listOf(
            uiState.transAmount, uiState.transDate, uiState.accountId, uiState.categoryId
        ).all { it.isNotBlank() }
    }

    fun updateUiState(
        transactionDetails: TransactionDetails, billsDepositsDetails: BillsDepositsDetails
    ) {
        transactionUiState = TransactionUiState(
            transactionDetails = transactionDetails,
            billsDepositsDetails = billsDepositsDetails,
            isEntryValid = validateInput(transactionDetails),
        )
    }
}

/**
 * UI State to hold transactions and bills deposits.
 */
data class TransactionScreenUiState(
    val transactions: List<TransactionWithDetails> = emptyList(),
    val billsDeposits: List<BillsDepositWithDetails> = emptyList()
)
