package com.example.expensetracker.ui.screen.operations.account

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expensetracker.data.account.AccountsRepository
import com.example.expensetracker.data.transaction.TransactionsRepository
import com.example.expensetracker.model.Account
import com.example.expensetracker.model.Transaction
import com.example.expensetracker.model.TransactionCode
import com.example.expensetracker.model.TransactionStatus
import com.example.expensetracker.model.TransactionWithDetails
import com.example.expensetracker.model.toTransaction
import com.example.expensetracker.ui.screen.operations.transaction.TransactionDetails
import com.example.expensetracker.ui.screen.operations.transaction.TransactionEntryViewModel
import com.example.expensetracker.ui.screen.operations.transaction.toTransaction
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class AccountDetailViewModel(
    private val accountsRepository: AccountsRepository,
    private val transactionsRepository: TransactionsRepository,
): ViewModel() {
    var accountId: Int = -1

    var accountDetailAccountUiState: StateFlow<AccountDetailAccountUiState> =
        accountsRepository.getAccountStream(accountId)
            .map { account ->
                AccountDetailAccountUiState(
                    account = account ?: Account(),
                    balance = transactionsRepository.getBalanceByAccountId().first().find { it.accountId == account?.accountId }?.balance ?: 3.3
                )
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = AccountDetailAccountUiState()
            )

    var accountDetailTransactionUiState: StateFlow<AccountDetailTransactionUiState> =
        transactionsRepository.getTransactionsFromAccount(0)
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

    fun getAccount() {
        accountDetailAccountUiState = accountsRepository.getAccountStream(accountId)
            .map { account ->
                AccountDetailAccountUiState(
                    account = account ?: Account(),
                    balance = transactionsRepository.getBalanceByAccountId().first().find { it.accountId == account?.accountId }?.balance ?: -69.420
                )
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = AccountDetailAccountUiState()
            )
    }

    fun getTransactions() {
        accountDetailTransactionUiState =
            transactionsRepository.getTransactionsFromAccount(accountId)
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
        Log.d("DEBUG", "getTransactions: AccountId is $accountId")
    }


    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

    private fun calculateBalance(account: Account): Double {
        var balance = account.initialBalance ?: 0.0
        var reconciledBalance = 0.0


        val getBalancesThread = Thread {
            var hi = transactionsRepository.getTransactionsFromAccount(account.accountId)
                .map {
                    it.forEach() {
                    when (it.status) {
                        TransactionStatus.R.displayName -> {
                            when (it.transCode) {
                                TransactionCode.DEPOSIT.displayName -> {
                                    balance += it.transAmount
                                    reconciledBalance += it.transAmount
                                }

                                TransactionCode.WITHDRAWAL.displayName -> {
                                    balance -= it.transAmount
                                    reconciledBalance -= it.transAmount
                                }

                                TransactionCode.TRANSFER.displayName -> {
                                    balance -= it.transAmount
                                    reconciledBalance -= it.transAmount
                                }
                            }
                        }

                        TransactionStatus.D.displayName, TransactionStatus.F.displayName, TransactionStatus.U.displayName, TransactionStatus.V.displayName -> {
                            when (it.transCode) {
                                TransactionCode.DEPOSIT.displayName -> {
                                    balance += it.transAmount
                                }

                                TransactionCode.WITHDRAWAL.displayName -> {
                                    balance -= it.transAmount
                                }

                                TransactionCode.TRANSFER.displayName -> {
                                    balance -= it.transAmount
                                }
                            }
                        }
                    }
                }
                }
        }

        val addInboundTransfersThread = Thread {
            var hi = transactionsRepository.getAllTransactionsByToAccount(account.accountId)
                .forEach() {
                    when (it.status) {
                        TransactionStatus.R.displayName -> {
                            when (it.transCode) {
                                TransactionCode.TRANSFER.displayName -> {
                                    balance += it.toTransAmount ?: 0.0
                                }
                            }
                        }

                        TransactionStatus.D.displayName, TransactionStatus.F.displayName, TransactionStatus.U.displayName, TransactionStatus.V.displayName -> {
                            when (it.transCode) {
                                TransactionCode.TRANSFER.displayName -> {
                                    balance += it.toTransAmount ?: 0.0
                                    reconciledBalance += it.toTransAmount ?: 0.0
                                }
                            }
                        }
                    }
                }
        }

        getBalancesThread.start()
        getBalancesThread.join()
        addInboundTransfersThread.start()
        addInboundTransfersThread.join()

        return balance
    }

    suspend fun deleteTransaction(transaction: Transaction) : Boolean {
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

    suspend fun editTransaction(transactionDetails: TransactionDetails) : Boolean {
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

    suspend fun editAccount(accountDetails : AccountDetails) : Boolean {
        return try {
            accountsRepository.updateAccount(accountDetails.toAccount())
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun deleteAccount(account: Account, transactions: List<TransactionWithDetails>) : Boolean {
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
    val balance: Double = 0.0
)
data class AccountDetailTransactionUiState(
    val transactions: List<TransactionWithDetails> = listOf(),
)