package com.seyone22.expensetracker.ui.screen.home.composables

import android.icu.text.DecimalFormat
import android.text.Layout
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material.icons.outlined.ArrowDropUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
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
import com.seyone22.expensetracker.data.model.CurrencyFormat
import com.seyone22.expensetracker.ui.common.FormattedCurrency
import com.seyone22.expensetracker.ui.screen.home.HomeViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate
import kotlin.math.absoluteValue

@Composable
fun MySpending(
    baseCurrencyInfo: CurrencyFormat,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel
) {
    val expensesByWeek by remember { viewModel.expensesByWeekFlow }.collectAsState(emptyList())
    Log.d("TAG", "MySpending: $expensesByWeek")

    // Observe the current week start and end dates
    val currentWeekStart = LocalDate.parse(viewModel.currentStartDate.value)
    val currentWeekEnd = LocalDate.parse(viewModel.currentEndDate.value)
    val previousWeekStart = LocalDate.parse(viewModel.currentStartDate.value).minusWeeks(1)
    val previousWeekEnd = LocalDate.parse(viewModel.currentEndDate.value).minusWeeks(1)

    // Initialize a list of 7 zeros
    val seriesState = remember { mutableStateOf(List(7) { 0.0 }) }
    val currentWeekSum = remember { mutableStateOf(0.0) }

    // Sum up the total expenses from the list
    val previousWeekSum = expensesByWeek.filter { (_, _, date) ->
        date?.let { LocalDate.parse(it) in previousWeekStart..previousWeekEnd } == true
    }.sumOf { it.balance.absoluteValue * -1 }

    Log.d("TAG", "MySpending: $previousWeekSum")

    val percentageChange = if (previousWeekSum != 0.0) {
        ((currentWeekSum.value - previousWeekSum) / previousWeekSum) * 100
    } else {
        if (currentWeekSum.value == 0.0) 0.0 else 100.0  // If both weeks are 0, change is 0%; if previous week was 0 but current has value, it's a full increase
    }
    val modelProducer = remember { CartesianChartModelProducer() }

    LaunchedEffect(expensesByWeek) {
        // Change the week sum
        currentWeekSum.value = expensesByWeek.filter { (_, _, date) ->
            date?.let { LocalDate.parse(it) in currentWeekStart..currentWeekEnd } == true
        }.sumOf { it.balance.absoluteValue * -1 }

        withContext(Dispatchers.Default) {
            // Create a mutable list of 7 zeros
            val updatedSeries = MutableList(7) { 0.0 }

            expensesByWeek.filter { (_, _, date) ->
                date?.let { LocalDate.parse(it) in currentWeekStart..currentWeekEnd } == true
            }.forEach { (_, balance, date) ->
                val dayIndex = date?.let {
                    (LocalDate.parse(it).dayOfWeek.value - 1) % 7  // Ensure correct day index
                }

                if (dayIndex != null && dayIndex in 0..6) {
                    updatedSeries[dayIndex] = balance.times(-1)
                }
            }

            withContext(Dispatchers.Main) {
                seriesState.value = updatedSeries
            }
        }

        modelProducer.runTransaction {
            columnSeries {
                series(y = seriesState.value)
            }
        }
    }

    OutlinedCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text("My Spending", color = Color.Gray)
                FormattedCurrency(
                    style = MaterialTheme.typography.headlineSmall,
                    value = currentWeekSum.value.times(-1),
                    currency = baseCurrencyInfo,
                )
                val changeColor =
                    if (percentageChange <= 0) Color(0xff50b381) else Color(0xffd9534f)
                val changeIcon =
                    if (percentageChange <= 0) Icons.Outlined.ArrowDropDown else Icons.Outlined.ArrowDropUp

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = changeIcon,
                        contentDescription = null,
                        tint = changeColor,
                        modifier = Modifier
                            .padding(0.dp)
                            .width(13.dp)
                            .height(13.dp)
                    )
                    Text(
                        "${DecimalFormat("#.##").format(percentageChange)}% ",
                        color = changeColor,
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text("from prev. week", style = MaterialTheme.typography.bodySmall)

                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    IconButton({ viewModel.getPreviousWeek() }) {
                        Icon(
                            Icons.Default.ChevronLeft,
                            ""
                        )
                    }
                    Text(
                        modifier = Modifier.align(Alignment.CenterVertically),
                        text = "${currentWeekStart.dayOfMonth}/${currentWeekStart.monthValue} - ${currentWeekEnd.dayOfMonth}/${currentWeekEnd.monthValue}"
                    )
                    IconButton({ viewModel.getNextWeek() }) { Icon(Icons.Default.ChevronRight, "") }
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
                        guideline = null,
                        line = null,
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
                        ),
                    )
                ),
                modelProducer = modelProducer,
            )
        }
    }
}

private val bottomAxisValueFormatter = CartesianValueFormatter { _, x, _ ->
    "MTWTFSS"[x.toInt()].toString()
}