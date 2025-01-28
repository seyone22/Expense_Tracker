package com.example.expensetracker.ui.screen.operations.transaction

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expensetracker.data.model.Account
import com.example.expensetracker.data.model.BillsDeposits
import com.example.expensetracker.data.model.Category
import com.example.expensetracker.data.model.Payee
import com.example.expensetracker.data.model.Tag
import com.example.expensetracker.data.model.TagLink
import com.example.expensetracker.data.model.Transaction
import com.example.expensetracker.data.model.TransactionCode
import com.example.expensetracker.data.model.TransactionStatus
import com.example.expensetracker.data.model.numericOf
import com.example.expensetracker.data.repository.account.AccountsRepository
import com.example.expensetracker.data.repository.billsDeposit.BillsDepositsRepository
import com.example.expensetracker.data.repository.category.CategoriesRepository
import com.example.expensetracker.data.repository.payee.PayeesRepository
import com.example.expensetracker.data.repository.transaction.TransactionsRepository
import com.example.expensetracker.ui.screen.operations.entity.payee.PayeeDetails
import com.example.expensetracker.ui.screen.operations.entity.payee.PayeeUiState
import com.example.expensetracker.ui.screen.operations.entity.payee.toPayee
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.Locale

class TransactionEntryViewModel(
    private val transactionsRepository: TransactionsRepository,
    private val accountsRepository: AccountsRepository,
    private val payeesRepository: PayeesRepository,
    private val categoriesRepository: CategoriesRepository,
    private val billsDepositsRepository: BillsDepositsRepository
) : ViewModel() {
    private val _transactionUiState = MutableStateFlow(TransactionUiState())
    val transactionUiState: StateFlow<TransactionUiState> get() = _transactionUiState

    init {
        viewModelScope.launch {
            val accounts = accountsRepository.getAllAccountsStream().first()
            val payees = payeesRepository.getAllPayeesStream().first()
            val categories = categoriesRepository.getAllCategoriesStream().first()
            //val tags = tagsRepository.getAllTagsStream().first()

            _transactionUiState.value = TransactionUiState(
                accountsList = accounts,
                payeesList = payees,
                categoriesList = categories,
            )
        }
    }

    companion object {
        const val TIMEOUT_MILLIS = 5_000L
    }

    fun updateUiState(
        transactionDetails: TransactionDetails? = null,
        billsDepositsDetails: BillsDepositsDetails? = null
    ) {
        _transactionUiState.value =
            _transactionUiState.value.copy(transactionDetails = transactionDetails
                ?: _transactionUiState.value.transactionDetails,
                billsDepositsDetails = billsDepositsDetails
                    ?: _transactionUiState.value.billsDepositsDetails,
                isEntryValid = transactionDetails?.let { validateInput(it) }
                    ?: _transactionUiState.value.isEntryValid,
                isRecurringEntryValid = billsDepositsDetails?.let { validateRecurringInput(it) }
                    ?: _transactionUiState.value.isRecurringEntryValid)
    }

    suspend fun saveTransaction() {
        if (validateInput()) {
            transactionsRepository.insertTransaction(transactionUiState.value.transactionDetails.toTransaction())
        }
    }

    suspend fun saveRecurringTransaction() {
        if (validateInput()) {
            val x = transactionUiState.value.billsDepositsDetails.addTransactionDetails(
                transactionUiState.value.transactionDetails
            )
            x.REPEATS = numericOf(x.REPEATS).toString()
            billsDepositsRepository.insertBillsDeposit(x.toBillsDeposits())


        }
    }

    private fun validateInput(uiState: TransactionDetails = transactionUiState.value.transactionDetails): Boolean {
        return with(uiState) {
            transAmount.isNotBlank() && transDate.isNotBlank() && accountId.isNotBlank() && categoryId.isNotBlank()
        }
    }

    private fun validateRecurringInput(uiState: BillsDepositsDetails = transactionUiState.value.billsDepositsDetails): Boolean {
        return with(uiState) {
            NEXTOCCURRENCEDATE.isNotBlank() && REPEATS.isNotBlank() && NUMOCCURRENCES.isNotBlank()
        }
    }

    var payeeUiState by mutableStateOf(PayeeUiState())
        private set

    suspend fun savePayee() {
        if (validatePayeeInput()) {
            payeesRepository.insertPayee(payeeUiState.payeeDetails.toPayee())
        }
    }

    private fun validatePayeeInput(uiState: PayeeDetails = payeeUiState.payeeDetails): Boolean {
        return with(uiState) {
            payeeName.isNotBlank()
        }
    }

    fun updatePayeeState(payeeDetails: PayeeDetails) {
        payeeUiState = PayeeUiState(
            payeeDetails = payeeDetails, isEntryValid = validatePayeeInput(payeeDetails)
        )
    }

    //Get Account, Payee, Category
    suspend fun getAccount(accountId: Int): Account {
        return accountsRepository.getAccountStream(accountId).first() ?: Account()
    }

    suspend fun getPayee(payeeId: Int): Payee {
        return payeesRepository.getPayeeStream(payeeId).first() ?: Payee()
    }

    suspend fun getCategory(categoryId: Int): Category {
        return categoriesRepository.getCategoriesStream(categoryId).first() ?: Category()
    }
}

//Data class for AccountUiState
data class TransactionUiState(
    val transactionDetails: TransactionDetails = TransactionDetails(),
    val billsDepositsDetails: BillsDepositsDetails = BillsDepositsDetails(),
    val isEntryValid: Boolean = false,
    val isRecurringEntryValid: Boolean = false,
    val accountsList: List<Account> = listOf(),
    val categoriesList: List<Category> = listOf(),
    val payeesList: List<Payee> = listOf(),
    val tagList: List<Tag> = listOf(),
    val tagLinkList: List<TagLink> = listOf()
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
    val transDate: String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(
        Instant.now().toEpochMilli()
    ).toString(),
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
        transactionDetails = this.toTransactionDetails(), isEntryValid = isEntryValid
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


// Helpers for BillsDeposits

//Data class for BillsDepositsDetails
data class BillsDepositsDetails(
    val BDID: Int = 0,
    val ACCOUNTID: String = "",
    val TOACCOUNTID: String = "",
    val PAYEEID: String = "0",
    val TRANSCODE: String = "", // Withdrawal, Deposit, Transfer
    val TRANSAMOUNT: String = "",
    val STATUS: String = "", // None, Reconciled, Void, Follow up, Duplicate
    val TRANSACTIONNUMBER: String = "",
    val NOTES: String = "",
    val CATEGID: String = "",
    val TRANSDATE: String = "",
    val FOLLOWUPID: String = "",
    val TOTRANSAMOUNT: String = "",
    var REPEATS: String = "",
    val NEXTOCCURRENCEDATE: String = "",
    val NUMOCCURRENCES: String = "",
    val COLOR: String = "-1"
)

// Extension functions to convert between [Transaction], [TransactionUiState], and [TransactionDetails]
fun BillsDepositsDetails.toBillsDeposits(): BillsDeposits = BillsDeposits(
    BDID = BDID,
    ACCOUNTID = ACCOUNTID.toInt(),
    TOACCOUNTID = TOACCOUNTID.toInt(),
    PAYEEID = PAYEEID.toInt(),
    TRANSCODE = TRANSCODE, // Withdrawal, Deposit, Transfer
    TRANSAMOUNT = TRANSAMOUNT.toDouble(),
    STATUS = STATUS, // None, Reconciled, Void, Follow up, Duplicate
    TRANSACTIONNUMBER = TRANSACTIONNUMBER,
    NOTES = NOTES,
    CATEGID = CATEGID.toInt(),
    TRANSDATE = TRANSDATE,
    FOLLOWUPID = FOLLOWUPID.toInt(),
    TOTRANSAMOUNT = TOTRANSAMOUNT.toDouble(),
    REPEATS = REPEATS.toInt(),
    NEXTOCCURRENCEDATE = NEXTOCCURRENCEDATE,
    NUMOCCURRENCES = NUMOCCURRENCES.toInt(),
    COLOR = COLOR.toInt()
)

fun BillsDeposits.toTransactionUiState(isEntryValid: Boolean = false): TransactionUiState =
    TransactionUiState(
        billsDepositsDetails = this.toBillsDepositsDetails(), isEntryValid = isEntryValid
    )

fun BillsDeposits.toBillsDepositsDetails(): BillsDepositsDetails = BillsDepositsDetails(
    BDID = BDID,
    ACCOUNTID = ACCOUNTID.toString(),
    TOACCOUNTID = TOACCOUNTID.toString(),
    PAYEEID = PAYEEID.toString(),
    TRANSCODE = TRANSCODE, // Withdrawal, Deposit, Transfer
    TRANSAMOUNT = TRANSAMOUNT.toString(),
    STATUS = STATUS ?: "", // None, Reconciled, Void, Follow up, Duplicate
    TRANSACTIONNUMBER = TRANSACTIONNUMBER ?: "",
    NOTES = NOTES ?: "",
    CATEGID = CATEGID.toString(),
    TRANSDATE = TRANSDATE ?: "",
    FOLLOWUPID = FOLLOWUPID.toString(),
    TOTRANSAMOUNT = TOTRANSAMOUNT.toString(),
    REPEATS = REPEATS.toString(),
    NEXTOCCURRENCEDATE = NEXTOCCURRENCEDATE ?: "",
    NUMOCCURRENCES = NUMOCCURRENCES.toString(),
    COLOR = COLOR.toString()
)

fun BillsDepositsDetails.addTransactionDetails(t: TransactionDetails): BillsDepositsDetails =
    BillsDepositsDetails(
        BDID = BDID,
        ACCOUNTID = t.accountId,
        TOACCOUNTID = t.toAccountId,
        PAYEEID = t.payeeId,
        TRANSCODE = t.transCode, // Withdrawal, Deposit, Transfer
        TRANSAMOUNT = t.transAmount,
        STATUS = t.status, // None, Reconciled, Void, Follow up, Duplicate
        TRANSACTIONNUMBER = t.transactionNumber,
        NOTES = t.notes,
        CATEGID = t.categoryId,
        TRANSDATE = t.transDate,
        FOLLOWUPID = t.followUpId,
        TOTRANSAMOUNT = t.toTransAmount,
        //Recurring Stuff
        REPEATS = REPEATS,
        NEXTOCCURRENCEDATE = NEXTOCCURRENCEDATE,
        NUMOCCURRENCES = NUMOCCURRENCES,
        COLOR = COLOR
    )