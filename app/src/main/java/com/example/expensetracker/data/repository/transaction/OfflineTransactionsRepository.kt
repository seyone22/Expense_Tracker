package com.example.expensetracker.data.repository.transaction

import com.example.expensetracker.data.model.Transaction
import com.example.expensetracker.data.model.TransactionWithDetails
import kotlinx.coroutines.flow.Flow

class OfflineTransactionsRepository(private val transactionDao: TransactionDao) :
    TransactionsRepository {
    override fun getAllTransactionsStream(): Flow<List<TransactionWithDetails>> =
        transactionDao.getAllTransactions()

    override fun getTransactionStream(transId: Int): Flow<Transaction?> =
        transactionDao.getTransaction(transId)

    override fun getTransactionsFromAccount(accountId: Int): Flow<List<TransactionWithDetails>> =
        transactionDao.getAllTransactionsByAccount(accountId)

    override fun getAllTransactionsByToAccount(toAccountId: Int): List<Transaction> =
        transactionDao.getAllTransactionsByToAccount(toAccountId)

    override fun getAllTransactionsByCode(transCode: String): Flow<List<Transaction>> =
        transactionDao.getAllTransactionsByCode(transCode)

    override fun getAllTransactionsByCategory(
        transCode: String,
        startDate: String?,
        endDate: String?
    ): Flow<List<Transaction>> =
        transactionDao.getAllTransactionsByCategory(transCode, startDate, endDate)

    override fun getAllTransactionsByCategoryName(
        categName: String,
        startDate: String?,
        endDate: String?
    ): Flow<List<Transaction>> =
        transactionDao.getAllTransactionsByCategory(categName, startDate, endDate)

    override fun getAllTransactionsByPayee(
        payeeId: String,
        startDate: String?,
        endDate: String?
    ): Flow<List<Transaction>> =
        transactionDao.getAllTransactionsByPayee(payeeId, startDate, endDate)

    override fun getBalanceByAccountId(): Flow<List<BalanceResult>> =
        transactionDao.getAllAccountBalances()

    override fun getTotalBalanceByCode(transactionCode: String, status: String): Flow<Double> =
        transactionDao.getTotalBalanceByCode(transactionCode, status)

    override fun getTotalBalance(status: String): Flow<Double> =
        transactionDao.getTotalBalance(status)


    override suspend fun insertTransaction(transaction: Transaction) =
        transactionDao.insert(transaction)

    override suspend fun deleteTransaction(transaction: Transaction) =
        transactionDao.delete(transaction)

    override suspend fun updateTransaction(transaction: Transaction) =
        transactionDao.update(transaction)
}