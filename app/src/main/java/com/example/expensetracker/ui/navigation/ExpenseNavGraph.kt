package com.example.expensetracker.ui.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.expensetracker.ui.screen.operations.account.AccountDetailDestination
import com.example.expensetracker.ui.screen.operations.account.AccountDetailScreen
import com.example.expensetracker.ui.screen.accounts.AccountScreen
import com.example.expensetracker.ui.screen.accounts.AccountsDestination
import com.example.expensetracker.ui.screen.entities.EntitiesDestination
import com.example.expensetracker.ui.screen.entities.EntityScreen
import com.example.expensetracker.ui.screen.operations.account.AccountEntryDestination
import com.example.expensetracker.ui.screen.operations.account.AccountEntryScreen
import com.example.expensetracker.ui.screen.operations.entity.category.CategoryEntryDestination
import com.example.expensetracker.ui.screen.operations.entity.category.CategoryEntryScreen
import com.example.expensetracker.ui.screen.operations.entity.currency.CurrencyEntryDestination
import com.example.expensetracker.ui.screen.operations.entity.currency.CurrencyEntryScreen
import com.example.expensetracker.ui.screen.operations.entity.payee.PayeeEntryDestination
import com.example.expensetracker.ui.screen.operations.entity.payee.PayeeEntryScreen
import com.example.expensetracker.ui.screen.operations.transaction.TransactionEntryDestination
import com.example.expensetracker.ui.screen.operations.transaction.TransactionEntryScreen
import com.example.expensetracker.ui.screen.report.ReportsDestination
import com.example.expensetracker.ui.screen.report.ReportScreen
import com.example.expensetracker.ui.screen.settings.SettingsDestination
import com.example.expensetracker.ui.screen.settings.SettingsScreen

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
        composable(route = ReportsDestination.route) {
            ReportScreen(
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
        composable(
            route = AccountDetailDestination.route+"/{accountId}",
            arguments = listOf(navArgument("accountId") { type = NavType.StringType })) {
            AccountDetailScreen(
                navController = navController,
                backStackEntry = it.arguments?.getString("accountId") ?: "-1"
            )
        }
        composable(route = TransactionEntryDestination.route) {
            TransactionEntryScreen(
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() }
            )
        }
        //Routes to pages for Create operations for Entities
        composable(route = CategoryEntryDestination.route) {
            CategoryEntryScreen(
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() }
            )
        }
        composable(route = PayeeEntryDestination.route) {
            PayeeEntryScreen(
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() }
            )
        }
        composable(route = CurrencyEntryDestination.route) {
            CurrencyEntryScreen(
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() }
            )
        }
        // Routes to settings screen
        composable(route = SettingsDestination.route) {
            SettingsScreen(
                navigateToScreen = { screen -> navController.navigate(screen) }
            )
        }
    }
}
