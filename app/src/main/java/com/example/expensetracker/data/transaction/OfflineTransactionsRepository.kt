package com.example.expensetracker.data.transaction

import com.example.expensetracker.model.Transaction
import com.example.expensetracker.model.TransactionWithDetails
import kotlinx.coroutines.flow.Flow

class OfflineTransactionsRepository(private val transactionDao: TransactionDao) : TransactionsRepository {
    override fun getAllTransactionsStream(): Flow<List<TransactionWithDetails>> = transactionDao.getAllTransactions()
    override fun getTransactionStream(transId: Int): Flow<Transaction?> = transactionDao.getTransaction(transId)
    override fun getTransactionsFromAccount(accountId: Int): Flow<List<TransactionWithDetails>> = transactionDao.getAllTransactionsByAccount(accountId)
    override fun getAllTransactionsByToAccount(toAccountId: Int): List<Transaction> = transactionDao.getAllTransactionsByToAccount(toAccountId)
    override fun getAllTransactionsByCode(transCode: String): Flow<List<Transaction>> = transactionDao.getAllTransactionsByCode(transCode)

    override fun getBalanceByAccountId() : Flow<List<TransactionDao.BalanceResult>> = transactionDao.getAllAccountBalances()
    override fun getTotalBalanceByCode(transactionCode : String) : Flow<Double> = transactionDao.getTotalBalanceByCode(transactionCode)
    override fun getTotalBalance() : Flow<Double> = transactionDao.getTotalBalance()


    override suspend fun insertTransaction(transaction: Transaction) = transactionDao.insert(transaction)
    override suspend fun deleteTransaction(transaction: Transaction) = transactionDao.delete(transaction)
    override suspend fun updateTransaction(transaction: Transaction) = transactionDao.update(transaction)
}