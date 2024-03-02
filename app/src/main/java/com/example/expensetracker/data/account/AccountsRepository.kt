package com.example.expensetracker.data.account

import com.example.expensetracker.data.transaction.TransactionDao
import com.example.expensetracker.model.Account
import kotlinx.coroutines.flow.Flow

interface AccountsRepository {
    fun getAllAccountsStream(): Flow<List<Account>>
    fun getAllActiveAccountsStream() : Flow<List<Account>>
    fun getAccountStream(accountId: Int): Flow<Account?>
    fun getAccountsFromTypeStream(accountType: String): Flow<List<Account>>
    fun getAccountBalance(accountId: Int): Flow<TransactionDao.BalanceResult>

    suspend fun insertAccount(account: Account)
    suspend fun deleteAccount(account: Account)
    suspend fun updateAccount(account: Account)
}