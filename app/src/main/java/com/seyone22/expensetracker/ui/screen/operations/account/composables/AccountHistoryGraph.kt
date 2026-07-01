package com.seyone22.expensetracker.ui.screen.operations.account.composables

import android.text.Layout
import android.util.Log
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLine
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.marker.rememberDefaultCartesianMarker
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.common.component.rememberLineComponent
import com.patrykandpatrick.vico.compose.common.component.rememberShapeComponent
import com.patrykandpatrick.vico.compose.common.component.rememberTextComponent
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.compose.cartesian.data.LineCartesianLayerModel
import com.patrykandpatrick.vico.compose.cartesian.data.columnModel
import com.patrykandpatrick.vico.compose.cartesian.data.lineModel
import com.patrykandpatrick.vico.compose.cartesian.data.lineSeries
import com.patrykandpatrick.vico.compose.cartesian.layer.ColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.layer.ColumnCartesianLayer.ColumnProvider.Companion.series
import com.patrykandpatrick.vico.compose.cartesian.layer.LineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.marker.rememberDefaultCartesianMarker
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.common.component.TextComponent
import com.patrykandpatrick.vico.compose.common.component.rememberLineComponent
import com.patrykandpatrick.vico.compose.common.component.rememberShapeComponent
import com.patrykandpatrick.vico.compose.common.component.rememberTextComponent
import com.seyone22.expensetracker.ui.screen.operations.account.AccountDetailUiState
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun AccountHistoryGraph(
    modifier: Modifier,
    accountDetailUiState: AccountDetailUiState,
) {
    val initialBalance = accountDetailUiState.account.initialBalance ?: 0.0
    // Initialize seriesState with a default line at the initial balance
    var seriesState by remember { mutableStateOf(List(7) { initialBalance }) }

    val modelProducer = remember { CartesianChartModelProducer() }
    LaunchedEffect(accountDetailUiState.balanceHistory, initialBalance) {
        // Extract balance values + initialBalance once data is available
        if (accountDetailUiState.balanceHistory.isNotEmpty()) {
            seriesState = accountDetailUiState.balanceHistory.map { (_, balance, _) ->
                balance + initialBalance
            }
        } else {
            seriesState = List(7) { initialBalance }
        }

        // Update the model producer with the new series data
        modelProducer.runTransaction {
            lineModel { series(y = seriesState) }
        }
    }

    val primaryColor = MaterialTheme.colorScheme.primary

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        shape = MaterialTheme.shapes.large
    ) {
        CartesianChartHost(
            modifier = Modifier
                .height(100.dp)
                .weight(1f),
            chart = rememberCartesianChart(
                rememberLineCartesianLayer(
                    LineCartesianLayer.LineProvider.series(
                        LineCartesianLayer.rememberLine(
                            pointConnector = remember {
                                LineCartesianLayer.PointConnector.cubic(
                                    curvature = 0.35f
                                )
                            },
                        )
                    )
                ),
                startAxis = VerticalAxis.rememberStart(
                    label = rememberTextComponent(
                    ),
                    line = null,
                    tick = null,
                    guideline = rememberLineComponent(
                        thickness = 1.dp
                    )
                ),
                bottomAxis = HorizontalAxis.rememberBottom(
                    label = rememberTextComponent(
                    ),
                    line = null,
                    tick = null,
                    valueFormatter = bottomAxisValueFormatter
                ), 
                marker = rememberDefaultCartesianMarker(
                    label = rememberTextComponent(
                        minWidth = TextComponent.MinWidth.fixed(40.dp),
                    )
                )
            ),
            modelProducer = modelProducer,
        )
    }
}

private val bottomAxisValueFormatter = CartesianValueFormatter { _, x, _ ->
    try {
        // Get today's date
        val today = LocalDate.now()

        // Calculate the date by subtracting x days from today
        val date = today.minusDays((6 - x).toLong())

        // Format the date as MM/dd
        date.format(DateTimeFormatter.ofPattern("MM/dd"))
    } catch (e: Exception) {
        "" // Fallback in case of errors
    }
}
