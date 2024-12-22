package com.example.expensetracker.ui.screen.report

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.expensetracker.data.model.Category
import com.example.expensetracker.data.model.Report
import com.example.expensetracker.data.model.Transaction
import com.example.expensetracker.data.repository.category.CategoriesRepository
import com.example.expensetracker.data.repository.payee.PayeesRepository
import com.example.expensetracker.data.repository.report.ReportsRepository
import com.example.expensetracker.data.repository.transaction.TransactionsRepository
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.columnSeries
import com.patrykandpatrick.vico.core.common.data.ExtraStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import java.util.Locale
import kotlin.math.absoluteValue

/**
 * ViewModel to retrieve all items in the Room database.
 */
class ReportViewModel(
    private val transactionsRepository: TransactionsRepository,
    private val categoriesRepository: CategoriesRepository,
    private val payeesRepository: PayeesRepository,
    private val reportsRepository: ReportsRepository

) : ViewModel() {
    companion object {
        private const val TIMEOUT_MILLIS = 5_000L

        val monthNumericalMap = mapOf(
            "JANUARY" to 1f,
            "FEBRUARY" to 2f,
            "MARCH" to 3f,
            "APRIL" to 4f,
            "MAY" to 5f,
            "JUNE" to 6f,
            "JULY" to 7f,
            "AUGUST" to 8f,
            "SEPTEMBER" to 9f,
            "OCTOBER" to 10f,
            "NOVEMBER" to 11f,
            "DECEMBER" to 12f
        )
    }

    // Flows for each type of entity
    val reportsFlow: Flow<List<Report>> =
        reportsRepository.getAllReportsStream()

    private suspend fun getExpensesByCategoryFromDB(
        transCode: String? = null,
        categName: String? = null,
        categoryId: Int? = null,
    ): List<Transaction> {
        return if (categoryId != null) {
            transactionsRepository.getAllTransactionsByCategory(categoryId).first()
        } else {
            return listOf()
        }
    }

    private suspend fun getExpensesByPayeeFromDB(payeeId: String): List<Transaction> {
        return transactionsRepository.getAllTransactionsByPayee(payeeId).first()
    }

    suspend fun getExpensesFromCategory(categoryId: Int): Map<Int?, Double> {
        val transactions = getExpensesByCategoryFromDB(categoryId = categoryId)

        if (transactions.isNotEmpty()) {
            val transactionMap = transactions
                .groupBy { transaction ->
                    transaction.categoryId
                }
                .mapValues { (_, transactions) ->
                    transactions.sumOf { if (it.transCode == "Withdrawal") -it.transAmount else it.transAmount }.absoluteValue
                }

            Log.d("TAG", "getExpensesFromCategory: $transactionMap")
            return transactionMap
        } else {
            return mapOf()
        }
    }

    suspend fun categoryNameOf(categoryId: Int): String {
        val category = categoriesRepository.getCategoriesStream(categoryId).first()
        return category?.categName ?: "NOT FOUND"
    }

    suspend fun getExpensesFromPayee(payeeId: String): CartesianChartModelProducer? {
        val transactions = getExpensesByPayeeFromDB(payeeId)

        if (transactions.isNotEmpty()) {
            val transactionMap = transactions.groupBy { transaction ->
                LocalDate.parse(transaction.transDate).month.toString()
            }.mapValues { (_, transactions) ->
                transactions.sumOf { if (it.transCode == "Withdrawal") -it.transAmount else it.transAmount }
            }

            val xToDateMapKey = ExtraStore.Key<List<String>>()
            val xToDates = transactionMap.keys.associateBy {
                monthNumericalMap[it.uppercase(Locale.ROOT)] ?: 0f
            }

            val modelProducer = CartesianChartModelProducer()

            modelProducer.runTransaction {
                columnSeries {
                    series(xToDates.keys, transactionMap.values)
                }
            }

            return modelProducer
        } else {
            return null
        }
    }
}