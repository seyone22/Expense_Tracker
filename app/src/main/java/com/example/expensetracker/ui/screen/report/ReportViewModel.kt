package com.example.expensetracker.ui.screen.report

import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import com.example.expensetracker.data.category.CategoriesRepository
import com.example.expensetracker.data.payee.PayeesRepository
import com.example.expensetracker.data.transaction.TransactionsRepository
import com.example.expensetracker.model.Category
import com.example.expensetracker.model.Payee
import com.example.expensetracker.model.Transaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlin.random.Random

/**
 * ViewModel to retrieve all items in the Room database.
 */
class ReportViewModel(
    private val transactionsRepository: TransactionsRepository,
    private val categoriesRepository: CategoriesRepository,
    private val payeesRepository: PayeesRepository
) : ViewModel() {
    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

    // Flow for expenses
    private val transactionsExpenseFlow: Flow<List<Transaction>> =
        transactionsRepository.getAllTransactionsByCode("Withdrawal")

    // Flow for income
    private val transactionsIncomeFlow: Flow<List<Transaction>> =
        transactionsRepository.getAllTransactionsByCode("Deposit")

    // Flow for categories
    private val categoriesFlow: Flow<List<Category>> =
        categoriesRepository.getAllCategoriesStream()

    // Flow for payees
    private val payeesFlow: Flow<List<Payee>> =
        payeesRepository.getAllActivePayeesStream()

    // Combine the flows and get the chart data
    val byPayeeData: Flow<Pair<List<String>, List<Double>>> =
        combine(payeesFlow, transactionsIncomeFlow) { payees, transactions ->
            // Separate payees and total amounts into two lists
            val payeeList = payees.map { it.payeeName }

            // Calculate the total sum of all payees' total amounts
            val totalSum = payees.sumOf { payee ->
                transactions
                    .filter { it.payeeId == payee.payeeId }
                    .sumOf { it.transAmount }
            }

            val totalAmountList = payees.map { payee ->
                val totalAmount = transactions
                    .filter { it.payeeId == payee.payeeId }
                    .sumOf { it.transAmount }

                // Calculate the fraction dynamically based on the total sum
                val fraction = if (totalSum != 0.0) totalAmount / totalSum else 0.0

                fraction
            }
            Pair(payeeList, totalAmountList)
        }

    // Combine the flows and get the chart data
    val byCategoryData: Flow<Pair<List<Category>, List<Double>>> =
        combine(categoriesFlow, transactionsIncomeFlow) { categories, transactions ->
            // Separate categories and total amounts into two lists
            val categoryList = categories.toList()
            Log.d("TAG", ": $transactions")
            // Calculate the total sum of all payees' total amounts
            val totalSum = categories.sumOf { category ->
                transactions
                    .filter { it.categoryId == category.categId }
                    .sumOf { it.transAmount }
            }

            val totalAmountList = categories.map { category ->
                val totalAmount = transactions
                    .filter { it.categoryId == category.categId }
                    .sumOf { it.transAmount }

                // Calculate the fraction dynamically based on the total sum
                val fraction = if (totalSum != 0.0) totalAmount / totalSum else 0.0
                fraction
            }
            Pair(categoryList, totalAmountList)
        }

    fun generateDistinctColors(numberOfColors: Int): List<Color> {
        return List(numberOfColors) {
            Color(
                red = Random.nextFloat(),
                green = Random.nextFloat(),
                blue = Random.nextFloat(),
                alpha = 1.0f
            )
        }
    }
}