package com.example.expensetracker.data.transaction

import com.example.expensetracker.model.Transaction
import kotlinx.coroutines.flow.Flow

class OfflineTransactionsRepository(private val transactionDao: TransactionDao) : TransactionsRepository {
    override fun getAllTransactionsStream(): Flow<List<Transaction>> = transactionDao.getAllTransactions()
    override fun getTransactionStream(transId: Int): Flow<Transaction?> = transactionDao.getTransaction(transId)
    override fun getTransactionsFromAccountStream(accountId: Int): Flow<List<Transaction>> = transactionDao.getAllTransactionsByAccount(accountId)

    override suspend fun insertAccount(transaction: Transaction) = transactionDao.insert(transaction)
    override suspend fun deleteAccount(transaction: Transaction) = transactionDao.delete(transaction)
    override suspend fun updateAccount(transaction: Transaction) = transactionDao.update(transaction)
}