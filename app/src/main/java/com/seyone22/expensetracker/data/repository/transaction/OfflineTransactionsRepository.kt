package com.seyone22.expensetracker.data.repository.transaction

import android.util.Log
import androidx.sqlite.db.SimpleSQLiteQuery
import com.seyone22.expensetracker.data.model.Transaction
import com.seyone22.expensetracker.data.model.TransactionCode
import com.seyone22.expensetracker.data.model.TransactionWithDetails
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class OfflineTransactionsRepository(private val transactionDao: TransactionDao) :
    TransactionsRepository {
    override fun getAllTransactionsStream(
        sortField: String,
        sortDirection: String
    ): Flow<List<TransactionWithDetails>> {
        val validSortFields =
            listOf("transDate", "amount", "payeeName", "categName") // Allowed columns
        val validSortDirections = listOf("ASC", "DESC")

        val sanitizedSortField = if (sortField in validSortFields) sortField else "transDate"
        val sanitizedSortDirection =
            if (sortDirection in validSortDirections) sortDirection else "ASC"

        val query = SimpleSQLiteQuery(
            """
        SELECT 
            CHECKINGACCOUNT_V1.*,
            PAYEE_V1.payeeName AS payeeName, 
            CATEGORY_V1.categName AS categName 
        FROM CHECKINGACCOUNT_V1 
        LEFT OUTER JOIN PAYEE_V1 
            ON CHECKINGACCOUNT_V1.payeeId = PAYEE_V1.payeeId 
        LEFT OUTER JOIN CATEGORY_V1 
            ON CHECKINGACCOUNT_V1.categoryId = CATEGORY_V1.categId
        ORDER BY $sanitizedSortField $sanitizedSortDirection
        """.trimIndent()
        )

        return transactionDao.getAllTransactions(query)
    }


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
        month: String?,
        year: String
    ): Flow<Double> =
        transactionDao.getTotalBalanceByCode(transactionCode, status, month, year)

    override fun getTotalBalanceByCategoryAndDate(
        categId: Int,
        status: String,
        month: String?,
        year: String
    ): Flow<Double> =
        transactionDao.getTotalBalanceByCategory(categId, status, month, year)

    // TODO: Incorporate Assets, Liabilities, Stocks & Shares
    // Withdrawals are already negative!!
    override fun getTotalBalance(status: String): Flow<Double> {
        return combine(
            transactionDao.getTotalBalanceByCode(TransactionCode.DEPOSIT.displayName, status),
            transactionDao.getTotalBalanceByCode(TransactionCode.WITHDRAWAL.displayName, status),
            transactionDao.getSumOfInitialBalances()
        ) { deposits, withdrawals, initBals ->
            initBals + (deposits + withdrawals)
        }
    }

    override fun getTotalBalanceByDate(status: String, month: String, year: String): Flow<Double> {
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

    override fun getExpensesForDateRange(
        startDate: String,
        endDate: String
    ): Flow<List<BalanceResult>> {
        return transactionDao.getExpensesForDateRange(startDate, endDate)
    }


    override suspend fun insertTransaction(transaction: Transaction) =
        transactionDao.insert(transaction)

    override suspend fun deleteTransaction(transaction: Transaction) =
        transactionDao.delete(transaction)

    override suspend fun updateTransaction(transaction: Transaction) =
        transactionDao.update(transaction)
}