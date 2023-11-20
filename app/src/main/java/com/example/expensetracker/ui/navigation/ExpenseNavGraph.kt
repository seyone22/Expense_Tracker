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
import com.example.expensetracker.model.Account
import com.example.expensetracker.ui.account.AccountDetailDestination
import com.example.expensetracker.ui.account.AccountDetailScreen
import com.example.expensetracker.ui.screen.accounts.AccountScreen
import com.example.expensetracker.ui.screen.accounts.AccountsDestination
import com.example.expensetracker.ui.screen.entities.EntitiesDestination
import com.example.expensetracker.ui.screen.entities.EntityScreen
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
        modifier = modifier,
        navController = navController,
        startDestination = AccountsDestination.route,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None }
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
        composable(route = EntitiesDestination.route) {
            EntityScreen(
                navigateToEntityEntry = { navController.navigate(EntitiesDestination.route) },
                navigateToScreen = { screen -> navController.navigate(screen) },
            )
        }
        composable(route = AccountDetailDestination.route+"/{accountId}", arguments = listOf(navArgument("accountId") { type = NavType.IntType })) {
            AccountDetailScreen(
                navigateToEntityEntry = { navController.navigate(AccountEntryDestination.route) },
                navigateToScreen = { screen -> navController.navigate(screen) },
                navController = navController
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
