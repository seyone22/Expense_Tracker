package com.seyone22.expensetracker.ui.screen.budget.budgetDetail

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

/**
 * ViewModel to retrieve all items in the Room database.
 */
class BudgetDetailViewModel(
    private val transactionsRepository: TransactionsRepository,
    private val categoriesRepository: CategoriesRepository,
    private val payeesRepository: PayeesRepository,
    private val budgetEntryRepository: BudgetEntryRepository,
    private val budgetYearRepository: BudgetYearRepository
) : BaseViewModel() {
    private var _selectedBudgetYear = MutableStateFlow<BudgetYear?>(null)

    // Flow for categories and transactions
    private val categoriesFlow: Flow<List<Category>> = categoriesRepository.getAllCategoriesStream()
    private val transactionsFlow: Flow<List<TransactionWithDetails>> =
        transactionsRepository.getAllTransactionsStream()

    // Mutable StateFlow to store budget entries
    private val _budgetEntriesFlow = MutableStateFlow<List<BudgetEntry>>(emptyList())
    val budgetEntriesFlow: StateFlow<List<BudgetEntry>> = _budgetEntriesFlow

    // Combine the flows
    val budgetDetailUiState: Flow<BudgetDetailUiState> = combine(
        categoriesFlow, transactionsFlow, budgetEntriesFlow, _selectedBudgetYear
    ) { categories, transactions, budgetEntries, selectedBudgetYear ->
        // Return a new BudgetUiState combining all data
        BudgetDetailUiState(
            categories, transactions, budgetEntries, selectedBudgetYear
        )
    }

    // Function to fetch Budget Entries for a specific Budget Year
    suspend fun fetchBudgetEntriesFor(budgetYearId: Int) {
        // Fetch the budget entries from the repository and update the StateFlow
        val entries = budgetEntryRepository.getBudgetEntriesForBudgetYear(budgetYearId)
        _budgetEntriesFlow.value = entries.firstOrNull() ?: emptyList()
    }

    suspend fun fetchBudgetYearFor(backStackEntry: Int) {
        val budgetYear = budgetYearRepository.getBudgetYearById(backStackEntry)
        _selectedBudgetYear.value = budgetYear.firstOrNull()
    }

    suspend fun fetchStatistics(backStackEntry: Int) {

    }

    suspend fun addBudgetEntry(budgetEntry: BudgetEntry) {
        budgetEntryRepository.insertBudgetEntry(budgetEntry)
    }

    suspend fun editBudgetEntry(budgetEntry: BudgetEntry) {
        budgetEntryRepository.updateBudgetEntry(budgetEntry)
    }

    suspend fun getExpensesForCategory(categId: Int, selectedBudgetYear: BudgetYear?): Double {
        if (selectedBudgetYear == null) return 0.0

        val dates = getDateOfBudgetYear(selectedBudgetYear)

        return transactionsRepository.getTotalBalanceByCategoryAndDate(
            categId = categId, month = dates.second, year = dates.first
        ).firstOrNull() ?: 0.0

    }

    private fun getDateOfBudgetYear(selectedBudgetYear: BudgetYear): Pair<Int, Int?> {
        val parts = selectedBudgetYear.budgetYearName.split("-") // Split the string by '-'
        val year =
            parts[0].toIntOrNull() ?: return Pair(0, null) // Ensure we always get a valid year
        val month = parts.getOrNull(1)?.toIntOrNull() // Get the month if it exists

        return Pair(year, month)
    }


}

//Data class for BudgetUiState
data class BudgetDetailUiState(
    val categories: List<Category> = emptyList(),
    val transactions: List<TransactionWithDetails> = emptyList(),
    val budgetEntries: List<BudgetEntry> = emptyList(),
    val selectedBudgetYear: BudgetYear? = null
)