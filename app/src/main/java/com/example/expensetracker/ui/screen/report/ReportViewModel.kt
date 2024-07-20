package com.example.expensetracker.ui.screen.report

import androidx.lifecycle.ViewModel
import com.example.expensetracker.data.model.Transaction
import com.example.expensetracker.data.repository.category.CategoriesRepository
import com.example.expensetracker.data.repository.payee.PayeesRepository
import com.example.expensetracker.data.repository.transaction.TransactionsRepository
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.columnSeries
import com.patrykandpatrick.vico.core.cartesian.layer.ColumnCartesianLayer.ColumnProvider.Companion.series
import com.patrykandpatrick.vico.core.common.data.ExtraStore
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

    suspend fun getExpensesByCategoryFromDB(
        transCode: String? = null,
        categName: String? = null
    ): List<Transaction> {
        return if (transCode != null) {
            transactionsRepository.getAllTransactionsByCategory(transCode).first()
        } else if (categName != null) {
            transactionsRepository.getAllTransactionsByCategoryName(categName).first()
        } else {
            listOf()
        }
    }

    suspend fun getExpensesByPayeeFromDB(payeeId: String): List<Transaction> {
        return transactionsRepository.getAllTransactionsByPayee(payeeId).first()
    }


    suspend fun getExpensesFromCategory(transCode: String): CartesianChartModelProducer? {
        val transactions = getExpensesByCategoryFromDB(transCode)

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
        } else {
            return null
        }
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
        } else {
            return null
        }
    }
}