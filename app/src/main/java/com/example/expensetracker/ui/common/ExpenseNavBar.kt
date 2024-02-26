package com.example.expensetracker.ui.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.CompareArrows
import androidx.compose.material.icons.automirrored.outlined.TextSnippet
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.Balance
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringArrayResource
import com.example.expensetracker.R
import com.example.expensetracker.ui.navigation.NavigationDestination
import com.example.expensetracker.ui.screen.accounts.AccountsDestination
import com.example.expensetracker.ui.screen.entities.EntitiesDestination
import com.example.expensetracker.ui.screen.report.BudgetsDestination
import com.example.expensetracker.ui.screen.report.ReportsDestination
import com.example.expensetracker.ui.screen.transactions.TransactionsDestination
import com.example.expensetracker.ui.utils.ExpenseNavigationType

data class ActivityIconPair(
    val name : String,
    val destination: NavigationDestination,
    val icon: ImageVector
)

val activitiesAndIcons = listOf(
    ActivityIconPair(name = "Accounts", AccountsDestination, Icons.Outlined.AccountBalanceWallet),
    ActivityIconPair(name = "Entities", EntitiesDestination, Icons.Outlined.AccountBalance),
    ActivityIconPair(name = "Budgets", BudgetsDestination, Icons.Outlined.Balance),
    ActivityIconPair(name = "Entries", TransactionsDestination, Icons.AutoMirrored.Outlined.CompareArrows),
    ActivityIconPair(name = "Reports", ReportsDestination, Icons.AutoMirrored.Outlined.TextSnippet),
)

@Composable
fun ExpenseNavBar(
    currentActivity : String?,
    navigateToScreen : (screen: String) -> Unit,
    type : ExpenseNavigationType = ExpenseNavigationType.BOTTOM_NAVIGATION

) {
    if(type == ExpenseNavigationType.BOTTOM_NAVIGATION) {
        NavigationBar(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ) {
            activitiesAndIcons.forEachIndexed { index, pair ->
                NavigationBarItem(
                    icon = { Icon(pair.icon, contentDescription = pair.destination.route) },
                    label = { Text(pair.destination.route) },
                    selected = currentActivity == pair.name,
                    onClick = {
                        navigateToScreen(pair.destination.route)
                    }
                )
            }
        }
    } else {
        NavigationRail(
        ) {
            Row(
                modifier = Modifier.fillMaxHeight(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    activitiesAndIcons.forEachIndexed {index, pair ->
                        NavigationRailItem(
                            selected = currentActivity == pair.name,
                            label = {Text(pair.destination.route)},
                            onClick = { navigateToScreen(pair.destination.route) },
                            icon = { Icon(pair.icon, contentDescription = pair.destination.route) },
                            alwaysShowLabel = false)
                    }
                }
            }
        }
    }
}