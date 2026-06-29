package com.seyone22.expensetracker.ui.screen.reconcile

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.seyone22.expensetracker.BaseViewModel
import com.seyone22.expensetracker.data.model.Account
import com.seyone22.expensetracker.data.model.TransactionWithDetails
import com.seyone22.expensetracker.data.model.toTransaction
import com.seyone22.expensetracker.data.repository.account.AccountsRepository
import com.seyone22.expensetracker.data.repository.transaction.TransactionsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.*

data class ReconcileUiState(
    val account: Account = Account(),
    val unreconciledTransactions: List<TransactionWithDetails> = emptyList(),
    val startingBalance: Double = 0.0,
    val endingBalance: Double = 0.0,
    val statementDate: String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()),
    val checkedIds: Set<Int> = emptySet(),
    val reconciledTotal: Double = 0.0, // Sum of previously reconciled transactions
    val selectedTotal: Double = 0.0, // Sum of currently checked transactions
    val difference: Double = 0.0, // endingBalance - (startingBalance + reconciledTotal + selectedTotal)
    val isWizardStarted: Boolean = false,
    val isCompleteEnabled: Boolean = false
)

class ReconcileViewModel(
    private val accountsRepository: AccountsRepository,
    private val transactionsRepository: TransactionsRepository
) : BaseViewModel() {

    private val _uiState = MutableStateFlow(ReconcileUiState())
    val uiState: StateFlow<ReconcileUiState> get() = _uiState

    fun initReconciliation(accountId: Int) {
        viewModelScope.launch {
            val account = accountsRepository.getAccountStream(accountId).first() ?: Account()
            
            // Get all transactions for this account
            val allTransactions = transactionsRepository.getTransactionsFromAccount(accountId).first()
            
            // Unreconciled transactions list (status is null, empty or not Reconciled)
            val unreconciled = allTransactions.filter { 
                it.status != "Reconciled" && it.status != "R" 
            }

            // Calculate starting balance: Initial balance of the account
            val initialBalance = account.initialBalance ?: 0.0

            // Reconciled total (sum of all already reconciled transactions)
            // Deposits (+) and Withdrawals (-) and Transfers (if destination, it is +, if source, it is -)
            val reconciledTotal = allTransactions.filter { 
                it.status == "Reconciled" || it.status == "R"
            }.sumOf { trans ->
                calculateTransactionImpact(trans, accountId)
            }

            val startingBalance = initialBalance + reconciledTotal

            _uiState.value = _uiState.value.copy(
                account = account,
                unreconciledTransactions = unreconciled,
                startingBalance = startingBalance,
                reconciledTotal = reconciledTotal,
                difference = _uiState.value.endingBalance - startingBalance
            )
        }
    }

    fun startWizard(endingBalance: Double, statementDate: String) {
        val diff = endingBalance - (_uiState.value.startingBalance)
        _uiState.value = _uiState.value.copy(
            endingBalance = endingBalance,
            statementDate = statementDate,
            isWizardStarted = true,
            difference = diff,
            isCompleteEnabled = Math.abs(diff) < 0.01
        )
    }

    fun toggleTransactionChecked(transId: Int) {
        val currentChecked = _uiState.value.checkedIds.toMutableSet()
        if (currentChecked.contains(transId)) {
            currentChecked.remove(transId)
        } else {
            currentChecked.add(transId)
        }

        // Recompute selected total
        val selectedTotal = _uiState.value.unreconciledTransactions.filter { 
            currentChecked.contains(it.transId) 
        }.sumOf { trans ->
            calculateTransactionImpact(trans, _uiState.value.account.accountId)
        }

        // Diff = endingBalance - (startingBalance + selectedTotal)
        val diff = _uiState.value.endingBalance - (_uiState.value.startingBalance + selectedTotal)

        _uiState.value = _uiState.value.copy(
            checkedIds = currentChecked,
            selectedTotal = selectedTotal,
            difference = diff,
            isCompleteEnabled = Math.abs(diff) < 0.01
        )
    }

    suspend fun completeReconciliation(): Boolean {
        return try {
            val checkedIds = _uiState.value.checkedIds
            val transactionsToUpdate = _uiState.value.unreconciledTransactions.filter {
                checkedIds.contains(it.transId)
            }

            transactionsToUpdate.forEach { transDetails ->
                val updatedTrans = transDetails.toTransaction().copy(status = "Reconciled")
                transactionsRepository.updateTransaction(updatedTrans)
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    private fun calculateTransactionImpact(trans: TransactionWithDetails, accountId: Int): Double {
        return when (trans.transCode) {
            "Deposit" -> trans.transAmount
            "Withdrawal" -> -trans.transAmount
            "Transfer" -> {
                // If transferring TO this account, it is positive. TO this account = toTransAmount
                // If transferring FROM this account, it is negative. FROM this account = -transAmount
                if (trans.toAccountId == accountId) {
                    trans.toTransAmount ?: trans.transAmount
                } else {
                    -trans.transAmount
                }
            }
            else -> 0.0
        }
    }
}
