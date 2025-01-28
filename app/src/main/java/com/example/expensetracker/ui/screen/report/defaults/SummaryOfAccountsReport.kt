package com.example.expensetracker.ui.screen.report.defaults

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.expensetracker.data.model.Report
import com.example.expensetracker.ui.screen.report.ReportViewModel
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoScrollState
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoZoomState
import com.patrykandpatrick.vico.compose.common.component.rememberLineComponent
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.core.cartesian.data.columnSeries
import com.patrykandpatrick.vico.core.cartesian.layer.ColumnCartesianLayer
import com.patrykandpatrick.vico.core.common.data.ExtraStore

@Composable
fun SummaryOfAccountsReportCard(
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
        values.value = viewModel.getExpensesFromCategory(report.GROUPNAME?.toInt() ?: -1)

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
        CartesianValueFormatter { x, chartValues, _ -> chartValues.toString() }

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
                                thickness = 16.dp,
                            )
                        )
                    ),
                    startAxis = VerticalAxis.rememberStart(),
                    bottomAxis =
                    HorizontalAxis.rememberBottom(
                        valueFormatter = xx,
                        itemPlacer =
                        remember {
                            HorizontalAxis.ItemPlacer.aligned(
                                spacing = 1,
                                addExtremeLabelPadding = true
                            )
                        },
                    ),
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