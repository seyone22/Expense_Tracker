package com.example.expensetracker.data.account

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.expensetracker.data.transaction.BalanceResult
import com.example.expensetracker.data.transaction.TransactionDao
import com.example.expensetracker.model.Account
import kotlinx.coroutines.flow.Flow

@Dao
interface AccountDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(account: Account)
    @Update
    suspend fun update(account: Account)
    @Delete
    suspend fun delete(account: Account)

    @Query("SELECT * FROM ACCOUNTLIST_V1 WHERE accountId = :accountId")
    fun getAccount(accountId: Int): Flow<Account>
    @Query("SELECT * FROM ACCOUNTLIST_V1 ORDER BY accountName ASC")
    fun getAllAccounts(): Flow<List<Account>>
    @Query("SELECT * FROM ACCOUNTLIST_V1 WHERE status = 'Open' ORDER BY accountName ASC")
    fun getAllActiveAccounts(): Flow<List<Account>>
    @Query("SELECT * FROM ACCOUNTLIST_V1 WHERE accountType = :accountType")
    fun getAllAccountsByType(accountType: String): Flow<List<Account>>

    @Query("SELECT " +
            "    accountId, " +
            "    SUM(CASE " +
            "        WHEN transCode = 'Deposit' AND accountId = :accountId THEN transAmount" +
            "        WHEN transCode = 'Withdrawal' AND accountId = :accountId THEN -transAmount" +
            "        WHEN transCode = 'Transfer' AND accountId = :accountId THEN -transAmount " +
            "        WHEN transCode = 'Transfer' AND toAccountId = :accountId THEN transAmount " +
            "        ELSE 0 " +
            "    END) AS balance " +
            "FROM CHECKINGACCOUNT_V1 " +
            "GROUP BY accountId")
    fun getAccountBalance(accountId: Int): Flow<BalanceResult>
}