package com.example.expensetracker.ui.screen.report

import androidx.lifecycle.ViewModel
import com.example.expensetracker.data.category.CategoriesRepository
import com.example.expensetracker.data.payee.PayeesRepository
import com.example.expensetracker.data.transaction.TransactionsRepository
import com.example.expensetracker.model.Category
import com.example.expensetracker.model.Transaction
import com.example.expensetracker.model.TransactionWithDetails
import com.example.expensetracker.ui.screen.accounts.Totals
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

/**
 * ViewModel to retrieve all items in the Room database.
 */
class ReportViewModel(
    private val transactionsRepository: TransactionsRepository,
    private val categoriesRepository: CategoriesRepository
) : ViewModel() {
    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

    // Flow for expenses
    private val expensesFlow: Flow<List<TransactionWithDetails>> =
        transactionsRepository.getAllTransactionsStream()

    // Flow for expenses
    private val categoriesFlow: Flow<List<Category>> =
        categoriesRepository.getAllCategoriesStream()

    //TODO: THIS
    // Combine the flows and get the chart data
    //val categoryChartData: Flow<Pair<>> =
      //  combine(expensesFlow, incomeFlow, totalFlow) { expenses, income, total ->
     //       Totals(expenses, income, total)
     //   }
}