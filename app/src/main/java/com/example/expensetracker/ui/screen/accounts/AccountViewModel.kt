package com.example.expensetracker.ui.screen.accounts

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expensetracker.data.account.AccountsRepository
import com.example.expensetracker.data.transaction.TransactionsRepository
import com.example.expensetracker.model.Account
import com.example.expensetracker.model.AccountTypes
import com.example.expensetracker.model.TransactionCode
import com.example.expensetracker.model.TransactionStatus
import com.example.expensetracker.ui.screen.operations.account.AccountUiState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/**
 * ViewModel to retrieve all items in the Room database.
 */
class AccountViewModel(
    private val accountsRepository: AccountsRepository,
    private val transactionsRepository: TransactionsRepository
) : ViewModel() {

    /**
     * Holds home ui state. The list of items are retrieved from [AccountsRepository] and mapped to
     * [AccountUiState]
     */
    val accountsUiState: StateFlow<AccountsUiState> =
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
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = AccountsUiState()
            )

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

    private fun calculateBalance(account: Account): Double {
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

    private suspend fun calculateGrandBalance(accounts : List<Account>): Double {
        //Should account for different currincies
        //TODO : Convert to base currency, then calculate
        var grandBalance = 0.0
        accounts.forEach {
            grandBalance += calculateBalance(it)
        }
        return grandBalance
    }

    fun countInType(accountType: AccountTypes, accountList: List<Pair<Account, Double>>) : Int {
        var counter = 0
        accountList.forEach {
            if (it.first.accountType == accountType.displayName) {
                counter++
            }
        }
        return counter
    }
}

/**
 * Ui State for HomeScreen
 */
data class AccountsUiState(
    val accountList: List<Pair<Account, Double>> = emptyList(),
    val grandTotal: Double = 0.0
)
