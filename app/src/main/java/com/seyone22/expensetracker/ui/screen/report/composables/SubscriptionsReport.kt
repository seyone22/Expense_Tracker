package com.seyone22.expensetracker.ui.screen.report.composables

import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.seyone22.expensetracker.data.model.Transaction
import com.seyone22.expensetracker.ui.screen.report.ReportViewModel

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