package com.seyone22.expensetracker.ui.screen.operations.account

import android.util.Log
import com.seyone22.expensetracker.BaseViewModel
import com.seyone22.expensetracker.data.model.Account
import com.seyone22.expensetracker.data.model.Transaction
import com.seyone22.expensetracker.data.model.TransactionWithDetails
import com.seyone22.expensetracker.data.model.toTransaction
import com.seyone22.expensetracker.data.repository.account.AccountsRepository
import com.seyone22.expensetracker.data.repository.transaction.BalanceResult
import com.seyone22.expensetracker.data.repository.transaction.TransactionsRepository
import com.seyone22.expensetracker.ui.screen.operations.transaction.TransactionDetails
import com.seyone22.expensetracker.ui.screen.operations.transaction.toTransaction
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class AccountDetailViewModel(
    private val accountsRepository: AccountsRepository,
    private val transactionsRepository: TransactionsRepository
) : BaseViewModel() {
    private val _accountDetailUiState = MutableStateFlow(AccountDetailUiState())
    val accountDetailUiState: StateFlow<AccountDetailUiState> = _accountDetailUiState.asStateFlow()

    private val _accountId = MutableStateFlow(-1)
    val accountId: StateFlow<Int> = _accountId.asStateFlow()

    suspend fun setAccountId(id: Int) {
        _accountId.value = id
        refreshAccount()
        getTransactions()
    }

    private suspend fun refreshAccount() {
        val accountId = _accountId.value
        val last7Days = (0..6).map {
            LocalDate.now().minusDays(it.toLong()).format(DateTimeFormatter.ISO_LOCAL_DATE)
        }

        val account = accountsRepository.getAccountStream(accountId).firstOrNull()

        val balanceHistoryForLast7Days = last7Days.reversed().map { date ->
            val balanceForDate = accountsRepository.getAccountBalance(accountId, date).firstOrNull()
            BalanceResult(accountId, balanceForDate?.balance ?: 0.0, date)
        }

        val lastBalance = (balanceHistoryForLast7Days.lastOrNull()?.balance
            ?.plus(account?.initialBalance ?: 0.0)) ?: 0.0

        _accountDetailUiState.update {
            it.copy(
                account = account ?: Account(),
                balance = lastBalance,
                balanceHistory = balanceHistoryForLast7Days
            )
        }
    }

    private suspend fun getTransactions() {
        val accountId = _accountId.value
        val transactions =
            transactionsRepository.getTransactionsFromAccount(accountId).firstOrNull()
                ?: emptyList()

        _accountDetailUiState.update { it.copy(transactions = transactions) }
            Log.d("DEBUG", "getTransactions: AccountId is $accountId")
    }

    suspend fun deleteTransaction(transaction: Transaction): Boolean {
        return try {
            transactionsRepository.deleteTransaction(transaction)
            Log.d("TAG", "deleteTransaction: pass ")
            true
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("TAG", "deleteTransaction: fail")
            false
        }
    }

    suspend fun editTransaction(transactionDetails: TransactionDetails): Boolean {
        return try {
            transactionsRepository.updateTransaction(transactionDetails.toTransaction())
            Log.d("TAG", "editTransaction: pass ")
            true
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("TAG", "editTransaction: fail")
            false
        }
    }

    suspend fun editAccount(accountDetails: AccountDetails): Boolean {
        return try {
            accountsRepository.updateAccount(accountDetails.toAccount())
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun deleteAccount(
        account: Account, transactions: List<TransactionWithDetails>
    ): Boolean {
        return try {
            accountsRepository.deleteAccount(account)
            transactions.forEach {
                transactionsRepository.deleteTransaction(it.toTransaction())
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}

data class AccountDetailUiState(
    val account: Account = Account(),
    val balance: Double = 0.0,
    val balanceHistory: List<BalanceResult> = emptyList(),
    val transactions: List<TransactionWithDetails> = emptyList()
)
