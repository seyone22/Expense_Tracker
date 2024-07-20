package com.example.expensetracker.ui.screen.accounts.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.expensetracker.data.model.CurrencyFormat
import com.example.expensetracker.ui.screen.accounts.Totals
import com.github.tehras.charts.piechart.PieChart
import com.github.tehras.charts.piechart.PieChartData
import com.github.tehras.charts.piechart.animation.simpleChartAnimation
import com.github.tehras.charts.piechart.renderer.SimpleSliceDrawer

@Composable
fun Summary(totals: Totals, baseCurrencyInfo: CurrencyFormat) {
    Card(
    ) {
        Box(
            modifier = Modifier
                .padding(24.dp)
                .height(240.dp)
                .fillMaxWidth()
        ) {
            // Add legend items
            Column {
                LegendItem("Income", MaterialTheme.colorScheme.primary)
                LegendItem("Expense", Color(0xfff75e51))
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

@Composable
fun LegendItem(label: String, color: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(color)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = label)
    }
}