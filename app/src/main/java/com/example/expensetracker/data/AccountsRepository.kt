package com.example.expensetracker.data

import com.example.expensetracker.model.Account
import kotlinx.coroutines.flow.Flow

interface AccountsRepository {
    fun getAllAccountsStream(): Flow<List<Account>>

    fun getAccountStream(accountId: Int): Flow<Account?>

    fun getAccountsFromType(accountType: String): Flow<List<Account>>

    suspend fun insertAccount(account: Account)

    suspend fun deleteAccount(account: Account)

    suspend fun updateAccount(account: Account)
}