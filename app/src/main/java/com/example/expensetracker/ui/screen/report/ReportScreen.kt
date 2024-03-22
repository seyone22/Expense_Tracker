package com.example.expensetracker.ui.screen.report

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.expensetracker.R
import com.example.expensetracker.model.CurrencyFormat
import com.example.expensetracker.ui.AppViewModelProvider
import com.example.expensetracker.ui.common.SortBar
import com.example.expensetracker.ui.navigation.NavigationDestination
import com.example.expensetracker.ui.screen.accounts.LegendItem
import com.example.expensetracker.ui.screen.accounts.Totals
import com.github.tehras.charts.piechart.PieChart
import com.github.tehras.charts.piechart.PieChartData
import com.github.tehras.charts.piechart.animation.simpleChartAnimation
import com.github.tehras.charts.piechart.renderer.SimpleSliceDrawer

object ReportsDestination : NavigationDestination {
    override val route = "Reports"
    override val titleRes = R.string.app_name
    override val routeId = 4
}

@Composable
fun ReportScreen(
    modifier: Modifier = Modifier,
    navigateToScreen: (screen: String) -> Unit,
    viewModel: ReportViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 320.dp),
        modifier = modifier.fillMaxSize()
    ) {
        items(count = 2) {
            Column {
                Text(
                    text = "Transactions by Payees",
                    modifier = Modifier
                        .padding(16.dp),
                    textAlign = TextAlign.Center,
                )
                SortBar(periodSortAction = {})

            }
        }
    }
}

@Composable
fun ReportCard(
    totals: Totals,
    baseCurrencyInfo: CurrencyFormat
) {
    Card(
    ) {
        Box(
            modifier = Modifier
                .padding(24.dp)
                .height(240.dp)
                .fillMaxWidth()
        ) {
            // Add legend items
            Column{
                LegendItem("Income", MaterialTheme.colorScheme.primary)
                LegendItem("Expense", MaterialTheme.colorScheme.error)
            }
            PieChart(
                pieChartData = PieChartData(
                    listOf(
                        PieChartData.Slice(
                            totals.income.toFloat(),
                            MaterialTheme.colorScheme.primary
                        ), PieChartData.Slice(
                            totals.expenses.toFloat(),
                            MaterialTheme.colorScheme.error,
                        )
                    )
                ),
                // Optional properties.
                modifier = Modifier
                    .fillMaxSize()
                    .offset(36.dp),
                animation = simpleChartAnimation(),
                sliceDrawer = SimpleSliceDrawer()
            )
        }
    }
}