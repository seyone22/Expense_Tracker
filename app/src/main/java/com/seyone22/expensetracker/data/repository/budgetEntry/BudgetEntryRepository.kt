package com.seyone22.expensetracker.data.repository.budgetEntry

import com.seyone22.expensetracker.data.model.BudgetEntry
import kotlinx.coroutines.flow.Flow

interface BudgetEntryRepository {
    suspend fun insertBudgetEntry(budgetEntry: BudgetEntry)
    suspend fun deleteBudgetEntry(budgetEntry: BudgetEntry)
    suspend fun updateBudgetEntry(budgetEntry: BudgetEntry)

    fun getAllBudgetEntriesStream(): Flow<List<BudgetEntry>>
    fun getBudgetEntryStream(budgetEntryId: Int): Flow<BudgetEntry?>
    fun getBudgetEntriesForBudgetYear(budgetYearId: Int): Flow<List<BudgetEntry>>
}
