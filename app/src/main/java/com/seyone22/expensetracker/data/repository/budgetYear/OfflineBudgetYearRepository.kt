package com.seyone22.expensetracker.data.repository.budgetYear

import com.seyone22.expensetracker.data.model.BudgetYear
import kotlinx.coroutines.flow.Flow

class OfflineBudgetYearRepository(private val budgetYearDao: BudgetYearDao) : BudgetYearRepository {
    override suspend fun insertBudgetYear(budgetYear: BudgetYear) =
        budgetYearDao.insertBudgetYear(budgetYear)

    override suspend fun updateBudgetYear(budgetYear: BudgetYear) =
        budgetYearDao.updateBudgetYear(budgetYear)

    override suspend fun deleteBudgetYear(budgetYear: BudgetYear) =
        budgetYearDao.deleteBudgetYear(budgetYear)

    override fun getAllBudgetYears(): Flow<List<BudgetYear>> =
        budgetYearDao.getAllBudgetYears()

    override fun getBudgetYearById(budgetYearId: Int): Flow<BudgetYear?> =
        budgetYearDao.getBudgetYearById(budgetYearId)

    override fun getBudgetYearByName(budgetYearName: String): Flow<BudgetYear?> =
        budgetYearDao.getBudgetYearByName(budgetYearName)
}
