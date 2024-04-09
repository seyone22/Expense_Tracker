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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.expensetracker.R
import com.example.expensetracker.model.CurrencyFormat
import com.example.expensetracker.ui.AppViewModelProvider
import com.example.expensetracker.ui.navigation.NavigationDestination
import com.example.expensetracker.ui.screen.accounts.LegendItem
import com.example.expensetracker.ui.screen.accounts.Totals
import com.github.tehras.charts.piechart.PieChart
import com.github.tehras.charts.piechart.PieChartData
import com.github.tehras.charts.piechart.animation.simpleChartAnimation
import com.github.tehras.charts.piechart.renderer.SimpleSliceDrawer
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.CartesianChartHost
import com.patrykandpatrick.vico.compose.chart.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.chart.rememberCartesianChart
import com.patrykandpatrick.vico.compose.chart.scroll.rememberVicoScrollState
import com.patrykandpatrick.vico.compose.chart.zoom.rememberVicoZoomState
import com.patrykandpatrick.vico.compose.m3.theme.rememberM3VicoTheme
import com.patrykandpatrick.vico.compose.theme.ProvideVicoTheme
import com.patrykandpatrick.vico.core.axis.AxisPosition
import com.patrykandpatrick.vico.core.axis.formatter.AxisValueFormatter
import com.patrykandpatrick.vico.core.chart.layer.ColumnCartesianLayer.ColumnProvider.Companion.series
import com.patrykandpatrick.vico.core.model.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.model.ExtraStore
import com.patrykandpatrick.vico.core.model.columnSeries
import java.time.LocalDate
import java.time.Month
import java.time.format.DateTimeFormatter

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
    var mp: CartesianChartModelProducer? by remember { mutableStateOf(null) }
    var mp2: CartesianChartModelProducer? by remember { mutableStateOf(null) }

    LaunchedEffect(Unit) {
        mp = viewModel.getExpensesFromCategory("31")
        mp2 = viewModel.getExpensesFromPayee("1")
    }


    LazyVerticalGrid(modifier = modifier, columns = GridCells.Adaptive(minSize = 320.dp)) {
        item {
            mp?.let { ReportCard(modifier, it) }
        }
        item {
            mp2?.let { ReportCard(modifier, it) }
        }
    }
}

@Composable
fun ReportCard(
    modifier: Modifier,
    modelProducer: CartesianChartModelProducer
) {
    val scrollState = rememberVicoScrollState()
    val zoomState = rememberVicoZoomState()
    
    Card(
        modifier = modifier
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .height(240.dp)
                .fillMaxWidth()
        ) {
            Text(text = "By Category - Dental")

            ProvideVicoTheme(theme = rememberM3VicoTheme()) {
                CartesianChartHost(
                    chart = rememberCartesianChart(
                        rememberColumnCartesianLayer(),
                        startAxis = rememberStartAxis(),
                        bottomAxis = rememberBottomAxis()
                    ),
                    modelProducer = modelProducer,
                    scrollState = scrollState,
                    zoomState = zoomState
                )
            }
        }
    }
}