package com.example.expensetracker.ui.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.expensetracker.model.Account
import com.example.expensetracker.ui.account.AccountDetailDestination
import com.example.expensetracker.ui.account.AccountDetailScreen
import com.example.expensetracker.ui.account.AccountDetailViewModel
import com.example.expensetracker.ui.screen.accounts.AccountScreen
import com.example.expensetracker.ui.screen.accounts.AccountsDestination
import com.example.expensetracker.ui.screen.entities.EntitiesDestination
import com.example.expensetracker.ui.screen.entities.EntityScreen
import com.example.expensetracker.ui.account.AccountEntryDestination
import com.example.expensetracker.ui.account.AccountEntryScreen
import com.example.expensetracker.ui.transaction.TransactionEntryDestination
import com.example.expensetracker.ui.transaction.TransactionEntryScreen

/**
 * Provides Navigation graph for the application.
 */
@Composable
fun ExpenseNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = AccountsDestination.route,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None }
    ) {
        // Routes to main Navbar destinations
        composable(route = AccountsDestination.route) {
            AccountScreen(
                navigateToScreen = { screen -> navController.navigate(screen) }
            )
        }
        composable(route = EntitiesDestination.route) {
            EntityScreen(
                navigateToScreen = { screen -> navController.navigate(screen) },
            )
        }
        // Routes to pages for CRUD operations
        composable(route = AccountEntryDestination.route) {
            AccountEntryScreen(
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() }
            )
        }
        composable(route = AccountDetailDestination.route+"/{accountId}", arguments = listOf(navArgument("accountId") { type = NavType.IntType })) {
            AccountDetailScreen(
                navController = navController
            )
        }
        composable(route = TransactionEntryDestination.route) {
            TransactionEntryScreen(
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() }
            )
        }
    }
}
