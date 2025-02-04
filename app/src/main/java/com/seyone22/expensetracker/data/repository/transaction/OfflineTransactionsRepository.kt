package com.seyone22.expensetracker.data.repository.transaction

import android.util.Log
import com.seyone22.expensetracker.data.model.Transaction
import com.seyone22.expensetracker.data.model.TransactionWithDetails
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

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
        categoryId: Int, startDate: String?, endDate: String?
    ): Flow<List<Transaction>> =
        transactionDao.getAllTransactionsByCategory(categoryId, startDate, endDate)

    override fun getAllTransactionsByCategoryName(
        categName: String, startDate: String?, endDate: String?
    ): Flow<List<Transaction>> =
        transactionDao.getAllTransactionsByCategory(categName.toInt(), startDate, endDate)

    override fun getAllTransactionsByPayee(
        payeeId: String, startDate: String?, endDate: String?
    ): Flow<List<Transaction>> =
        transactionDao.getAllTransactionsByPayee(payeeId, startDate, endDate)

    override fun getBalanceByAccountId(): Flow<List<BalanceResult>> =
        transactionDao.getAllAccountBalances()

    override fun getTotalBalanceByCode(transactionCode: String, status: String): Flow<Double> =
        transactionDao.getTotalBalanceByCode(transactionCode, status)

    override fun getTotalBalanceByCodeAndDate(
        transactionCode: String,
        status: String,
        month: Int,
        year: Int
    ): Flow<Double> =
        transactionDao.getTotalBalanceByCode(transactionCode, status, month, year)

    override fun getTotalBalanceByCategoryAndDate(
        categId: Int,
        status: String,
        month: Int?,
        year: Int
    ): Flow<Double> =
        transactionDao.getTotalBalanceByCategory(categId, status, month, year)

    override fun getTotalBalance(status: String): Flow<Double> {
        return combine(
            transactionDao.getTotalTransactionBalance(status),
            transactionDao.getSumOfInitialBalances()
        ) { transBal, initBal ->
            transBal + initBal
        }
    }

    override fun getTotalBalanceByDate(status: String, month: Int, year: Int): Flow<Double> {
        return combine(
            transactionDao.getTotalTransactionBalance(status),
            transactionDao.getSumOfInitialBalances()
        ) { transBal, initBal ->
            transBal + initBal
        }
    }


    override fun getTotalExpensesForWeek(weekNumber: Int): Flow<List<ExpensePerDay>> {
        Log.d("TAG", "getTotalExpensesForWeek: $weekNumber")
        return transactionDao.getTotalExpensesForWeek(weekNumber)
    }


    override suspend fun insertTransaction(transaction: Transaction) =
        transactionDao.insert(transaction)

    override suspend fun deleteTransaction(transaction: Transaction) =
        transactionDao.delete(transaction)

    override suspend fun updateTransaction(transaction: Transaction) =
        transactionDao.update(transaction)
}