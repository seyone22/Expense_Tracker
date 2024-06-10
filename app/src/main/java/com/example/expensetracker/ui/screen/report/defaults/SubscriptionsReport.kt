package com.example.expensetracker.ui.screen.report.defaults

import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.expensetracker.data.model.Transaction
import com.example.expensetracker.ui.screen.report.ReportViewModel

@Composable
fun SubscriptionsReport(
    modifier: Modifier,
    viewModel: ReportViewModel,
    name: String = "Subscriptions",
) {
    val data = listOf(Transaction())

    Card(
        modifier = modifier
    ) {

    }
}