package com.seyone22.expensetracker.ui.screen.entities.panes

import android.text.Layout
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigation.ThreePaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.window.core.layout.WindowSizeClass
import androidx.window.core.layout.WindowWidthSizeClass
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.marker.rememberDefaultCartesianMarker
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.common.component.rememberLineComponent
import com.patrykandpatrick.vico.compose.common.component.rememberShapeComponent
import com.patrykandpatrick.vico.compose.common.component.rememberTextComponent
import com.patrykandpatrick.vico.compose.common.dimensions
import com.patrykandpatrick.vico.compose.common.fill
import com.patrykandpatrick.vico.compose.common.shape.markerCorneredShape
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.core.cartesian.data.columnSeries
import com.patrykandpatrick.vico.core.cartesian.layer.ColumnCartesianLayer
import com.patrykandpatrick.vico.core.common.component.TextComponent
import com.patrykandpatrick.vico.core.common.shape.Corner
import com.patrykandpatrick.vico.core.common.shape.CorneredShape
import com.seyone22.expensetracker.data.model.Category
import com.seyone22.expensetracker.data.model.CurrencyFormat
import com.seyone22.expensetracker.data.model.Payee
import com.seyone22.expensetracker.data.model.Tag
import com.seyone22.expensetracker.ui.AppViewModelProvider
import com.seyone22.expensetracker.ui.common.ExpenseTopBar
import com.seyone22.expensetracker.ui.screen.entities.EntityViewModel
import com.seyone22.expensetracker.ui.screen.transactions.TransactionsViewModel
import com.seyone22.expensetracker.ui.screen.transactions.composables.TransactionFilters
import com.seyone22.expensetracker.ui.screen.transactions.composables.TransactionList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter


@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun EntityDetailPane(
    viewModel: EntityViewModel,
    transactionViewModel: TransactionsViewModel = viewModel(factory = AppViewModelProvider.Factory),
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    scaffoldNavigator: ThreePaneScaffoldNavigator<Int>,
    windowSizeClass: WindowSizeClass = currentWindowAdaptiveInfo().windowSizeClass,
    navController: NavController
) {
    val entity by viewModel.selectedEntity.collectAsState()
    val transactions by transactionViewModel.filteredTransactions.collectAsState()
    val modelProducer = remember { CartesianChartModelProducer() }

    val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    // Apply filters based on the selected entity
    LaunchedEffect(entity) {
        transactionViewModel.setFilters(
            when (entity) {
                is Category -> TransactionFilters(categoryFilter = entity as Category)
                is CurrencyFormat -> TransactionFilters(currencyFilter = entity as CurrencyFormat)
                is Payee -> TransactionFilters(payeeFilter = entity as Payee)
                is Tag -> TransactionFilters(tagFilter = entity as Tag)
                else -> TransactionFilters()
            }
        )
    }

    // Process transactions to aggregate amounts per date
    LaunchedEffect(transactions) {
        val transactionsByDate =
            transactions.groupBy { it.transDate } // Assuming `transDate` is a string or LocalDate
                .mapKeys {
                    LocalDate.parse(
                        it.key, dateFormatter
                    )
                } // Convert String to LocalDate for proper sorting
                .mapValues { (_, list) ->
                    // Conditionally sum based on transCode
                    list.sumOf {
                        when (it.transCode) {
                            "Deposit" -> it.transAmount // Add if Deposit
                            "Withdrawal" -> -it.transAmount // Subtract if Withdrawal
                            else -> 0.0 // Default to 0 for other transaction types
                        }
                    }
                }.toSortedMap() // Ensure sorted order

        val xValues = if (transactions.size <= 3) {
            // Convert dates to Epoch days and create a padded list
            val firstDate = transactionsByDate.keys.minOrNull() ?: LocalDate.now()
            val lastDate = transactionsByDate.keys.maxOrNull() ?: LocalDate.now()

            // Create a list of dates from 3 days before the first date to 3 days after the last date
            val paddedDates =
                generateSequence(firstDate.minusDays(3)) { it.plusDays(1) }.takeWhile {
                    it <= lastDate.plusDays(3)
                }.map { it.toEpochDay().toFloat() }.toList()

            Log.d("TAG", "EntityDetailPane: $paddedDates")

            paddedDates
        } else {
            // For more than 3 transactions, no padding needed
            transactionsByDate.keys.map { date ->
                date.toEpochDay().toFloat()
            }
        }

        val yValues = transactionsByDate.values.map { it.toFloat() } // Convert amounts to Float

        modelProducer.runTransaction {
            columnSeries {
                series(x = xValues, y = yValues)
            }
        }
    }

    if (entity != null) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            item {
                ExpenseTopBar(
                    selectedActivity = "Details",
                    type = "Left",
                    hasNavBarAction = false,
                    navBarBackAction = { coroutineScope.launch { scaffoldNavigator.navigateBack() } },
                    navController = navController,
                    hasNavigation = (windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.COMPACT),
                )
            }

            if (transactions.isNotEmpty()) {
                item {
                    Box {
                        CartesianChartHost(
                            modifier = Modifier.fillMaxWidth(),
                            chart = rememberCartesianChart(
                                rememberColumnCartesianLayer(
                                    columnProvider = ColumnCartesianLayer.ColumnProvider.series(
                                        rememberLineComponent(
                                            fill = fill(Color(MaterialTheme.colorScheme.primary.toArgb())),
                                            thickness = 8.dp,
                                            shape = CorneredShape.rounded(allPercent = 16),
                                        )
                                    )
                                ), startAxis = VerticalAxis.rememberStart(
                                    label = rememberTextComponent(
                                        color = MaterialTheme.colorScheme.onSurface,
                                    ),


                                    ), bottomAxis = HorizontalAxis.rememberBottom(
                                    guideline = null,
                                    valueFormatter = bottomAxisValueFormatter,
                                    label = rememberTextComponent(
                                        color = MaterialTheme.colorScheme.onSurface,
                                    )
                                ), marker = rememberDefaultCartesianMarker(
                                    label = rememberTextComponent(
                                        color = MaterialTheme.colorScheme.onSurface,
                                        textAlignment = Layout.Alignment.ALIGN_CENTER,
                                        padding = dimensions(8.dp, 4.dp),
                                        background = rememberShapeComponent(
                                            fill = fill(MaterialTheme.colorScheme.surfaceBright),
                                            shape = markerCorneredShape(Corner.Sharp),
                                        ),
                                        minWidth = TextComponent.MinWidth.fixed(40f),
                                    )
                                )
                            ),
                            modelProducer = modelProducer,

                            )
                    }
                }

                item {
                    TransactionList()
                }
            } else {
                item {
                    Text("No transactions found")
                }
            }
        }
    } else {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Nothing selected!")
        }
    }
}

private val bottomAxisValueFormatter = CartesianValueFormatter { _, x, _ ->
    try {
        val date = LocalDate.ofEpochDay(x.toLong()) // Convert x back to LocalDate
        date.format(DateTimeFormatter.ofPattern("MM/dd")) // Format as MM/dd
    } catch (e: Exception) {
        "" // Fallback in case of errors
    }
}
