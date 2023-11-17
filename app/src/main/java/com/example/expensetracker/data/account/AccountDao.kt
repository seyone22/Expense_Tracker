package com.example.expensetracker.data.account

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
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

    @Query("SELECT * FROM ACCOUNTLIST_V1 WHERE accountType = :accountType")
    fun getAllAccountsByType(accountType: String): Flow<List<Account>>
}