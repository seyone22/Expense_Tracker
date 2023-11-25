package com.example.expensetracker.ui.screen.operations.account

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expensetracker.data.account.AccountsRepository
import com.example.expensetracker.data.transaction.TransactionsRepository
import com.example.expensetracker.model.Account
import com.example.expensetracker.model.Category
import com.example.expensetracker.model.Payee
import com.example.expensetracker.model.Transaction
import com.example.expensetracker.model.TransactionCode
import com.example.expensetracker.model.TransactionStatus
import com.example.expensetracker.ui.screen.operations.transaction.TransactionDetails
import com.example.expensetracker.ui.screen.operations.transaction.TransactionEntryViewModel
import com.example.expensetracker.ui.screen.operations.transaction.TransactionUiState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.forEach
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlin.math.log

class AccountDetailViewModel(
    private val accountsRepository: AccountsRepository,
    private val transactionsRepository: TransactionsRepository,
): ViewModel() {
    var accountId: Int = -1;

    var accountDetailAccountUiState: StateFlow<AccountDetailAccountUiState> =
        accountsRepository.getAccountStream(accountId)
            .map { account ->
                AccountDetailAccountUiState(account ?: Account())
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(AccountDetailViewModel.TIMEOUT_MILLIS),
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

    suspend fun getAccount(accountId : Int) {
        accountDetailAccountUiState = accountsRepository.getAccountStream(accountId)
            .map { account ->
                AccountDetailAccountUiState(account ?: Account())
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(AccountDetailViewModel.TIMEOUT_MILLIS),
                initialValue = AccountDetailAccountUiState()
            )
    }

    suspend fun getTransactions() {
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

    private suspend fun calculateBalance(account: Account): Double {
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
}

data class AccountDetailAccountUiState(
    val account: Account = Account(),
    val balance: Double = 0.0
)
data class AccountDetailTransactionUiState(
    val transactions: List<Transaction> = listOf(),
)