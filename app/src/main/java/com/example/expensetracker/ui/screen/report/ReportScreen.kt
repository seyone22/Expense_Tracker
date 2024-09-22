package com.example.expensetracker.ui.screen.report

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.expensetracker.R
import com.example.expensetracker.data.model.Report
import com.example.expensetracker.ui.AppViewModelProvider
import com.example.expensetracker.ui.navigation.NavigationDestination
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottomAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStartAxis
import com.patrykandpatrick.vico.compose.cartesian.fullWidth
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoScrollState
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoZoomState
import com.patrykandpatrick.vico.compose.common.component.rememberLineComponent
import com.patrykandpatrick.vico.core.cartesian.HorizontalLayout
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.core.cartesian.data.columnSeries
import com.patrykandpatrick.vico.core.cartesian.layer.ColumnCartesianLayer
import com.patrykandpatrick.vico.core.common.data.ExtraStore
import com.patrykandpatrick.vico.core.common.shape.Shape

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
    setTopBarAction: (Int) -> Unit
) {
    val reports by viewModel.reportsFlow.collectAsState(initial = listOf())

    LaunchedEffect(Unit) {
        setTopBarAction(12)
    }

    LazyVerticalGrid(modifier = modifier, columns = GridCells.Adaptive(minSize = 320.dp)) {
        items(reports.size) { index ->
            ReportCard(
                modifier = Modifier,
                viewModel = viewModel,
                report = reports[index]
            )
        }
    }
}

@Composable
fun ReportCard(
    modifier: Modifier = Modifier,
    viewModel: ReportViewModel,
    report: Report,
    category: Int = 1,
) {
    val categoryName = remember { mutableStateOf("") }
    val values = remember { mutableStateOf<Map<Int?, Double>>(emptyMap()) }

    val labelListKey = remember { ExtraStore.Key<List<String>>() }

    val scrollState = rememberVicoScrollState()
    val zoomState = rememberVicoZoomState()

    val modelProducer = remember { CartesianChartModelProducer() }

    // Use LaunchedEffect to run a coroutine within the Composable
    LaunchedEffect(Unit) {
        categoryName.value = viewModel.categoryNameOf(report.GROUPNAME?.toInt() ?: -1)
        values.value = viewModel.getExpensesFromCategory(report.GROUPNAME?.toInt() ?: -1 )

        val doubleValues = values.value.values.toList()
        val keysList = values.value.keys.toList()

        val resultsList = keysList.map { key ->
            viewModel.categoryNameOf(key ?: 0)
        }
        modelProducer.runTransaction {
            columnSeries { series(doubleValues) }
            extras { it[labelListKey] = resultsList }
        }
    }
    val xx =
        CartesianValueFormatter { x, chartValues, _ -> chartValues.model.extraStore[labelListKey][x.toInt()] }

    Card(
        modifier = modifier
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .height(240.dp)
                .fillMaxSize()
        ) {
            Text(text = "By Category - ${categoryName.value}")

            CartesianChartHost(
                chart = rememberCartesianChart(
                    rememberColumnCartesianLayer(
                        ColumnCartesianLayer.ColumnProvider.series(
                            rememberLineComponent(
                                color = MaterialTheme.colorScheme.primary,
                                thickness = 16.dp,
                                shape = remember { Shape.rounded(allPercent = 24) },
                            )
                        )
                    ),
                    startAxis = rememberStartAxis(),
                    bottomAxis =
                    rememberBottomAxis(
                        valueFormatter = xx,
                        itemPlacer =
                        remember {
                            HorizontalAxis.ItemPlacer.default(
                                spacing = 1,
                                addExtremeLabelPadding = true
                            )
                        },
                    ),
                    horizontalLayout = HorizontalLayout.fullWidth(),
                ),
                scrollState = scrollState,
                zoomState = zoomState,
                modelProducer = modelProducer,
                placeholder = {
                    Text(text = "Error")
                },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

