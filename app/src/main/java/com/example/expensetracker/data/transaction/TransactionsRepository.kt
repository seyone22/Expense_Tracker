package com.example.expensetracker.data.transaction

import com.example.expensetracker.model.Transaction
import kotlinx.coroutines.flow.Flow

interface TransactionsRepository {
    fun getAllTransactionsStream(): Flow<List<Transaction>>
    fun getTransactionStream(transId: Int): Flow<Transaction?>
    fun getTransactionsFromAccount(accountId: Int): Flow<List<Transaction>>
    fun getAllTransactionsByToAccount(toAccountId: Int): List<Transaction>
    fun getBalanceByAccountId() : Flow<List<TransactionDao.BalanceResult>>
    fun getTotalBalanceByCode(transactionCode : String) : Flow<Double>
    fun getTotalBalance() : Flow<Double>

    suspend fun insertTransaction(transaction: Transaction)
    suspend fun deleteTransaction(transaction: Transaction)
    suspend fun updateTransaction(transaction: Transaction)
}