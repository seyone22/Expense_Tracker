package com.seyone22.expensetracker.ui.screen.budget.budgetDetail

import android.util.Log
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
import com.seyone22.expensetracker.utils.convertBudgetValue
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
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
    val selectedBudgetYear: StateFlow<BudgetYear?> = _selectedBudgetYear

    private val _incomeStatistics = MutableStateFlow(0.0)
    val incomeStatistics: StateFlow<Double> = _incomeStatistics

    private val _expenseStatistics = MutableStateFlow(0.0)
    val expenseStatistics: StateFlow<Double> = _expenseStatistics

    // Flow for categories and transactions
    private val _categoriesFlow: Flow<List<Category>> =
        categoriesRepository.getAllCategoriesStream()
    private val _transactionsFlow: Flow<List<TransactionWithDetails>> =
        transactionsRepository.getAllTransactionsStream(
            sortField = "TransDate",
            sortDirection = "DESC"
        )

    // Mutable StateFlow to store budget entries
    private val _budgetEntriesFlow = MutableStateFlow<List<BudgetEntry>>(emptyList())
    val budgetEntriesFlow: StateFlow<List<BudgetEntry>> = _budgetEntriesFlow

    // Combine the flows
    val budgetDetailUiState: Flow<BudgetDetailUiState> = combine(
        _categoriesFlow, _transactionsFlow, budgetEntriesFlow, _selectedBudgetYear
    ) { categories, transactions, budgetEntries, selectedBudgetYear ->
        // Return a new BudgetUiState combining all data
        BudgetDetailUiState(
            categories, transactions, budgetEntries, selectedBudgetYear
        )
    }


    // Function to get the total estimated income
    fun getEstimatedIncome(): Double {
        return _budgetEntriesFlow.value.filter { budgetEntry ->
            budgetEntry.amount >= 0  // Consider income as entries with non-negative amounts
        }.sumOf { convertBudgetValue(it, fetchBudgetPeriod()) }  // Sum of amounts for those entries
    }

    // Function to get the total estimated income
    fun getEstimatedExpenses(): Double {
        return _budgetEntriesFlow.value.filter { budgetEntry ->
            budgetEntry.amount < 0  // Consider expense as entries with non-negative amounts
        }.sumOf { convertBudgetValue(it, fetchBudgetPeriod()) } // Sum of amounts for those entries
    }


    suspend fun fetchBudgetEntriesFor(budgetYearId: Int) {
        val selectedYear = _selectedBudgetYear.value ?: return

        if (getDateOfBudgetYear(selectedYear).second == null) {
            val entries = budgetEntryRepository.getBudgetEntriesForBudgetYear(budgetYearId)
            _budgetEntriesFlow.value = entries.firstOrNull() ?: emptyList()
        } else {
            val entries = budgetEntryRepository.getBudgetEntriesForBudgetYear(budgetYearId)
            _budgetEntriesFlow.value = entries.firstOrNull() ?: emptyList()
        }


    }

    suspend fun fetchBudgetYearFor(backStackEntry: Int) {
        val budgetYear = budgetYearRepository.getBudgetYearById(backStackEntry)
        _selectedBudgetYear.value = budgetYear.firstOrNull()
    }

    suspend fun fetchStatistics() {
        val selectedYear = _selectedBudgetYear.value ?: return

        val (year, month) = getDateOfBudgetYear(selectedYear)

        val monthString = month?.toString()?.padStart(2, '0')

        // Fetch total income (deposits)
        val totalIncome = transactionsRepository
            .getTotalBalanceByCodeAndDate("Deposit", "Reconciled", monthString, year.toString())
            .firstOrNull() ?: 0.0

        // Fetch total expenses (withdrawals)
        val totalExpenses = transactionsRepository
            .getTotalBalanceByCodeAndDate("Withdrawal", "Reconciled", monthString, year.toString())
            .firstOrNull() ?: 0.0

        // Update state
        _incomeStatistics.value = totalIncome
        _expenseStatistics.value = totalExpenses * -1

        Log.d(
            "BudgetDetailViewModel",
            "fetchStatistics: Income = $totalIncome, Expenses = $totalExpenses"
        )
    }

    suspend fun fetchCategoryStatisticsFor(
        categId: Int,
        selectedBudgetYear: BudgetYear?
    ): Pair<Double, Double> {
        val estimated = getEstimatedForCategory(categId)
        val actual = getActualForCategory(categId, selectedBudgetYear)
        return Pair(estimated, actual)
    }

    private suspend fun getChildCategoriesForParent(parentId: Int): List<Int> {
        return budgetDetailUiState.first().categories.filter { it.parentId == parentId }
            .map { it.categId }
    }

    private suspend fun getEstimatedForCategory(parentId: Int): Double {
        val categoryIds = listOf(parentId) + getChildCategoriesForParent(parentId)
        return _budgetEntriesFlow.value.filter { it.categId in categoryIds }
            .sumOf { convertBudgetValue(it, fetchBudgetPeriod()) }
    }

    private suspend fun getActualForCategory(
        parentId: Int,
        selectedBudgetYear: BudgetYear?
    ): Double {
        if (selectedBudgetYear == null) return 0.0

        val categoryIds = listOf(parentId) + getChildCategoriesForParent(parentId)

        return categoryIds.sumOf { categId ->
            getExpensesForCategory(categId, selectedBudgetYear)
        }
    }

    suspend fun getExpensesForCategory(categId: Int, selectedBudgetYear: BudgetYear?): Double {
        if (selectedBudgetYear == null) return 0.0

        val (year, month) = getDateOfBudgetYear(selectedBudgetYear)
        val monthString = month?.toString()?.padStart(2, '0')

        val expense = transactionsRepository.getTotalBalanceByCategoryAndDate(
            categId = categId,
            month = monthString,
            year = year.toString()
        ).firstOrNull() ?: 0.0

        Log.d(
            "BudgetDetailViewModel",
            "getExpensesForCategory: Year=$year, Month=$month, CategId=$categId, Expense=$expense"
        )

        return expense
    }

    fun fetchBudgetPeriod(): String? {
        val selectedYear = _selectedBudgetYear.value ?: return null

        if (getDateOfBudgetYear(selectedYear).second == null) {
            return "Yearly"
        } else {
            return "Monthly"
        }
    }

    private fun getDateOfBudgetYear(selectedBudgetYear: BudgetYear): Pair<Int, Int?> {
        val parts = selectedBudgetYear.budgetYearName.split("-")
        val year = parts[0].toIntOrNull() ?: return Pair(0, null)
        val month = parts.getOrNull(1)?.toIntOrNull()
        return Pair(year, month)
    }

    suspend fun addBudgetEntry(budgetEntry: BudgetEntry) {
        budgetEntryRepository.insertBudgetEntry(budgetEntry)
    }

    suspend fun editBudgetEntry(budgetEntry: BudgetEntry) {
        budgetEntryRepository.updateBudgetEntry(budgetEntry)
    }
}

//Data class for BudgetUiState
data class BudgetDetailUiState(
    val categories: List<Category> = emptyList(),
    val transactions: List<TransactionWithDetails> = emptyList(),
    val budgetEntries: List<BudgetEntry> = emptyList(),
    val selectedBudgetYear: BudgetYear? = null
)
