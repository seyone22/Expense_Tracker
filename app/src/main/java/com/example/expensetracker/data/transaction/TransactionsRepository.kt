package com.example.expensetracker.data.transaction

import com.example.expensetracker.model.Transaction
import kotlinx.coroutines.flow.Flow

interface TransactionsRepository {
    fun getAllTransactionsStream(): Flow<List<Transaction>>
    fun getTransactionStream(transId: Int): Flow<Transaction?>
    fun getTransactionsFromAccountStream(accountId: Int): Flow<List<Transaction>>

    suspend fun insertAccount(transaction: Transaction)
    suspend fun deleteAccount(transaction: Transaction)
    suspend fun updateAccount(transaction: Transaction)
}