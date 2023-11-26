package com.example.expensetracker.data.transaction

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.expensetracker.model.Transaction
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

    @Query("SELECT * FROM CHECKINGACCOUNT_V1 ORDER BY transId ASC")
    fun getAllTransactions(): Flow<List<Transaction>>

    @Query("SELECT * FROM CHECKINGACCOUNT_V1 WHERE accountId = :accountId")
    fun getAllTransactionsByAccount(accountId: Int): Flow<List<Transaction>>

    @Query("SELECT * FROM CHECKINGACCOUNT_V1 WHERE toAccountId = :toAccountId")
    fun getAllTransactionsByToAccount(toAccountId: Int): List<Transaction>

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