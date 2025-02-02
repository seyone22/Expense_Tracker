package com.seyone22.expensetracker.ui.screen.budget

import androidx.lifecycle.ViewModel
import com.seyone22.expensetracker.data.model.BudgetEntry
import com.seyone22.expensetracker.data.model.BudgetYear
import com.seyone22.expensetracker.data.model.Category
import com.seyone22.expensetracker.data.model.TransactionWithDetails
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

    private val categoriesFlow: Flow<List<Category>> =
        categoriesRepository.getAllCategoriesStream()
    private val transactionsFlow: Flow<List<TransactionWithDetails>> =
        transactionsRepository.getAllTransactionsStream()

    // Combine the flows
    val budgetUiState: Flow<BudgetUiState> = combine(
        categoriesFlow,
        transactionsFlow
    ) { categories, transactions ->
        BudgetUiState(categories, transactions)
    }
}

//Data class for BudgetUiState
data class BudgetUiState(
    val categories: List<Category> = listOf(),
    val transactions: List<TransactionWithDetails> = listOf(),
    val budgetYears: List<BudgetYear> = listOf(),
    val budgetEntries: List<BudgetEntry> = listOf(),
)