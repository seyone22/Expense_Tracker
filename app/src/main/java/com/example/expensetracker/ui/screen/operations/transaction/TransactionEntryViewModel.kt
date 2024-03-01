package com.example.expensetracker.ui.screen.operations.transaction

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expensetracker.data.account.AccountsRepository
import com.example.expensetracker.data.category.CategoriesRepository
import com.example.expensetracker.data.payee.PayeesRepository
import com.example.expensetracker.data.transaction.TransactionsRepository
import com.example.expensetracker.model.Account
import com.example.expensetracker.model.Category
import com.example.expensetracker.model.Payee
import com.example.expensetracker.model.Transaction
import com.example.expensetracker.model.TransactionCode
import com.example.expensetracker.model.TransactionStatus
import com.example.expensetracker.ui.screen.accounts.AccountsUiState
import com.example.expensetracker.ui.screen.operations.entity.payee.PayeeDetails
import com.example.expensetracker.ui.screen.operations.entity.payee.PayeeUiState
import com.example.expensetracker.ui.screen.operations.entity.payee.toPayee
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.Locale

class TransactionEntryViewModel(
    private val transactionsRepository: TransactionsRepository,
    private val accountsRepository: AccountsRepository,
    private val payeesRepository: PayeesRepository,
    private val categoriesRepository: CategoriesRepository
) :
    ViewModel() {
    var transactionUiState by mutableStateOf(TransactionUiState())
        private set
    var transactionUiState2 by mutableStateOf(TransactionUiState())
        private set

    val transactionUiState1: StateFlow<TransactionUiState> =
        categoriesRepository.getAllCategoriesStream()
            //.onEach { Log.d("DEBUG", ": flow emitted $it") }
            .map { categories ->
                TransactionUiState(
                    categoriesList = categories
                )
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = TransactionUiState()
            )

    init {
        viewModelScope.launch {
            accountsRepository.getAllActiveAccountsStream().map {
                Log.d("DEBUG", ": Value is:  $it")
                TransactionUiState(TransactionDetails(),false,it)
            }
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
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
        accountsRepository.getAllAccountsStream().collect {
            transactionUiState =
                TransactionUiState(
                    transactionDetails = transactionUiState.transactionDetails,
                    isEntryValid = transactionUiState.isEntryValid,
                    accountsList = it
                )
        }
    }
    suspend fun getAllPayees()  {
        payeesRepository.getAllPayeesStream().collect {
            transactionUiState2 =
                TransactionUiState(
                    transactionDetails = transactionUiState2.transactionDetails,
                    isEntryValid = transactionUiState2.isEntryValid,
                    payeesList = it
                )
        }
    }

    suspend fun saveTransaction() {
        if (validateInput()) {
            transactionsRepository.insertTransaction(transactionUiState.transactionDetails.toTransaction())
        }
    }

    private fun validateInput(uiState: TransactionDetails = transactionUiState.transactionDetails): Boolean {
        Log.d("DEBUG", "validateInput: Validation Begins!")
        Log.d("DEBUG", uiState.transactionNumber)
        return with(uiState) {
            transAmount.isNotBlank() && transDate.isNotBlank()&& accountId.isNotBlank() && categoryId.isNotBlank()
        }
    }

    var payeeUiState by mutableStateOf(PayeeUiState())
        private set
    suspend fun savePayee() {
        if(validatePayeeInput()) {
            payeesRepository.insertPayee(payeeUiState.payeeDetails.toPayee())
        }
    }
    private fun validatePayeeInput(uiState: PayeeDetails = payeeUiState.payeeDetails): Boolean {
        return with(uiState) {
            payeeName.isNotBlank()
        }
    }
    fun updatePayeeState(payeeDetails: PayeeDetails) {
        payeeUiState =
            PayeeUiState(payeeDetails = payeeDetails, isEntryValid = validatePayeeInput(payeeDetails))
    }


}

//Data class for AccountUiState
data class TransactionUiState(
    val transactionDetails: TransactionDetails = TransactionDetails(),
    val isEntryValid: Boolean = false,
    val accountsList : List<Account> = listOf(),
    val categoriesList: List<Category> = listOf(),
    val payeesList: List<Payee> = listOf(),
)

//Data class for AccountDetails
data class TransactionDetails(
    val transId: Int = 0,
    val accountId: String = "",
    val toAccountId: String = "-1",
    val payeeId: String = "-1",
    val transCode: String = TransactionCode.WITHDRAWAL.displayName,
    val transAmount: String = "",
    val status: String = TransactionStatus.U.displayName,
    val transactionNumber: String = "0",
    val notes: String = "",
    val categoryId: String = "",
    val transDate: String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Instant.now().toEpochMilli()).toString(),
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
