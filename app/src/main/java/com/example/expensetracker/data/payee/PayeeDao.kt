package com.example.expensetracker.data.payee

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.expensetracker.model.Payee
import kotlinx.coroutines.flow.Flow

@Dao
interface PayeeDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(payee: Payee)
    @Update
    suspend fun update(payee: Payee)
    @Delete
    suspend fun delete(payee: Payee)

    @Query("SELECT * FROM PAYEE_V1 WHERE payeeId = :payeeId")
    fun getPayee(payeeId: Int): Flow<Payee>
    @Query("SELECT * FROM PAYEE_V1 ORDER BY payeeName ASC")
    fun getAllPayees(): Flow<List<Payee>>
    @Query("SELECT * FROM PAYEE_V1 WHERE active = 1 ORDER BY payeeName ASC")
    fun getAllActivePayees(): Flow<List<Payee>>
    @Query("SELECT * FROM PAYEE_V1 WHERE categId = :categId")
    fun getAllPayeesByCategory(categId: Int): Flow<List<Payee>>
}