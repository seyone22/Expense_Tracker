package com.example.expensetracker

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expensetracker.data.AccountsRepository
import com.example.expensetracker.model.Account
import com.example.expensetracker.ui.account.AccountUiState
import com.example.expensetracker.ui.account.toAccount
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/**
 * ViewModel to retrieve all items in the Room database.
 */
class AccountViewModel(private val accountsRepository: AccountsRepository) : ViewModel() {

    /**
     * Holds home ui state. The list of items are retrieved from [AccountsRepository] and mapped to
     * [AccountUiState]
     */
    val accountsUiState: StateFlow<AccountsUiState> =
        accountsRepository.getAllAccountsStream().map { AccountsUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = AccountsUiState()
            )

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

    suspend fun getAccountsByType(type : String) : Flow<List<Account>> {
        Log.d("DEBUG", "getAccountsByType: Called!")

        return accountsRepository.getAccountsFromTypeStream(type)
    }
}

/**
 * Ui State for HomeScreen
 */
data class AccountsUiState(val accountList: List<Account> = listOf())
