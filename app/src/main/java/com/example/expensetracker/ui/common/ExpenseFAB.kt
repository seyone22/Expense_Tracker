package com.example.expensetracker.ui.common

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.expensetracker.ui.screen.home.HomeDestination
import com.example.expensetracker.ui.screen.budget.BudgetsDestination
import com.example.expensetracker.ui.screen.entities.EntitiesDestination
import com.example.expensetracker.ui.screen.report.ReportsDestination
import com.example.expensetracker.ui.screen.transactions.TransactionsDestination

@Composable
fun ExpenseFAB(
    modifier: Modifier = Modifier,
    extended: Boolean,
    navigateToScreen: (screen: String) -> Unit,
    currentActivity: String
) {
    if ((currentActivity == HomeDestination.route) or (currentActivity == EntitiesDestination.route) or (currentActivity == BudgetsDestination.route) or (currentActivity == TransactionsDestination.route) or (currentActivity == ReportsDestination.route)) {
        FloatingActionButton(onClick = {
            navigateToScreen("TransactionEntry")
        }) {
            Row(
                modifier = Modifier.padding(12.dp, 0.dp)
            ) {
                Icon(Icons.Outlined.Edit, "Add")

                AnimatedVisibility(visible = extended) {
                    Row() {
                        Spacer(Modifier.width(12.dp))
                        Text(text = "New Transaction")
                    }
                }
            }
        }
    }
}