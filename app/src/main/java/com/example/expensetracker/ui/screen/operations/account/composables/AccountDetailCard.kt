package com.example.expensetracker.ui.screen.operations.account.composables

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
import com.example.expensetracker.ui.screen.operations.account.AccountDetailAccountUiState
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLine
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.marker.rememberDefaultCartesianMarker
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.common.component.rememberShapeComponent
import com.patrykandpatrick.vico.compose.common.component.rememberTextComponent
import com.patrykandpatrick.vico.compose.common.dimensions
import com.patrykandpatrick.vico.compose.common.fill
import com.patrykandpatrick.vico.compose.common.shape.markerCorneredShape
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.patrykandpatrick.vico.core.cartesian.layer.LineCartesianLayer
import com.patrykandpatrick.vico.core.common.component.TextComponent
import com.patrykandpatrick.vico.core.common.shape.Corner

@Composable
fun AccountDetailCard(
    modifier: Modifier,
    accountDetailUiState: AccountDetailAccountUiState,
) {
    // Initialize seriesState with a default value (e.g., list of 0.0)
    var seriesState by remember { mutableStateOf(List(2) { 10.0 }) }

    val modelProducer = remember { CartesianChartModelProducer() }
    LaunchedEffect(accountDetailUiState.balanceHistory) {
        Log.d("TAG", "AccountDetailCard: ${accountDetailUiState.balanceHistory}")
        // Extract balance values into seriesState once data is available
        if (accountDetailUiState.balanceHistory.isNotEmpty()) {
            seriesState = accountDetailUiState.balanceHistory.map { (_, balance, _) ->
                balance
            }
        }

        // Update the model producer with the new series data
        modelProducer.runTransaction {
            lineSeries {
                series(y = seriesState)
            }
        }
    }

    val primary = MaterialTheme.colorScheme.primary.toArgb()


    Card(
        modifier = modifier.fillMaxWidth().height(200.dp),
        colors = CardDefaults.cardColors().copy(containerColor = MaterialTheme.colorScheme.background)
    ) {
        CartesianChartHost(
            modifier = Modifier
                .height(100.dp)
                .weight(1f),
            chart = rememberCartesianChart(
                rememberLineCartesianLayer(
                    LineCartesianLayer.LineProvider.series(
                        LineCartesianLayer.rememberLine(
                            fill = remember { LineCartesianLayer.LineFill.single(fill(Color(primary))) },
                            pointConnector = remember { LineCartesianLayer.PointConnector.cubic(curvature = 0f) },
                        )
                    )
                ),
                startAxis = VerticalAxis.rememberStart(),
                bottomAxis = HorizontalAxis.rememberBottom(

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