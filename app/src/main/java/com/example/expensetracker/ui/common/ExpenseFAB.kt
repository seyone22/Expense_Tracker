package com.example.expensetracker.ui.common

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import com.example.expensetracker.ui.screen.accounts.AccountsDestination
import com.example.expensetracker.ui.screen.entities.EntitiesDestination
import com.example.expensetracker.ui.screen.report.BudgetsDestination
import com.example.expensetracker.ui.screen.report.ReportsDestination
import com.example.expensetracker.ui.screen.transactions.TransactionsDestination

@Composable
fun ExpenseFAB(
    navigateToScreen: (screen: String) -> Unit,
    currentActivity: String
) {
    if ((currentActivity == AccountsDestination.route) or (currentActivity == EntitiesDestination.route) or (currentActivity == BudgetsDestination.route) or (currentActivity == TransactionsDestination.route) or (currentActivity == ReportsDestination.route)) {
        FloatingActionButton(onClick = {
            navigateToScreen("TransactionEntry")
        }) {
            Icon(Icons.Outlined.Edit, "Add")
        }
    }
}