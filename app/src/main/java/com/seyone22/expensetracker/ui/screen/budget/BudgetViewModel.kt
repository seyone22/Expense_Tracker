package com.seyone22.expensetracker.ui.screen.budget

import androidx.lifecycle.ViewModel
import com.seyone22.expensetracker.data.model.Category
import com.seyone22.expensetracker.data.repository.category.CategoriesRepository
import com.seyone22.expensetracker.data.repository.payee.PayeesRepository
import com.seyone22.expensetracker.data.repository.transaction.TransactionsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

/**
 * ViewModel to retrieve all items in the Room database.
 */
class BudgetViewModel(
    private val transactionsRepository: TransactionsRepository,
    private val categoriesRepository: CategoriesRepository,
    private val payeesRepository: PayeesRepository,
) : ViewModel() {
    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

    private val categoriesParentFlow: Flow<List<Category>> =
        categoriesRepository.getAllParentCategories()
    private val categoriesSubFlow: Flow<List<Category>> = categoriesRepository.getAllSubCategories()

    // Combine the flows
    val budgetUiState: Flow<BudgetUiState> = combine(
        categoriesParentFlow,
        categoriesSubFlow,
    ) { categoriesParent, categoriesSub ->
        BudgetUiState(categoriesParent, categoriesSub)
    }
}

//Data class for BudgetUiState
data class BudgetUiState(
    val categoriesParent: List<Category> = listOf(),
    val categoriesSub: List<Category> = listOf(),
)