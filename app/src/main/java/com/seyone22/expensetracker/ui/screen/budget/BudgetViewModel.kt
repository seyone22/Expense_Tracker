package com.seyone22.expensetracker.ui.screen.budget

import androidx.lifecycle.viewModelScope
import com.seyone22.expensetracker.BaseViewModel
import com.seyone22.expensetracker.data.model.BudgetEntry
import com.seyone22.expensetracker.data.model.BudgetYear
import com.seyone22.expensetracker.data.model.Category
import com.seyone22.expensetracker.data.model.TransactionWithDetails
import com.seyone22.expensetracker.data.repository.budgetEntry.BudgetEntryRepository
import com.seyone22.expensetracker.data.repository.budgetYear.BudgetYearRepository
import com.seyone22.expensetracker.data.repository.category.CategoriesRepository
import com.seyone22.expensetracker.data.repository.payee.PayeesRepository
import com.seyone22.expensetracker.data.repository.transaction.TransactionsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

/**
 * ViewModel to retrieve all items in the Room database.
 */
class BudgetViewModel(
    private val transactionsRepository: TransactionsRepository,
    private val categoriesRepository: CategoriesRepository,
    private val payeesRepository: PayeesRepository,
    private val budgetEntryRepository: BudgetEntryRepository,
    private val budgetYearRepository: BudgetYearRepository
) : BaseViewModel() {
    // Flow for categories and transactions
    private val categoriesFlow: Flow<List<Category>> = categoriesRepository.getAllCategoriesStream()
    private val transactionsFlow: Flow<List<TransactionWithDetails>> =
        transactionsRepository.getAllTransactionsStream()
    private val budgetYearsFlow: Flow<List<BudgetYear>> = budgetYearRepository.getAllBudgetYears()

    // Mutable StateFlow to store budget entries
    private val _budgetEntriesFlow = MutableStateFlow<List<BudgetEntry>>(emptyList())
    val budgetEntriesFlow: StateFlow<List<BudgetEntry>> = _budgetEntriesFlow

    // Combine the flows
    val budgetUiState: Flow<BudgetUiState> = combine(
        categoriesFlow,
        transactionsFlow,
        budgetEntriesFlow,
        budgetYearsFlow,
    ) { categories, transactions, budgetEntries, budgetYears ->
        // Return a new BudgetUiState combining all data
        BudgetUiState(categories, transactions, budgetEntries, budgetYears)
    }

    // Function to fetch Budget Entries for a specific Budget Year
    fun fetchBudgetEntriesFor(budgetYearId: Int) {
        // Fetch the budget entries from the repository and update the StateFlow
        viewModelScope.launch {
            val entries = budgetEntryRepository.getBudgetEntriesForBudgetYear(budgetYearId)
            _budgetEntriesFlow.value = entries.firstOrNull() ?: listOf()
        }
    }

    suspend fun addBudgetYear(year: String, month: Int?, baseBudget: BudgetYear) {
        val yearString = year + if (month != null) "-${String.format("%02d", month)}" else ""
        budgetYearRepository.insertBudgetYear(BudgetYear(0, yearString))
    }
}


//Data class for BudgetUiState
data class BudgetUiState(
    val categories: List<Category> = listOf(),
    val transactions: List<TransactionWithDetails> = listOf(),
    val budgetEntries: List<BudgetEntry> = listOf(),
    val budgetYears: List<BudgetYear> = listOf(),
    val selectedBudgetYear: BudgetYear? = null
)