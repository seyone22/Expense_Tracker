package com.seyone22.expensetracker.data.repository.budgetYear

import com.seyone22.expensetracker.data.model.BudgetYear
import kotlinx.coroutines.flow.Flow

interface BudgetYearRepository {
    suspend fun insertBudgetYear(budgetYear: BudgetYear)
    suspend fun updateBudgetYear(budgetYear: BudgetYear)
    suspend fun deleteBudgetYear(budgetYear: BudgetYear)

    fun getAllBudgetYears(): Flow<List<BudgetYear>>
    fun getBudgetYearById(budgetYearId: Int): Flow<BudgetYear?>
    fun getBudgetYearByName(budgetYearName: String): Flow<BudgetYear?>
}
