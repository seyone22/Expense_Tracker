package com.example.expensetracker.ui.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.expensetracker.AccountScreen
import com.example.expensetracker.AccountsDestination
import com.example.expensetracker.EntityDestination
import com.example.expensetracker.EntityScreen
import com.example.expensetracker.ui.account.AccountEntryDestination
import com.example.expensetracker.ui.account.AccountEntryScreen

/**
 * Provides Navigation graph for the application.
 */
@Composable
fun ExpenseNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = AccountsDestination.route,
        modifier = modifier
    ) {
        composable(route = AccountsDestination.route) {
            AccountScreen(
                navigateToAccountEntry = { navController.navigate(AccountEntryDestination.route) },
                navigateToScreen = { screen -> navController.navigate(screen) }
            )
        }
        composable(route = AccountEntryDestination.route) {
            AccountEntryScreen(
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() }
            )
        }
        composable(route = EntityDestination.route) {
            EntityScreen(

            )
        }
        composable(route = AccountEntryDestination.route) {
            AccountEntryScreen(
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() }
            )
        }
    }
}
