package com.seyone22.expensetracker.ui.screen.report.composables

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.seyone22.expensetracker.SharedViewModel
import com.seyone22.expensetracker.data.model.CurrencyFormat
import com.seyone22.expensetracker.ui.AppViewModelProvider
import com.seyone22.expensetracker.ui.common.FormattedCurrency
import com.seyone22.expensetracker.ui.common.TransactionType
import com.seyone22.expensetracker.ui.common.removeTrPrefix
import com.seyone22.expensetracker.ui.screen.report.ReportViewModel
import com.seyone22.expensetracker.utils.RepeatsFieldHelper

@Composable
fun SubscriptionsReport(
    modifier: Modifier,
    viewModel: ReportViewModel,
    name: String = "Subscriptions",
) {
    val activeSubscriptions by viewModel.activeSubscriptionsFlow.collectAsState(initial = emptyList())
    val sharedViewModel: SharedViewModel = viewModel(factory = AppViewModelProvider.Factory)
    val baseCurrency by sharedViewModel.baseCurrencyFlow.collectAsState(initial = CurrencyFormat())

    // Filter only withdrawals/debits as active subscriptions (e.g. rent, streaming bills)
    val subscriptions = activeSubscriptions.filter { it.TRANSCODE == "Withdrawal" }

    // Calculate total projected monthly cost
    val totalMonthlyCost = subscriptions.sumOf { item ->
        val repeatsCode = item.REPEATS ?: 0
        val (_, _, recurrenceType) = RepeatsFieldHelper.decode(repeatsCode)
        val amount = item.TRANSAMOUNT
        when (recurrenceType) {
            com.seyone22.expensetracker.data.constants.RecurrenceType.DAILY -> amount * 30.0
            com.seyone22.expensetracker.data.constants.RecurrenceType.WEEKLY -> amount * 4.33
            com.seyone22.expensetracker.data.constants.RecurrenceType.EVERY_2_WEEKS -> amount * 2.16
            com.seyone22.expensetracker.data.constants.RecurrenceType.FOUR_WEEKS -> amount * 1.08
            com.seyone22.expensetracker.data.constants.RecurrenceType.MONTHLY -> amount
            com.seyone22.expensetracker.data.constants.RecurrenceType.EVERY_2_MONTHS -> amount / 2.0
            com.seyone22.expensetracker.data.constants.RecurrenceType.QUARTERLY -> amount / 3.0
            com.seyone22.expensetracker.data.constants.RecurrenceType.FOUR_MONTHS -> amount / 4.0
            com.seyone22.expensetracker.data.constants.RecurrenceType.HALF_YEARLY -> amount / 6.0
            com.seyone22.expensetracker.data.constants.RecurrenceType.YEARLY -> amount / 12.0
            else -> amount
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Summary Card
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Projected Monthly Cost",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(modifier = Modifier.height(8.dp))
                FormattedCurrency(
                    value = totalMonthlyCost,
                    currency = baseCurrency ?: CurrencyFormat(),
                    style = MaterialTheme.typography.headlineLarge,
                    defaultColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Based on ${subscriptions.size} active recurring bills",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                )
            }
        }

        Text(
            text = "Active Reminders & Subscriptions",
            style = MaterialTheme.typography.titleLarge
        )

        if (subscriptions.isNotEmpty()) {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(subscriptions) { subscription ->
                    val repeatsCode = subscription.REPEATS ?: 0
                    val (_, _, recurrenceType) = RepeatsFieldHelper.decode(repeatsCode)

                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = subscription.payeeName ?: "Transfer",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    text = removeTrPrefix(subscription.categName),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                                )
                                Text(
                                    text = "Frequency: ${recurrenceType.description}  |  Next Due: ${subscription.NEXTOCCURRENCEDATE}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                )
                            }
                            FormattedCurrency(
                                value = subscription.TRANSAMOUNT,
                                currency = baseCurrency ?: CurrencyFormat(),
                                style = MaterialTheme.typography.titleLarge,
                                type = TransactionType.DEBIT,
                                defaultColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "No active recurring bills or subscriptions found.",
                    fontStyle = FontStyle.Italic,
                    color = Color.Gray
                )
            }
        }
    }
}