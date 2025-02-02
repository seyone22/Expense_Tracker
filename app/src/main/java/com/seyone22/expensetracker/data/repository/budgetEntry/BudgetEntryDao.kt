package com.seyone22.expensetracker.data.repository.budgetEntry

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.seyone22.expensetracker.data.model.BudgetEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface BudgetEntryDao {
    @Insert
    suspend fun insert(entry: BudgetEntry)

    @Update
    suspend fun update(entry: BudgetEntry)

    @Delete
    suspend fun delete(entry: BudgetEntry)

    @Query("SELECT * FROM BUDGETTABLE_V1 WHERE active = 1")
    fun getActiveBudgetEntries(): Flow<List<BudgetEntry>>

    @Query("SELECT * FROM BUDGETTABLE_V1 WHERE active = 1 AND budgetEntryId = :budgetYearId")
    fun getActiveBudgetEntriesForBudgetYearId(budgetYearId: Int): Flow<List<BudgetEntry>>

    @Query("SELECT * FROM BUDGETTABLE_V1 WHERE active = 1 AND budgetEntryId = :budgetEntryId")
    fun getBudgetEntryById(budgetEntryId: Int): Flow<BudgetEntry>


}
