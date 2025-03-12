package com.seyone22.expensetracker.ui.screen.budget

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
    suspend fun fetchBudgetEntriesFor(budgetYearId: Int) {
        // Fetch the budget entries from the repository and update the StateFlow
        val entries = budgetEntryRepository.getBudgetEntriesForBudgetYear(budgetYearId)
        _budgetEntriesFlow.value = entries.firstOrNull() ?: emptyList()
    }

    suspend fun addBudgetYear(year: String, month: Int?, baseBudget: BudgetYear) {
        // Check if the year budget already exists
        val yearExists = budgetYearRepository.getBudgetYearByName(year).firstOrNull() != null

        // Gets the year string, which will include month if it's a month budget
        val yearString = if (month != null) "$year-${"%02d".format(month)}" else year
        // Check if the month budget already exists
        val monthExists = budgetYearRepository.getBudgetYearByName(yearString).firstOrNull() != null

        // Ensure the budget year exists
        if (!yearExists) budgetYearRepository.insertBudgetYear(BudgetYear(0, year))
        else showSnackbar("Budget year already exists")

        // Ensure the budget month exists if a month is provided
        month?.let {
            if (!monthExists) budgetYearRepository.insertBudgetYear(BudgetYear(0, yearString))
            else showSnackbar("Budget month already exists")
        }

    }
}

//Data class for BudgetUiState
data class BudgetUiState(
    val categories: List<Category> = emptyList(),
    val transactions: List<TransactionWithDetails> = emptyList(),
    val budgetEntries: List<BudgetEntry> = emptyList(),
    val budgetYears: List<BudgetYear> = emptyList(),
    val selectedBudgetYear: BudgetYear? = null
)