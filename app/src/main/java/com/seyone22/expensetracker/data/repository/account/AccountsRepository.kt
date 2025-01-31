package com.seyone22.expensetracker.data.repository.account

import com.seyone22.expensetracker.data.model.Account
import com.seyone22.expensetracker.data.repository.transaction.BalanceResult
import kotlinx.coroutines.flow.Flow

interface AccountsRepository {
    fun getAllAccountsStream(): Flow<List<Account>>
    fun getAllActiveAccountsStream(): Flow<List<Account>>
    fun getAccountStream(accountId: Int): Flow<Account?>
    fun getAccountsFromTypeStream(accountType: String): Flow<List<Account>>
    fun getAccountBalance(accountId: Int, date: String?): Flow<BalanceResult>

    suspend fun insertAccount(account: Account)
    suspend fun deleteAccount(account: Account)
    suspend fun updateAccount(account: Account)
}