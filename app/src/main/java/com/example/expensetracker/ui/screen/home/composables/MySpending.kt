package com.example.expensetracker.ui.screen.home.composables

import android.text.Layout
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowDropUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import com.example.expensetracker.data.model.CurrencyFormat
import com.example.expensetracker.data.repository.transaction.ExpensePerDay
import com.example.expensetracker.ui.common.FormattedCurrency
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
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
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.core.cartesian.data.columnSeries
import com.patrykandpatrick.vico.core.cartesian.layer.ColumnCartesianLayer
import com.patrykandpatrick.vico.core.common.component.TextComponent
import com.patrykandpatrick.vico.core.common.shape.Corner
import com.patrykandpatrick.vico.core.common.shape.CorneredShape
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun MySpending(expensesByWeek: List<ExpensePerDay>, baseCurrencyInfo: CurrencyFormat) {
    // Initialize a list of 7 zeros
    val seriesState = remember { mutableStateOf(List(7) { 0.0 }) }

    // Sum up the total expenses from the list
    val expenseSUM = expensesByWeek.sumOf { it.totalExpense }

    val modelProducer = remember { CartesianChartModelProducer() }
    LaunchedEffect(expensesByWeek) {
        withContext(Dispatchers.Default) {
            if (expensesByWeek.isNotEmpty()) {
                // Create a mutable list of 7 zeros
                val updatedSeries = MutableList(7) { 0.0 }
                // Update only the correct indices based on the data from expensesByWeek
                expensesByWeek.forEach { (day, expense) ->
                    val dayIndex = day.toIntOrNull()

                    if (dayIndex != null && dayIndex in 0..6) {
                        updatedSeries[dayIndex] = expense
                    }
                }

                // Update seriesState with the updated series
                seriesState.value = updatedSeries
            }

            modelProducer.runTransaction {
                columnSeries {
                    series(y = seriesState.value)
                }
            }
        }
    }

    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.Bottom
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text("My Spending", color = Color.Gray)
                FormattedCurrency(
                    value = expenseSUM,
                    currency = baseCurrencyInfo,
                    style = MaterialTheme.typography.headlineMedium
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ArrowDropUp,
                        contentDescription = null,
                        tint = Color(0xff50b381),
                        modifier = Modifier
                            .padding(0.dp)
                            .width(13.dp)
                            .height(13.dp)
                    )
                    Text(
                        "4.9% ",
                        color = Color(0xff50b381),
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text("From last week", style = MaterialTheme.typography.bodySmall)
                }
            }

            CartesianChartHost(
                modifier = Modifier
                    .height(100.dp)
                    .weight(1f),
                chart = rememberCartesianChart(
                    rememberColumnCartesianLayer(
                        columnCollectionSpacing = 2.dp,
                        columnProvider = ColumnCartesianLayer.ColumnProvider.series(
                            rememberLineComponent(
                                fill = fill(Color(MaterialTheme.colorScheme.primary.toArgb())),
                                thickness = 8.dp,
                                shape = CorneredShape.rounded(allPercent = 16),
                            )
                        )
                    ), bottomAxis = HorizontalAxis.rememberBottom(
                        guideline = null, line = null,
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
}

private val bottomAxisValueFormatter = CartesianValueFormatter { _, x, _ ->
    "SMTWTFS"[x.toInt()].toString()
}