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
}