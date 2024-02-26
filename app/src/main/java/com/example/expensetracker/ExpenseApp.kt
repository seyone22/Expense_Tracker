package com.example.expensetracker

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.expensetracker.ui.common.ExpenseFAB
import com.example.expensetracker.ui.common.ExpenseNavBar
import com.example.expensetracker.ui.common.ExpenseTopBar
import com.example.expensetracker.ui.navigation.ExpenseNavHost
import com.example.expensetracker.ui.screen.accounts.AccountsDestination
import com.example.expensetracker.ui.screen.operations.account.AccountEntryDestination
import com.example.expensetracker.ui.screen.operations.entity.payee.PayeeEntryDestination
import com.example.expensetracker.ui.screen.operations.transaction.TransactionEntryDestination
import com.example.expensetracker.ui.screen.settings.SettingsDestination
import com.example.expensetracker.ui.utils.ExpenseNavigationType

@Composable
fun ExpenseApp(
    navController: NavHostController = rememberNavController(),
    windowSizeClass: WindowWidthSizeClass
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()

    val navigationType: ExpenseNavigationType = when (windowSizeClass) {
        WindowWidthSizeClass.Compact -> ExpenseNavigationType.BOTTOM_NAVIGATION
        WindowWidthSizeClass.Medium -> ExpenseNavigationType.NAVIGATION_RAIL
        WindowWidthSizeClass.Expanded -> {
            // Need to fix this!!
            if (false) {
                ExpenseNavigationType.NAVIGATION_RAIL
            } else {
                ExpenseNavigationType.PERMANENT_NAVIGATION_DRAWER
            }
        }

        else -> ExpenseNavigationType.BOTTOM_NAVIGATION
    }

    Row(
    ) {
        if ((navigationType == ExpenseNavigationType.NAVIGATION_RAIL) or (navigationType == ExpenseNavigationType.PERMANENT_NAVIGATION_DRAWER)) {
            ExpenseNavBar(
                navigateToScreen = { screen -> navController.navigate(screen) },
                type = navigationType,
                currentActivity = navBackStackEntry?.destination?.route
            )
        }
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,

            topBar = {
                ExpenseTopBar(
                    selectedActivity = AccountsDestination.routeId,
                    navBarAction = {
                                   when (navBackStackEntry?.destination?.route) {
                                       "Accounts" -> navController.navigate(AccountEntryDestination.route)
                                       "Entities" -> navController.navigate(PayeeEntryDestination.route)
                                       "Entries" -> navController.navigate(TransactionEntryDestination.route)
                                   }
                    },
                    navigateToSettings = { navController.navigate(SettingsDestination.route) }
                )
            },
            bottomBar = {
                if (navigationType == ExpenseNavigationType.BOTTOM_NAVIGATION) {
                    ExpenseNavBar(
                        navigateToScreen = { screen -> navController.navigate(screen) },
                        type = navigationType,
                        currentActivity = navBackStackEntry?.destination?.route
                    )
                }
            },

            floatingActionButton = {
                ExpenseFAB(navigateToScreen = { screen -> navController.navigate(screen) })
            }
        ) { innerPadding ->
            ExpenseNavHost(
                navController = navController,
                windowSizeClass = windowSizeClass,
                innerPadding = innerPadding
            )
        }
    }
}