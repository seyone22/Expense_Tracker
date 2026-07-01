package com.seyone22.expensetracker.ui.screen.report

import android.util.Log
import androidx.lifecycle.ViewModel
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.compose.cartesian.data.columnModel
import com.patrykandpatrick.vico.compose.cartesian.layer.ColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.layer.ColumnCartesianLayer.ColumnProvider.Companion.series
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.marker.rememberDefaultCartesianMarker
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.common.component.TextComponent
import com.patrykandpatrick.vico.compose.common.component.rememberLineComponent
import com.patrykandpatrick.vico.compose.common.component.rememberShapeComponent
import com.patrykandpatrick.vico.compose.common.component.rememberTextComponent
import com.seyone22.expensetracker.data.model.Report
import com.seyone22.expensetracker.data.model.Transaction
import com.seyone22.expensetracker.data.repository.category.CategoriesRepository
import com.seyone22.expensetracker.data.repository.payee.PayeesRepository
import com.seyone22.expensetracker.data.repository.report.ReportsRepository
import com.seyone22.expensetracker.data.repository.transaction.TransactionsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import androidx.sqlite.db.SimpleSQLiteQuery
import com.patrykandpatrick.vico.compose.cartesian.data.ColumnCartesianLayerModel
import com.patrykandpatrick.vico.compose.cartesian.data.columnModel
import com.patrykandpatrick.vico.compose.cartesian.data.columnSeries
import com.patrykandpatrick.vico.compose.common.data.ExtraStore
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
    private val reportsRepository: ReportsRepository,
    private val billsDepositsRepository: com.seyone22.expensetracker.data.repository.billsDeposit.BillsDepositsRepository
) : ViewModel() {

    val activeSubscriptionsFlow: Flow<List<com.seyone22.expensetracker.data.model.BillsDepositWithDetails>> =
        billsDepositsRepository.getAllTransactionsStream()

    init {
        viewModelScope.launch {
            val currentReports = reportsRepository.getAllReportsStream().first()
            if (currentReports.isEmpty()) {
                insertDefaultReports()
            }
        }
    }
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
            return emptyList()
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
        val category = categoriesRepository.getCategoryByIdStream(categoryId).first()
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
                columnModel { series(xToDates.keys, transactionMap.values) }
            }

            return modelProducer
        } else {
            return null
        }
    }

    private suspend fun insertDefaultReports() {
        val defaultList = listOf(
            Report(
                REPORTNAME = "Income vs. Expenses",
                GROUPNAME = "Income & Expenses",
                ACTIVE = 1,
                SQLCONTENT = """
                    SELECT 
                        strftime('%Y-%m', transDate) AS month,
                        SUM(CASE WHEN transCode = 'Deposit' THEN transAmount ELSE 0 END) AS Income,
                        SUM(CASE WHEN transCode = 'Withdrawal' THEN transAmount ELSE 0 END) AS Expenses
                    FROM CHECKINGACCOUNT_V1
                    WHERE status != 'Void'
                    GROUP BY month
                    ORDER BY month ASC
                    LIMIT 12
                """.trimIndent(),
                LUACONTENT = "",
                TEMPLATECONTENT = "chart_type=bar",
                DESCRIPTION = "Shows your monthly income against expenses for the last 12 months."
            ),
            Report(
                REPORTNAME = "Expenses by Category (This Month)",
                GROUPNAME = "Categories",
                ACTIVE = 1,
                SQLCONTENT = """
                    SELECT 
                        c.categName AS category,
                        SUM(t.transAmount) AS amount
                    FROM CHECKINGACCOUNT_V1 t
                    INNER JOIN CATEGORY_V1 c ON t.categoryId = c.categId
                    WHERE t.transCode = 'Withdrawal' 
                      AND t.status != 'Void'
                      AND strftime('%Y-%m', t.transDate) = strftime('%Y-%m', 'now')
                    GROUP BY category
                    ORDER BY amount DESC
                """.trimIndent(),
                LUACONTENT = "",
                TEMPLATECONTENT = "chart_type=pie",
                DESCRIPTION = "Pie chart breakdown of expenses by category for the current month."
            ),
            Report(
                REPORTNAME = "Current Account Balances",
                GROUPNAME = "Accounts",
                ACTIVE = 1,
                SQLCONTENT = """
                    SELECT 
                        a.accountName AS account,
                        (a.initialBalance + COALESCE(SUM(
                            CASE 
                                WHEN t.transCode = 'Deposit' THEN t.transAmount
                                WHEN t.transCode = 'Withdrawal' THEN -t.transAmount
                                WHEN t.transCode = 'Transfer' AND t.accountId = a.accountId THEN -t.transAmount
                                WHEN t.transCode = 'Transfer' AND t.toAccountId = a.accountId THEN t.transAmount
                                ELSE 0 
                            END
                        ), 0)) AS balance
                    FROM ACCOUNTLIST_V1 a
                    LEFT JOIN CHECKINGACCOUNT_V1 t ON t.accountId = a.accountId OR t.toAccountId = a.accountId
                    WHERE a.status = 'Open'
                    GROUP BY a.accountId
                    ORDER BY balance DESC
                """.trimIndent(),
                LUACONTENT = "",
                TEMPLATECONTENT = "chart_type=bar",
                DESCRIPTION = "Current real-time balances for all open accounts."
            )
        )
        for (report in defaultList) {
            reportsRepository.insertReport(report)
        }
    }

    suspend fun runReportQuery(sql: String): List<Map<String, Any>> = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
        val results = mutableListOf<Map<String, Any>>()
        try {
            val query = SimpleSQLiteQuery(sql)
            val cursor = reportsRepository.executeRawQuery(query)
            cursor.use { c ->
                val columnNames = c.columnNames
                while (c.moveToNext()) {
                    val row = mutableMapOf<String, Any>()
                    for (i in 0 until c.columnCount) {
                        val name = columnNames[i]
                        when (c.getType(i)) {
                            android.database.Cursor.FIELD_TYPE_NULL -> row[name] = ""
                            android.database.Cursor.FIELD_TYPE_INTEGER -> row[name] = c.getLong(i)
                            android.database.Cursor.FIELD_TYPE_FLOAT -> row[name] = c.getDouble(i)
                            android.database.Cursor.FIELD_TYPE_STRING -> row[name] = c.getString(i)
                            android.database.Cursor.FIELD_TYPE_BLOB -> row[name] = c.getBlob(i)
                        }
                    }
                    results.add(row)
                }
            }
        } catch (e: Exception) {
            Log.e("ReportViewModel", "Error running custom report query", e)
        }
        results
    }

    suspend fun deleteReport(report: Report) {
        reportsRepository.deleteReport(report)
    }

    suspend fun updateReport(report: Report) {
        reportsRepository.updateReport(report)
    }
}