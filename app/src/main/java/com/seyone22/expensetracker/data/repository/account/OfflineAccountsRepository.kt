package com.seyone22.expensetracker.data.repository.account

import com.seyone22.expensetracker.data.model.Account
import com.seyone22.expensetracker.data.repository.transaction.BalanceResult
import kotlinx.coroutines.flow.Flow

class OfflineAccountsRepository(private val accountDao: AccountDao) : AccountsRepository {
    override fun getAllAccountsStream(): Flow<List<Account>> = accountDao.getAllAccounts()
    override fun getAllActiveAccountsStream(): Flow<List<Account>> =
        accountDao.getAllActiveAccounts()

    override fun getAccountStream(accountId: Int): Flow<Account?> = accountDao.getAccount(accountId)
    override fun getAccountsFromTypeStream(accountType: String): Flow<List<Account>> =
        accountDao.getAllAccountsByType(accountType)

    override fun getAccountBalance(accountId: Int, date: String?): Flow<BalanceResult> =
        accountDao.getAccountBalance(accountId, date)

    override suspend fun insertAccount(account: Account) = accountDao.insert(account)
    override suspend fun deleteAccount(account: Account) {
        accountDao.deleteTransactionsForAccount(account.accountId)
        accountDao.delete(account)
    }
    override suspend fun updateAccount(account: Account) = accountDao.update(account)
}