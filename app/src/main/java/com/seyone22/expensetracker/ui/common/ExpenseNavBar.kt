package com.seyone22.expensetracker.ui.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.CompareArrows
import androidx.compose.material.icons.automirrored.filled.TextSnippet
import androidx.compose.material.icons.automirrored.outlined.CompareArrows
import androidx.compose.material.icons.automirrored.outlined.TextSnippet
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Balance
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.Balance
import androidx.compose.material.icons.outlined.Settings
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
import androidx.compose.ui.unit.dp
import com.seyone22.expensetracker.ui.navigation.NavigationDestination
import com.seyone22.expensetracker.ui.screen.budget.BudgetsDestination
import com.seyone22.expensetracker.ui.screen.entities.EntitiesDestination
import com.seyone22.expensetracker.ui.screen.home.HomeDestination
import com.seyone22.expensetracker.ui.screen.report.ReportsDestination
import com.seyone22.expensetracker.ui.screen.settings.SettingsDestination
import com.seyone22.expensetracker.ui.screen.transactions.TransactionsDestination
import com.seyone22.expensetracker.utils.ExpenseNavigationType

data class ActivityIconPair(
    val name: String,
    val destination: NavigationDestination,
    val icon: ImageVector,
    val iconSelected: ImageVector
)

val activitiesAndIcons = listOf(
    ActivityIconPair(
        name = "Home",
        HomeDestination,
        Icons.Outlined.AccountBalanceWallet,
        Icons.Filled.AccountBalanceWallet
    ),
    ActivityIconPair(
        name = "Entities",
        EntitiesDestination,
        Icons.Outlined.AccountBalance,
        Icons.Filled.AccountBalance
    ),
    ActivityIconPair(
        name = "Budgets",
        BudgetsDestination,
        Icons.Outlined.Balance,
        Icons.Filled.Balance
    ),
    ActivityIconPair(
        name = "Entries",
        TransactionsDestination,
        Icons.AutoMirrored.Outlined.CompareArrows,
        Icons.AutoMirrored.Filled.CompareArrows
    ),
    ActivityIconPair(
        name = "Reports",
        ReportsDestination,
        Icons.AutoMirrored.Outlined.TextSnippet,
        Icons.AutoMirrored.Filled.TextSnippet
    ),
)

@Composable
fun ExpenseNavBar(
    currentActivity: String?,
    navigateToScreen: (screen: String) -> Unit,
    type: ExpenseNavigationType = ExpenseNavigationType.BOTTOM_NAVIGATION
) {
    if ((currentActivity == HomeDestination.route) or (currentActivity == EntitiesDestination.route) or (currentActivity == BudgetsDestination.route) or (currentActivity == TransactionsDestination.route) or (currentActivity == ReportsDestination.route)) {
        if (type == ExpenseNavigationType.BOTTOM_NAVIGATION) {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surfaceContainer
            ) {
                activitiesAndIcons.forEachIndexed { _, pair ->
                    NavigationBarItem(
                        icon = {
                            Icon(
                                imageVector = if (currentActivity == pair.name) {
                                    pair.iconSelected
                                } else {
                                    pair.icon
                                },
                                contentDescription = pair.destination.route,
                            )
                        },
                        label = { Text(pair.destination.route) },
                        selected = currentActivity == pair.name,
                        onClick = {
                            navigateToScreen(pair.destination.route)
                        }
                    )
                }
            }
        } else {
            NavigationRail {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(8.dp))
                    if (currentActivity != null) {
                        ExpenseFAB(
                            navigateToScreen = { screen -> navigateToScreen(screen) },
                            currentActivity = currentActivity,
                            extended = false
                        )
                    }
                    Spacer(modifier = Modifier.height(200.dp))
                    activitiesAndIcons.forEachIndexed { _, pair ->
                        NavigationRailItem(
                            selected = currentActivity == pair.name,
                            label = { Text(pair.destination.route) },
                            onClick = { navigateToScreen(pair.destination.route) },
                            icon = {
                                Icon(
                                    imageVector = if (currentActivity == pair.name) {
                                        pair.iconSelected
                                    } else {
                                        pair.icon
                                    },
                                    contentDescription = pair.destination.route,
                                )
                            },
                            alwaysShowLabel = false
                        )
                    }

                    NavigationRailItem(
                        selected = currentActivity == SettingsDestination.route,
                        label = { Text(SettingsDestination.route) },
                        onClick = { navigateToScreen(SettingsDestination.route) },
                        icon = {
                            Icon(
                                Icons.Outlined.Settings,
                                contentDescription = SettingsDestination.route
                            )
                        },
                        alwaysShowLabel = false
                    )
                }
            }
        }
    }
}