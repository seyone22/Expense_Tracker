package com.example.expensetracker.data.billsDeposit

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Update
import com.example.expensetracker.model.BillsDeposits

@Dao
interface BillsDepositsDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(billsDeposits: BillsDeposits)
    @Update
    suspend fun update(billsDeposits: BillsDeposits)
    @Delete
    suspend fun delete(billsDeposits: BillsDeposits)
}