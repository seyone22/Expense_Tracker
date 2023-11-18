package com.example.expensetracker.ui.transaction

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expensetracker.AccountsUiState
import com.example.expensetracker.data.account.AccountsRepository
import com.example.expensetracker.data.transaction.TransactionsRepository
import com.example.expensetracker.model.Account
import com.example.expensetracker.model.Transaction
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TransactionEntryViewModel(
    private val transactionsRepository: TransactionsRepository,
    private val accountsRepository: AccountsRepository
) :
    ViewModel() {
    var transactionUiState by mutableStateOf(TransactionUiState())
        private set

    init {
        Log.d("DEBUG", ": Initialization")
        viewModelScope.launch {
            accountsRepository.getAllActiveAccountsStream().map {
                Log.d("DEBUG", ": Value is:  $it")
                TransactionUiState(TransactionDetails(),false,it)
            }
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(TransactionEntryViewModel.TIMEOUT_MILLIS),
                    initialValue = AccountsUiState()
                )
        }
    }

    companion object {
        const val TIMEOUT_MILLIS = 5_000L
    }

    fun updateUiState(transactionDetails: TransactionDetails) {
        transactionUiState =
            TransactionUiState(
                transactionDetails = transactionDetails,
                isEntryValid = validateInput(transactionDetails),
            )
    }

    suspend fun getAllAccounts()  {
        var x : List<Account> = listOf()
        accountsRepository.getAllAccountsStream().collect {
            transactionUiState =
                TransactionUiState(
                    transactionDetails = transactionUiState.transactionDetails,
                    isEntryValid = transactionUiState.isEntryValid,
                    accountsList = it
                )
            Log.d("DEBUG", "getAllAccounts: Within the collect $it")
        }
        Log.d("DEBUG", "getAllAccounts: x is $x")
    }

    suspend fun saveTransaction() {
        Log.d("DEBUG", "saveTransaction: Called!")
        if (validateInput()) {
            Log.d("DEBUG", "saveTransaction: Input Valid!")
            transactionsRepository.insertTransaction(transactionUiState.transactionDetails.toTransaction())
        }
    }



    private fun validateInput(uiState: TransactionDetails = transactionUiState.transactionDetails): Boolean {
        Log.d("DEBUG", "validateInput: Validation Begins!")
        Log.d("DEBUG", uiState.transactionNumber)
        return with(uiState) {
            transAmount.isNotBlank() && transDate.isNotBlank()
        }
    }

}


//Data class for AccountUiState
data class TransactionUiState(
    val transactionDetails: TransactionDetails = TransactionDetails(),
    val isEntryValid: Boolean = false,
    val accountsList : List<Account> = listOf()
)

//Data class for AccountDetails
data class TransactionDetails(
    val transId: Int = 0,
    val accountId: String = "0",
    val toAccountId: String = "0",
    val payeeId: String = "0",
    val transCode: String = "0",
    val transAmount: String = "",
    val status: String = "0",
    val transactionNumber: String = "0",
    val notes: String = "",
    val categoryId: String = "0",
    val transDate: String = "",
    val lastUpdatedTime: String = "",
    val deletedTime: String = "",
    val followUpId: String = "0",
    val toTransAmount: String = "0",
    val color: String = "-1"
)


// Extension functions to convert between [Transaction], [TransactionUiState], and [TransactionDetails]
fun TransactionDetails.toTransaction(): Transaction = Transaction(
    transId = transId,
    accountId = accountId.toInt(),
    toAccountId = toAccountId.toInt(),
    payeeId = payeeId.toInt(),
    transCode = transCode,
    transAmount = transAmount.toDouble(),
    status = status,
    transactionNumber = transactionNumber,
    notes = notes,
    categoryId = categoryId.toInt(),
    transDate = transDate,
    lastUpdatedTime = lastUpdatedTime,
    deletedTime = deletedTime,
    followUpId = followUpId.toInt(),
    toTransAmount = toTransAmount.toDouble(),
    color = color.toInt()
)

fun Transaction.toTransactionUiState(isEntryValid: Boolean = false): TransactionUiState =
    TransactionUiState(
        transactionDetails = this.toTransactionDetails(),
        isEntryValid = isEntryValid
    )

fun Transaction.toTransactionDetails(): TransactionDetails = TransactionDetails(
    transId = transId,
    accountId = accountId.toString(),
    toAccountId = toAccountId.toString(),
    payeeId = payeeId.toString(),
    transCode = transCode,
    transAmount = transAmount.toString(),
    status = status.toString(),
    transactionNumber = transactionNumber.toString(),
    notes = notes.toString(),
    categoryId = categoryId.toString(),
    transDate = transDate.toString(),
    lastUpdatedTime = lastUpdatedTime.toString(),
    deletedTime = deletedTime.toString(),
    followUpId = followUpId.toString(),
    toTransAmount = toTransAmount.toString(),
    color = color.toString()
)
