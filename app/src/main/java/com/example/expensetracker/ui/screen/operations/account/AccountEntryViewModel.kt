package com.example.expensetracker.ui.screen.operations.account

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expensetracker.data.account.AccountsRepository
import com.example.expensetracker.data.currencyFormat.CurrencyFormatsRepository
import com.example.expensetracker.model.Account
import com.example.expensetracker.model.AccountTypes
import com.example.expensetracker.ui.screen.onboarding.CurrencyList
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.Locale

class AccountEntryViewModel(
    private val accountsRepository: AccountsRepository,
    private val currencyFormatsRepository: CurrencyFormatsRepository
) : ViewModel() {
    val currencyList: StateFlow<CurrencyList> =
        currencyFormatsRepository.getAllCurrencyFormatsStream()
            .map { currencies ->
                CurrencyList(
                    currenciesList = currencies
                )
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = CurrencyList()
            )


    var accountUiState by mutableStateOf(AccountUiState())
        private set

    fun updateUiState(accountDetails: AccountDetails) {
        accountUiState =
            AccountUiState(accountDetails = accountDetails, isEntryValid = validateInput(accountDetails))
    }

    suspend fun saveAccount() {
        Log.d("DEBUG", "saveAccount: Called!")
        if(validateInput()) {
            Log.d("DEBUG", "saveAccount: Input Valid!")
            accountsRepository.insertAccount(accountUiState.accountDetails.toAccount())
        }
    }

    private fun validateInput(uiState: AccountDetails = accountUiState.accountDetails): Boolean {
        Log.d("DEBUG", "validateInput: Validation Begins!")
        Log.d("DEBUG", uiState.accountName)
        return with(uiState) {
            accountName.isNotBlank() && (initialDate?.isNotBlank() ?: false)
        }
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}

//Data class for AccountUiState
data class AccountUiState(
    val accountDetails: AccountDetails = AccountDetails(),
    val isEntryValid: Boolean = false
)
//Data class for AccountDetails
data class AccountDetails(
    val accountId: Int = 0,
    val accountName: String = "",
    val accountType: String = AccountTypes.CHECKING.displayName, //Default value to populate dropdowns
    val accountNum: String? = "",
    val status: String = "",
    val notes: String? = "",
    val heldAt: String? = "",
    val website: String? = "",
    val contactInfo: String? = "",
    val accessInfo: String? = "",
    val initialBalance: String? = "0.0", // Changed the type to String
    val initialDate: String? = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Instant.now().toEpochMilli()).toString(),
    val favoriteAccount: String = "",
    val currencyId: String = "0", // Changed the type to String
    val statementLocked: String? = "", // Changed the type to String
    val statementDate: String? = "",
    val minimumBalance: String? = "", // Changed the type to String
    val creditLimit: String? = "", // Changed the type to String
    val interestRate: String? = "", // Changed the type to String
    val paymentDueDate: String? = "",
    val minimumPayment: String? = "" // Changed the type to String
)


// Extension functions to convert between [Account], [AccountUiState], and [AccountDetails]
fun AccountDetails.toAccount(): Account = Account(
    accountId = accountId,
    accountName = accountName,
    accountType = accountType,
    accountNum = accountNum,
    status = status,
    notes = notes,
    heldAt = heldAt,
    website = website,
    contactInfo = contactInfo,
    accessInfo = accessInfo,
    initialBalance = initialBalance?.toDoubleOrNull() ?: 0.0,
    initialDate = initialDate,
    favoriteAccount = favoriteAccount,
    currencyId = currencyId.toIntOrNull() ?: 0,
    statementLocked = statementLocked?.toIntOrNull() ?: 0,
    statementDate = statementDate,
    minimumBalance = minimumBalance?.toDoubleOrNull() ?: 0.0,
    creditLimit = creditLimit?.toDoubleOrNull() ?: 0.0,
    interestRate = interestRate?.toDoubleOrNull() ?: 0.0,
    paymentDueDate = paymentDueDate,
    minimumPayment = minimumPayment?.toDoubleOrNull() ?: 0.0
)

fun Account.toAccountUiState(isEntryValid: Boolean = false): AccountUiState = AccountUiState(
    accountDetails = this.toAccountDetails(),
    isEntryValid = isEntryValid
)

fun Account.toAccountDetails(): AccountDetails = AccountDetails(
    accountId = accountId,
    accountName = accountName,
    accountType = accountType,
    accountNum = accountNum.toString(),
    status = status,
    notes = notes,
    heldAt = heldAt,
    website = website,
    contactInfo = contactInfo,
    accessInfo = accessInfo,
    initialBalance = initialBalance?.toString(),
    initialDate = initialDate,
    favoriteAccount = favoriteAccount,
    currencyId = currencyId.toString(),
    statementLocked = statementLocked?.toString(),
    statementDate = statementDate,
    minimumBalance = minimumBalance?.toString(),
    creditLimit = creditLimit?.toString(),
    interestRate = interestRate?.toString(),
    paymentDueDate = paymentDueDate,
    minimumPayment = minimumPayment?.toString()
)