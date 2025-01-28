package com.example.expensetracker.data.repository.budgetEntry

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Delete
import com.example.expensetracker.data.model.BudgetEntry

@Dao
interface BudgetEntryDao {
    @Insert
    suspend fun insertBudgetEntry(entry: BudgetEntry)

    @Update
    suspend fun updateBudgetEntry(entry: BudgetEntry)

    @Delete
    suspend fun deleteBudgetEntry(entry: BudgetEntry)

    @Query("SELECT * FROM BUDGETTABLE_V1 WHERE active = 1")
    suspend fun getActiveBudgetEntries(): List<BudgetEntry>
}
