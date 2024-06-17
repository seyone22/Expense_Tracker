package com.example.expensetracker.data.repository.transaction

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.expensetracker.data.model.Transaction
import com.example.expensetracker.data.model.TransactionWithDetails
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface TransactionDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(transaction: Transaction)

    @Update
    suspend fun update(transaction: Transaction)

    @Delete
    suspend fun delete(transaction: Transaction)

    @Query("SELECT * FROM CHECKINGACCOUNT_V1 WHERE transId = :transId")
    fun getTransaction(transId: Int): Flow<Transaction>

    // DOES NOT ACCOUNT FOR CUSTOM CATEGORIES
    @Query(
        "SELECT " +
                "    CHECKINGACCOUNT_V1.*, " +
                "    PAYEE_V1.payeeName AS payeeName, " +
                "    CATEGORY_V1.categName AS categName " +
                "FROM " +
                "    CHECKINGACCOUNT_V1 " +
                "LEFT OUTER JOIN " +
                "    PAYEE_V1 ON CHECKINGACCOUNT_V1.payeeId = PAYEE_V1.payeeId " +
                "LEFT OUTER JOIN " +
                "    CATEGORY_V1 ON CHECKINGACCOUNT_V1.categoryId = CATEGORY_V1.categId "
    )
    fun getAllTransactions(): Flow<List<TransactionWithDetails>>

    @Query(
        "SELECT " +
                "    CHECKINGACCOUNT_V1.*, " +
                "    PAYEE_V1.payeeName AS payeeName, " +
                "    CATEGORY_V1.categName AS categName " +
                "FROM " +
                "    CHECKINGACCOUNT_V1 " +
                "LEFT OUTER JOIN " +
                "    PAYEE_V1 ON CHECKINGACCOUNT_V1.payeeId = PAYEE_V1.payeeId " +
                "INNER JOIN " +
                "    CATEGORY_V1 ON CHECKINGACCOUNT_V1.categoryId = CATEGORY_V1.categId " +
                "WHERE " +
                "    CHECKINGACCOUNT_V1.accountId = :accountId OR CHECKINGACCOUNT_V1.toAccountId = :accountId"
    )
    fun getAllTransactionsByAccount(accountId: Int): Flow<List<TransactionWithDetails>>

    @Query("SELECT * FROM CHECKINGACCOUNT_V1 WHERE toAccountId = :toAccountId")
    fun getAllTransactionsByToAccount(toAccountId: Int): List<Transaction>

    @Query("SELECT * FROM CHECKINGACCOUNT_V1 WHERE transCode = :transCode")
    fun getAllTransactionsByCode(transCode: String): Flow<List<Transaction>>

    @Query("SELECT * FROM CHECKINGACCOUNT_V1 WHERE categoryId = :categoryId AND (transDate BETWEEN :startDate AND :endDate OR :startDate IS NULL OR :endDate IS NULL)")
    fun getAllTransactionsByCategory(
        categoryId: String,
        startDate: String?,
        endDate: String?
    ): Flow<List<Transaction>>

    @Query(
        "SELECT" +
                " transId, accountId, toAccountId, payeeId, transCode, transAmount, status, transactionNumber, notes, categoryId, transDate, lastUpdatedTime, deletedTime, followUpId, toTransAmount, color  " +
                "FROM CHECKINGACCOUNT_V1 " +
                "JOIN CATEGORY_V1 ON CHECKINGACCOUNT_V1.categoryId = CATEGORY_V1.categId " +
                "WHERE :categoryName  = categName AND (transDate BETWEEN :startDate AND :endDate OR :startDate IS NULL OR :endDate IS NULL)"
    )
    fun getAllTransactionsByCategoryName(
        categoryName: String,
        startDate: String?,
        endDate: String?
    ): Flow<List<Transaction>>

    @Query("SELECT * FROM CHECKINGACCOUNT_V1 WHERE payeeId = :payeeId AND (transDate BETWEEN :startDate AND :endDate OR :startDate IS NULL OR :endDate IS NULL)")
    fun getAllTransactionsByPayee(
        payeeId: String,
        startDate: String?,
        endDate: String?
    ): Flow<List<Transaction>>

    @Query(
        "SELECT " +
                "    a.accountId, " +
                "    a.initialBalance + COALESCE(SUM(subquery.balanceChange), 0) AS balance " +
                "FROM ACCOUNTLIST_V1 a " +
                "LEFT JOIN ( " +
                "    SELECT " +
                "        accountId, " +
                "        SUM(CASE WHEN transCode = 'Deposit' THEN transAmount " +
                "                 WHEN transCode = 'Withdrawal' OR transCode = 'Transfer' THEN -transAmount " +
                "                 ELSE 0 END) AS balanceChange " +
                "    FROM CHECKINGACCOUNT_V1 " +
                "    GROUP BY accountId " +
                " " +
                "    UNION ALL " +
                " " +
                "    SELECT " +
                "        toAccountId AS accountId, " +
                "        SUM(transAmount) AS balanceChange " +
                "    FROM CHECKINGACCOUNT_V1 " +
                "    WHERE transCode = 'Transfer' " +
                "    GROUP BY toAccountId " +
                ") AS subquery ON a.accountId = subquery.accountId " +
                "GROUP BY a.accountId, a.initialBalance"
    )
    fun getAllAccountBalances(): Flow<List<BalanceResult>>
    // INCLUDES THE ACCOUNT OPENING VALUE

    @Query(
        "SELECT " +
                "   SUM(transAmount * baseConvRate) AS totalWithdrawal " +
                "FROM CHECKINGACCOUNT_V1 " +
                "JOIN ACCOUNTLIST_V1 ON CHECKINGACCOUNT_V1.accountId = ACCOUNTLIST_V1.accountId " +
                "JOIN CURRENCYFORMATS_V1 ON ACCOUNTLIST_V1.currencyId = CURRENCYFORMATS_V1.currencyId " +
                "WHERE transCode = :transCode AND CHECKINGACCOUNT_V1.status LIKE :status"
    )
    fun getTotalBalanceByCode(transCode: String, status: String): Flow<Double>

    @Query(
        "SELECT " +
                "SUM(CASE WHEN c.transCode = 'Deposit' THEN c.transAmount * cf.baseConvRate " +
                "         WHEN c.transCode = 'Withdrawal' OR c.transCode = 'Transfer' THEN -c.transAmount * cf.baseConvRate " +
                "         ELSE 0 END) AS totalTransactionBalance " +
                "FROM CHECKINGACCOUNT_V1 c " +
                "JOIN ACCOUNTLIST_V1 al ON c.accountId = al.accountId " +
                "JOIN CURRENCYFORMATS_V1 cf ON al.currencyId = cf.currencyId " +
                "WHERE c.status LIKE :status"
    )
    fun getTotalTransactionBalance(status: String): Flow<Double>

    @Query(
        "SELECT SUM(initialBalance) FROM ACCOUNTLIST_V1"
    )
    fun getSumOfInitialBalances(): Flow<Double>
    // TODO: REFACTOR THIS TO INCLUDE STARTING BALANCE
}

data class BalanceResult(
    val accountId: Int,
    val balance: Double
)


fun getTodayDateString(): String {
    val currentDate = LocalDate.now()
    return currentDate.toString()
}

fun getStartOfMonthDateString(): String {
    val firstDayOfMonth = LocalDate.now().withDayOfMonth(1)
    return firstDayOfMonth.toString()
}

fun getStartOfLastMonthDateString(): String {
    val firstDayOfLastMonth = LocalDate.now().minusMonths(1).withDayOfMonth(1)
    return firstDayOfLastMonth.toString()
}

fun getArbitaryEndDateString(): String {
    return "9999-12-31"
}