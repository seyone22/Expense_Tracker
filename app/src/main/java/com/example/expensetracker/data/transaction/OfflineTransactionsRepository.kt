package com.example.expensetracker.data.transaction

import com.example.expensetracker.model.Transaction
import kotlinx.coroutines.flow.Flow

class OfflineTransactionsRepository(private val transactionDao: TransactionDao) : TransactionsRepository {
    override fun getAllTransactionsStream(): Flow<List<Transaction>> = transactionDao.getAllTransactions()
    override fun getTransactionStream(transId: Int): Flow<Transaction?> = transactionDao.getTransaction(transId)
    override fun getTransactionsFromAccount(accountId: Int): List<Transaction> = transactionDao.getAllTransactionsByAccount(accountId)
    override fun getAllTransactionsByToAccount(toAccountId: Int): List<Transaction> = transactionDao.getAllTransactionsByToAccount(toAccountId)
    override suspend fun insertTransaction(transaction: Transaction) = transactionDao.insert(transaction)
    override suspend fun deleteTransaction(transaction: Transaction) = transactionDao.delete(transaction)
    override suspend fun updateTransaction(transaction: Transaction) = transactionDao.update(transaction)
}