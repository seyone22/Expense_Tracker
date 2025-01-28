package com.example.expensetracker.ui.screen.operations.account

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expensetracker.data.model.Account
import com.example.expensetracker.data.model.Transaction
import com.example.expensetracker.data.model.TransactionWithDetails
import com.example.expensetracker.data.model.toTransaction
import com.example.expensetracker.data.repository.account.AccountsRepository
import com.example.expensetracker.data.repository.transaction.BalanceResult
import com.example.expensetracker.data.repository.transaction.TransactionsRepository
import com.example.expensetracker.ui.screen.operations.transaction.TransactionDetails
import com.example.expensetracker.ui.screen.operations.transaction.toTransaction
import com.example.expensetracker.ui.screen.transactions.AccountDetailTransactionUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class AccountDetailViewModel(
    private val accountsRepository: AccountsRepository,
    private val transactionsRepository: TransactionsRepository
) : ViewModel() {
    var accountId: Int = -1

    private val _accountDetailAccountUiState = MutableStateFlow(AccountDetailAccountUiState())
    val accountDetailAccountUiState: StateFlow<AccountDetailAccountUiState> =
        _accountDetailAccountUiState

    private val _accountDetailTransactionUiState =
        MutableStateFlow(AccountDetailTransactionUiState())
    val accountDetailTransactionUiState: StateFlow<AccountDetailTransactionUiState> =
        _accountDetailTransactionUiState

    suspend fun getAccount() {
        // Generate the last 7 days' dates
        val last7Days = (0..6).map {
            LocalDate.now().minusDays(it.toLong()).format(DateTimeFormatter.ISO_LOCAL_DATE)
        }

        // Fetch account
        val account = accountsRepository.getAccountStream(accountId).firstOrNull()

        Log.d("TAG", "getAccount: $account")

        // Fetch balance for each of the last 7 days
        val balanceHistoryForLast7Days = mutableListOf<BalanceResult>()
        for (date in last7Days.reversed()) {
            val balanceForDate = accountsRepository.getAccountBalance(accountId, date).firstOrNull()
            val balance = balanceForDate?.balance ?: 0.0 // Default to 0.0 if balanceForDate is null
            balanceHistoryForLast7Days.add(BalanceResult(accountId, balance, date))
        }

        // Set the account details with the balance history
        val lastBalance =
            (balanceHistoryForLast7Days.lastOrNull()?.balance?.plus(account?.initialBalance ?: 0.0))
                ?: 0.0

        // Emit the updated state to the MutableStateFlow
        _accountDetailAccountUiState.value = AccountDetailAccountUiState(
            account = account ?: Account(),
            balance = lastBalance,
            balanceHistory = balanceHistoryForLast7Days
        )
    }

    fun getTransactions() {
        viewModelScope.launch {
            val transactions =
                transactionsRepository.getTransactionsFromAccount(accountId).firstOrNull()
                    ?: emptyList()

            _accountDetailTransactionUiState.value =
                AccountDetailTransactionUiState(transactions = transactions)
            Log.d("DEBUG", "getTransactions: AccountId is $accountId")
        }
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

data class AccountDetailAccountUiState(
    val account: Account = Account(),
    val balance: Double = 0.0,
    val balanceHistory: List<BalanceResult> = listOf()
)

data class AccountDetailTransactionUiState(
    val transactions: List<Transaction> = listOf()
)
