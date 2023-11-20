package com.example.expensetracker.ui.account

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expensetracker.data.account.AccountsRepository
import com.example.expensetracker.data.transaction.TransactionsRepository
import com.example.expensetracker.model.Account
import com.example.expensetracker.model.TransactionCode
import com.example.expensetracker.model.TransactionStatus
import com.example.expensetracker.ui.screen.accounts.AccountViewModel
import com.example.expensetracker.ui.screen.accounts.AccountsUiState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class AccountDetailViewModel(
    private val accountsRepository: AccountsRepository,
    private val transactionsRepository: TransactionsRepository,
    savedStateHandle: SavedStateHandle
): ViewModel() {

    private val argument: Int = savedStateHandle.get("accountId") ?: 0
    /**
     * Holds home ui state. The list of items are retrieved from [AccountsRepository] and mapped to
     * [AccountUiState]
     */
    val accountDetailAccountUiState: StateFlow<AccountDetailAccountUiState> =
        accountsRepository.getAccountStream(argument)
            //.onEach { Log.d("DEBUG", ": flow emitted $it") }
            .map { account  ->
                AccountDetailAccountUiState(account ?: Account(), calculateBalance(account ?: Account()))
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(AccountDetailViewModel.TIMEOUT_MILLIS),
                initialValue = AccountDetailAccountUiState()
            )
    /**
     * Holds home ui state. The list of items are retrieved from [AccountsRepository] and mapped to
     * [AccountUiState]
     */
/*    val accountDetailTransactionUiState: StateFlow<AccountDetailTransactionUiState> =
        accountsRepository.getAllAccountsStream()
            //.onEach { Log.d("DEBUG", ": flow emitted $it") }
            .map { accounts ->
                val transformedList = accounts.map { account ->
                    Log.d("DEBUG", ": map value $account")
                    Pair(account, calculateBalance(account))
                }
                AccountsUiState(transformedList, calculateGrandBalance(accounts))
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(AccountViewModel.TIMEOUT_MILLIS),
                initialValue = AccountsUiState()
            )*/


    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

    private suspend fun calculateBalance(account: Account): Double {
        var balance = account.initialBalance ?: 0.0
        var reconciledBalance = 0.0


        val getBalancesThread = Thread {
            var hi = transactionsRepository.getTransactionsFromAccount(account.accountId)
                .forEach() {
                    Log.d("DEBUG", "calculateBalance.withinThread: $it")
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

        val addInboundTransfersThread = Thread {
            var hi = transactionsRepository.getAllTransactionsByToAccount(account.accountId)
                .forEach() {
                    Log.d("DEBUG", "calculateBalance.withinThread: $it")
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
    val transactions: List<Pair<Account, Double>> = emptyList(),
)