package com.seyone22.expensetracker.data.repository.budgetEntry

import com.seyone22.expensetracker.data.model.BudgetEntry
import kotlinx.coroutines.flow.Flow

class OfflineBudgetEntryRepository(private val budgetEntryDao: BudgetEntryDao) :
    BudgetEntryRepository {
    override suspend fun insertBudgetEntry(budgetEntry: BudgetEntry) =
        budgetEntryDao.insert(budgetEntry)

    override suspend fun deleteBudgetEntry(budgetEntry: BudgetEntry) =
        budgetEntryDao.delete(budgetEntry)

    override suspend fun updateBudgetEntry(budgetEntry: BudgetEntry) =
        budgetEntryDao.update(budgetEntry)

    override fun getAllBudgetEntriesStream(): Flow<List<BudgetEntry>> =
        budgetEntryDao.getActiveBudgetEntries()

    override fun getBudgetEntryStream(budgetEntryId: Int): Flow<BudgetEntry?> =
        budgetEntryDao.getBudgetEntryById(budgetEntryId)

    override fun getBudgetEntriesForBudgetYear(budgetYearId: Int): Flow<List<BudgetEntry>> =
        budgetEntryDao.getActiveBudgetEntriesForBudgetYearId(budgetYearId)

}