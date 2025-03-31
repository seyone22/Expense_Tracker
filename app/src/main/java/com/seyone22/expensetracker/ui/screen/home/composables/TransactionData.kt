package com.seyone22.expensetracker.ui.screen.home.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.seyone22.expensetracker.ui.screen.home.HomeUiState
import com.seyone22.expensetracker.ui.screen.transactions.TransactionsDestination
import com.seyone22.expensetracker.ui.screen.transactions.composables.TransactionList

@Composable
fun TransactionData(
    modifier: Modifier = Modifier,
    accountsUiState: HomeUiState,
    navigateToScreen: (screen: String) -> Unit,
) {
    Column(
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Recent Transactions",
                style = MaterialTheme.typography.titleLarge,
            )
            TextButton(
                onClick = {
                    navigateToScreen(TransactionsDestination.route)
                },
            ) {
                Text(
                    text = "View All"
                )
            }
        }

        TransactionList(
            showFilter = false,
            count = 5,
        )
    }
}

