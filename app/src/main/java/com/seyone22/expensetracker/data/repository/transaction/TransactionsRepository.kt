package com.seyone22.expensetracker.data.repository.transaction

import com.seyone22.expensetracker.data.model.Transaction
import com.seyone22.expensetracker.data.model.TransactionWithDetails
import kotlinx.coroutines.flow.Flow

interface TransactionsRepository {
    fun getAllTransactionsStream(): Flow<List<TransactionWithDetails>>
    fun getTransactionStream(transId: Int): Flow<Transaction?>
    fun getTransactionsFromAccount(accountId: Int): Flow<List<TransactionWithDetails>>
    fun getAllTransactionsByToAccount(toAccountId: Int): List<Transaction>
    fun getAllTransactionsByCode(transCode: String): Flow<List<Transaction>>
    fun getAllTransactionsByCategory(
        categoryId: Int, startDate: String? = null, endDate: String? = null
    ): Flow<List<Transaction>>

    fun getAllTransactionsByCategoryName(
        categName: String, startDate: String? = null, endDate: String? = null
    ): Flow<List<Transaction>>

    fun getAllTransactionsByPayee(
        payeeId: String, startDate: String? = null, endDate: String? = null
    ): Flow<List<Transaction>>

    // Fetches the total expenses for a specific week number
    fun getTotalExpensesForWeek(weekNumber: Int): Flow<List<ExpensePerDay>>


    fun getBalanceByAccountId(): Flow<List<BalanceResult>>
    fun getTotalBalanceByCode(transactionCode: String, status: String = "Reconciled"): Flow<Double>
    fun getTotalBalanceByCodeAndDate(
        transactionCode: String, status: String = "Reconciled", month: Int, year: Int
    ): Flow<Double>

    fun getTotalBalanceByCategoryAndDate(
        categId: Int, status: String = "Reconciled", month: Int?, year: Int
    ): Flow<Double>


    fun getTotalBalance(status: String = "Reconciled"): Flow<Double>
    fun getTotalBalanceByDate(status: String = "Reconciled", month: Int, year: Int): Flow<Double>

    fun getExpensesForDateRange(startDate: String, endDate: String): Flow<List<BalanceResult>>

    suspend fun insertTransaction(transaction: Transaction)
    suspend fun deleteTransaction(transaction: Transaction)
    suspend fun updateTransaction(transaction: Transaction)
}