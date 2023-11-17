package com.example.expensetracker.data.account

import com.example.expensetracker.model.Account
import kotlinx.coroutines.flow.Flow

class OfflineAccountsRepository(private val accountDao: AccountDao) : AccountsRepository {
    override fun getAllAccountsStream(): Flow<List<Account>> = accountDao.getAllAccounts()
    override fun getAccountStream(accountId: Int): Flow<Account?> = accountDao.getAccount(accountId)
    override fun getAccountsFromTypeStream(accountType: String): Flow<List<Account>> = accountDao.getAllAccountsByType(accountType)

    override suspend fun insertAccount(account: Account) = accountDao.insert(account)
    override suspend fun deleteAccount(account: Account) = accountDao.delete(account)
    override suspend fun updateAccount(account: Account) = accountDao.update(account)
}