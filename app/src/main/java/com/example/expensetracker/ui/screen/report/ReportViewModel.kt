package com.example.expensetracker.ui.screen.report

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.expensetracker.data.category.CategoriesRepository
import com.example.expensetracker.data.payee.PayeesRepository
import com.example.expensetracker.data.transaction.TransactionsRepository
import com.example.expensetracker.model.Category
import com.example.expensetracker.model.Payee
import com.example.expensetracker.model.Transaction
import com.patrykandpatrick.vico.core.model.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.model.ExtraStore
import com.patrykandpatrick.vico.core.model.columnSeries
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import java.util.Locale

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


    suspend fun getExpensesFromCategory(transCode: String) : CartesianChartModelProducer {
        val transactions = transactionsRepository.getAllTransactionsByCategory(transCode).first()

        val transactionMap = transactions.groupBy { transaction ->
            LocalDate.parse(transaction.transDate).month.toString()
        }.mapValues { (_, transactions) ->
            transactions.sumOf { if (it.transCode == "Withdrawal") -it.transAmount else it.transAmount }
        }

        val xToDateMapKey = ExtraStore.Key<List<String>>()
        val xToDates = transactionMap.keys.associateBy { monthNumericalMap[it.uppercase(Locale.ROOT)] ?: 0f }

        val modelProducer = CartesianChartModelProducer.build()

        modelProducer.tryRunTransaction {
            columnSeries {
                series(xToDates.keys, transactionMap.values)
            }
            updateExtras {
                it[xToDateMapKey] = transactionMap.keys.toList()
            }
        }

        return modelProducer
    }

    suspend fun getExpensesFromPayee(payeeId: String): CartesianChartModelProducer {
        val transactions = transactionsRepository.getAllTransactionsByPayee(payeeId).first()

        val transactionMap = transactions.groupBy { transaction ->
            LocalDate.parse(transaction.transDate).month.toString()
        }.mapValues { (_, transactions) ->
            transactions.sumOf { if (it.transCode == "Withdrawal") -it.transAmount else it.transAmount }
        }

        val xToDateMapKey = ExtraStore.Key<List<String>>()
        val xToDates = transactionMap.keys.associateBy { monthNumericalMap[it.uppercase(Locale.ROOT)] ?: 0f }

        val modelProducer = CartesianChartModelProducer.build()

        modelProducer.tryRunTransaction {
            columnSeries {
                series(xToDates.keys, transactionMap.values)
            }
            updateExtras {
                it[xToDateMapKey] = transactionMap.keys.toList()
            }
        }

        return modelProducer
    }
}