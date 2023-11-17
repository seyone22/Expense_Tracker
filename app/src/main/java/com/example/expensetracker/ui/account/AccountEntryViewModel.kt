package com.example.expensetracker.ui.account

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.expensetracker.data.AccountsRepository
import com.example.expensetracker.model.Account

class AccountEntryViewModel(private val accountsRepository: AccountsRepository) : ViewModel() {
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
            accountName.isNotBlank()
        }
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
    val accountType: String = "",
    val accountNum: String? = null,
    val status: String = "",
    val notes: String? = null,
    val heldAt: String? = null,
    val website: String? = null,
    val contactInfo: String? = null,
    val accessInfo: String? = null,
    val initialBalance: String? = null, // Changed the type to String
    val initialDate: String? = null,
    val favoriteAccount: String = "",
    val currencyId: String = "0", // Changed the type to String
    val statementLocked: String? = null, // Changed the type to String
    val statementDate: String? = null,
    val minimumBalance: String? = null, // Changed the type to String
    val creditLimit: String? = null, // Changed the type to String
    val interestRate: String? = null, // Changed the type to String
    val paymentDueDate: String? = null,
    val minimumPayment: String? = null // Changed the type to String
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
    currencyId = currencyId?.toIntOrNull() ?: 0,
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
