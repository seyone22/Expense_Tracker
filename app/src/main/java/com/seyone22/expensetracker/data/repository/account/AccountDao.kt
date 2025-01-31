package com.seyone22.expensetracker.data.repository.account

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.seyone22.expensetracker.data.model.Account
import com.seyone22.expensetracker.data.repository.transaction.BalanceResult
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

    @Query(
        """
    SELECT 
        accountId, 
        SUM(balanceChange) AS balance 
    FROM ( 
        SELECT 
            accountId, 
            SUM(CASE 
                    WHEN transCode = 'Deposit' THEN transAmount 
                    WHEN transCode = 'Withdrawal' OR transCode = 'Transfer' THEN -transAmount 
                    ELSE 0 
                END) AS balanceChange 
        FROM CHECKINGACCOUNT_V1 
        WHERE DATE(transDate) <= COALESCE(:date, DATE('now'))  -- Use provided date or current date
        GROUP BY accountId 

        UNION ALL 

        SELECT 
            toAccountId AS accountId, 
            SUM(transAmount) AS balanceChange 
        FROM CHECKINGACCOUNT_V1 
        WHERE transCode = 'Transfer' 
          AND DATE(transDate) <= COALESCE(:date, DATE('now')) -- Use provided date or current date
        GROUP BY toAccountId 
    ) AS subquery 
    WHERE accountId = :accountId 
    GROUP BY accountId
    """
    )
    fun getAccountBalance(accountId: Int, date: String? = null): Flow<BalanceResult>
}