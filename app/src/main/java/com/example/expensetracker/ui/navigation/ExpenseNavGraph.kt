package com.example.expensetracker.ui.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.expensetracker.ui.screen.accounts.AccountScreen
import com.example.expensetracker.ui.screen.accounts.AccountsDestination
import com.example.expensetracker.ui.screen.entities.EntitiesDestination
import com.example.expensetracker.ui.screen.entities.EntityScreen
import com.example.expensetracker.ui.screen.onboarding.OnboardingDestination
import com.example.expensetracker.ui.screen.onboarding.OnboardingScreen
import com.example.expensetracker.ui.screen.operations.account.AccountDetailDestination
import com.example.expensetracker.ui.screen.operations.account.AccountDetailScreen
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
import com.example.expensetracker.ui.screen.report.BudgetScreen
import com.example.expensetracker.ui.screen.report.BudgetsDestination
import com.example.expensetracker.ui.screen.report.ReportScreen
import com.example.expensetracker.ui.screen.report.ReportsDestination
import com.example.expensetracker.ui.screen.settings.SettingsDestination
import com.example.expensetracker.ui.screen.settings.SettingsDetailDestination
import com.example.expensetracker.ui.screen.settings.SettingsDetailScreen
import com.example.expensetracker.ui.screen.settings.SettingsScreen
import com.example.expensetracker.ui.screen.transactions.TransactionsDestination
import com.example.expensetracker.ui.screen.transactions.TransactionsScreen
import com.example.expensetracker.ui.utils.ExpenseNavigationType

/**
 * Provides Navigation graph for the application.
 */

@Composable
fun ExpenseNavHost(
    navController: NavHostController,
    windowSizeClass: WindowWidthSizeClass,
    modifier: Modifier = Modifier,
    setTopBarAction : (Int) -> Unit,
    setIsItemSelected : (Boolean) -> Unit,
    setSelectedObject : (Any) -> Unit,
    innerPadding : PaddingValues
) {
    NavHost(
        modifier = modifier.padding(innerPadding),
        navController = navController,
        startDestination = AccountsDestination.route,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None },
    ) {
        // Routes to main Navbar destinations
        composable(route = AccountsDestination.route) {
            AccountScreen(
                navigateToScreen = { screen -> navController.navigate(screen) },
                windowSizeClass = windowSizeClass,
                setTopBarAction = setTopBarAction
            )
        }
        composable(route = EntitiesDestination.route) {
            EntityScreen(
                navigateToScreen = { screen -> navController.navigate(screen) },
                setTopBarAction = setTopBarAction,
                setIsItemSelected = setIsItemSelected,
                setSelectedObject = setSelectedObject
            )
        }
        composable(route = TransactionsDestination.route) {
            TransactionsScreen(
                navigateToScreen = { screen -> navController.navigate(screen) },
                setIsItemSelected = setIsItemSelected,
                setSelectedObject = setSelectedObject,
                setTopBarAction = setTopBarAction,
                )
        }
        composable(route = ReportsDestination.route) {
            ReportScreen(
                navigateToScreen = { screen -> navController.navigate(screen) },
            )
        }
        composable(route = BudgetsDestination.route) {
            BudgetScreen(
                navigateToScreen = { screen -> navController.navigate(screen) },
            )
        }
        // Routes to pages for CRUD operations
        composable(route = AccountEntryDestination.route) {
            AccountEntryScreen(
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() },
                navigateToScreen = { screen -> navController.navigate(screen) },
            )
        }
        composable(
            route = AccountDetailDestination.route + "/{accountId}",
            arguments = listOf(navArgument("accountId") { type = NavType.StringType })
        ) {
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
                navigateToScreen = { screen -> navController.navigate(screen) },
                navigateBack = { navController.popBackStack() },
            )
        }
        composable(route = SettingsDetailDestination.route + "/{setting}",
            arguments = listOf(navArgument("setting") { type = NavType.StringType })
        ) {
            SettingsDetailScreen(
                navigateToScreen = { screen -> navController.navigate(screen) },
                navigateBack = { navController.popBackStack() },
                backStackEntry = it.arguments?.getString("setting") ?: "-1"
            )
        }

        //Route to Onboarding Screen
        composable(route = OnboardingDestination.route) {
            OnboardingScreen(
                navigateToScreen = { screen -> navController.navigate(screen) }
            )
        }
    }
}
