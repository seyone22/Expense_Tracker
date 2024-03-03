package com.example.expensetracker.data.transaction

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.expensetracker.model.Transaction
import com.example.expensetracker.model.TransactionWithDetails
import kotlinx.coroutines.flow.Flow

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
    @Query("SELECT " +
            "    CHECKINGACCOUNT_V1.*, " +
            "    PAYEE_V1.payeeName AS payeeName, " +
            "    CATEGORY_V1.categName AS categName " +
            "FROM " +
            "    CHECKINGACCOUNT_V1 " +
            "LEFT OUTER JOIN " +
            "    PAYEE_V1 ON CHECKINGACCOUNT_V1.payeeId = PAYEE_V1.payeeId " +
            "LEFT OUTER JOIN " +
            "    CATEGORY_V1 ON CHECKINGACCOUNT_V1.categoryId = CATEGORY_V1.categId ")
    fun getAllTransactions(): Flow<List<TransactionWithDetails>>

    @Query("SELECT " +
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
            "    CHECKINGACCOUNT_V1.accountId = :accountId OR CHECKINGACCOUNT_V1.toAccountId = :accountId")
    fun getAllTransactionsByAccount(accountId: Int): Flow<List<TransactionWithDetails>>

    @Query("SELECT * FROM CHECKINGACCOUNT_V1 WHERE toAccountId = :toAccountId")
    fun getAllTransactionsByToAccount(toAccountId: Int): List<Transaction>

    @Query("SELECT * FROM CHECKINGACCOUNT_V1 WHERE transCode = :transCode")
    fun getAllTransactionsByCode(transCode: String): Flow<List<Transaction>>

    @Query("SELECT " +
            "    accountId, " +
            "    SUM(balanceChange) AS balance " +
            "FROM ( " +
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
            ") AS subquery " +
            "GROUP BY accountId")
    fun getAllAccountBalances(): Flow<List<BalanceResult>>

    @Query("SELECT SUM(transAmount) AS totalWithdrawal FROM CHECKINGACCOUNT_V1 WHERE transCode = :transCode")
    fun getTotalBalanceByCode(transCode : String): Flow<Double>
    @Query("SELECT SUM(transAmount) AS totalWithdrawal FROM CHECKINGACCOUNT_V1")
    fun getTotalBalance(): Flow<Double>

    data class BalanceResult(
        val accountId: Int,
        val balance: Double
    )
}