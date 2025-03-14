package com.seyone22.expensetracker.ui.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.seyone22.expensetracker.ui.screen.budget.BudgetScreen
import com.seyone22.expensetracker.ui.screen.budget.BudgetsDestination
import com.seyone22.expensetracker.ui.screen.budget.budgetDetail.BudgetDetailDestination
import com.seyone22.expensetracker.ui.screen.budget.budgetDetail.BudgetDetailScreen
import com.seyone22.expensetracker.ui.screen.entities.EntitiesDestination
import com.seyone22.expensetracker.ui.screen.entities.EntityScreen
import com.seyone22.expensetracker.ui.screen.home.HomeDestination
import com.seyone22.expensetracker.ui.screen.home.HomeScreen
import com.seyone22.expensetracker.ui.screen.onboarding.OnboardingDestination
import com.seyone22.expensetracker.ui.screen.onboarding.OnboardingScreen
import com.seyone22.expensetracker.ui.screen.operations.account.AccountDetailDestination
import com.seyone22.expensetracker.ui.screen.operations.account.AccountDetailScreen
import com.seyone22.expensetracker.ui.screen.operations.account.AccountEntryDestination
import com.seyone22.expensetracker.ui.screen.operations.account.AccountEntryScreen
import com.seyone22.expensetracker.ui.screen.operations.entity.category.CategoryEntryDestination
import com.seyone22.expensetracker.ui.screen.operations.entity.category.CategoryEntryScreen
import com.seyone22.expensetracker.ui.screen.operations.entity.currency.CurrencyEntryDestination
import com.seyone22.expensetracker.ui.screen.operations.entity.currency.CurrencyEntryScreen
import com.seyone22.expensetracker.ui.screen.operations.entity.payee.PayeeEntryDestination
import com.seyone22.expensetracker.ui.screen.operations.entity.payee.PayeeEntryScreen
import com.seyone22.expensetracker.ui.screen.operations.report.ReportEntryDestination
import com.seyone22.expensetracker.ui.screen.operations.report.ReportEntryScreen
import com.seyone22.expensetracker.ui.screen.operations.transaction.TransactionEntryDestination
import com.seyone22.expensetracker.ui.screen.operations.transaction.TransactionEntryScreen
import com.seyone22.expensetracker.ui.screen.report.ReportScreen
import com.seyone22.expensetracker.ui.screen.report.ReportsDestination
import com.seyone22.expensetracker.ui.screen.settings.SettingsDestination
import com.seyone22.expensetracker.ui.screen.settings.SettingsDetailDestination
import com.seyone22.expensetracker.ui.screen.settings.SettingsDetailScreen
import com.seyone22.expensetracker.ui.screen.settings.SettingsScreen
import com.seyone22.expensetracker.ui.screen.transactions.TransactionsDestination
import com.seyone22.expensetracker.ui.screen.transactions.TransactionsScreen

/**
 * Provides Navigation graph for the application.
 */

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun ExpenseNavHost(
    navController: NavHostController,
    windowSizeClass: WindowWidthSizeClass,
    modifier: Modifier = Modifier,
    onToggleDarkTheme: (Int) -> Unit,
    snackbarHostState: SnackbarHostState,
) {
    NavHost(
        navController = navController,
        startDestination = HomeDestination.route,
        enterTransition = { fadeIn(animationSpec = tween(500)) },
        exitTransition = { fadeOut(animationSpec = tween(500)) },
    ) {
        // Routes to main Navbar destinations
        composable(route = HomeDestination.route) {
            HomeScreen(
                navigateToScreen = { screen -> navController.navigate(screen) },
                windowSizeClass = windowSizeClass,
            )
        }
        composable(route = EntitiesDestination.route, enterTransition = {
            slideInHorizontally(animationSpec = tween(500),
                initialOffsetX = { fullWidth -> fullWidth } // Slide in from the right
            ) + fadeIn(animationSpec = tween(500))
        }, exitTransition = {
            slideOutHorizontally(animationSpec = tween(500),
                targetOffsetX = { fullWidth -> fullWidth } // Slide out to the right
            ) + fadeOut(animationSpec = tween(500))
        }) {
            EntityScreen(
                navigateToScreen = { screen -> navController.navigate(screen) },
                navController = navController

            )
        }
        composable(route = TransactionsDestination.route, enterTransition = {
            slideInHorizontally(animationSpec = tween(500),
                initialOffsetX = { fullWidth -> fullWidth } // Slide in from the right
            ) + fadeIn(animationSpec = tween(500))
        }, exitTransition = {
            slideOutHorizontally(animationSpec = tween(500),
                targetOffsetX = { fullWidth -> fullWidth } // Slide out to the right
            ) + fadeOut(animationSpec = tween(500))
        }) {
            TransactionsScreen(
                navigateToScreen = { screen -> navController.navigate(screen) },
                navController = navController
            )
        }
        composable(route = ReportsDestination.route) {
            ReportScreen(
                navigateToScreen = { screen -> navController.navigate(screen) },
            )
        }
        // Routes to budget destinations
        composable(route = BudgetsDestination.route) {
            BudgetScreen(
                navigateToScreen = { screen -> navController.navigate(screen) }
            )
        }
        composable(
            route = BudgetDetailDestination.route + "/{budgetYearId}",
            arguments = listOf(navArgument("budgetYearId") { type = NavType.IntType }),
            enterTransition = {
                slideInHorizontally(animationSpec = tween(500),
                    initialOffsetX = { fullWidth -> fullWidth } // Slide in from the right
                ) + fadeIn(animationSpec = tween(500))
            },
            exitTransition = {
                slideOutHorizontally(animationSpec = tween(500),
                    targetOffsetX = { fullWidth -> fullWidth } // Slide out to the right
                ) + fadeOut(animationSpec = tween(500))
            }

        ) {
            BudgetDetailScreen(
                backStackEntry = it.arguments?.getInt("budgetYearId") ?: -1,
                navigateToScreen = { screen -> navController.navigate(screen) },
                navController = navController
            )
        }

        // Routes to pages for CRUD operations
        composable(route = AccountEntryDestination.route, enterTransition = {
            slideInHorizontally(animationSpec = tween(500),
                initialOffsetX = { fullWidth -> fullWidth } // Slide in from the right
            ) + fadeIn(animationSpec = tween(500))
        }, exitTransition = {
            slideOutHorizontally(animationSpec = tween(500),
                targetOffsetX = { fullWidth -> fullWidth } // Slide out to the right
            ) + fadeOut(animationSpec = tween(500))
        }) {
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
        composable(route = TransactionEntryDestination.route + "/{transactionType}") { backStackEntry ->
            val transactionType =
                backStackEntry.arguments?.getString("transactionType") ?: "Unknown"

            TransactionEntryScreen(
                transactionType = transactionType,
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() })
        }
        composable(route = ReportEntryDestination.route) {
            ReportEntryScreen(
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() },
                modifier = modifier
            )
        }
        //Routes to pages for Create operations for Entities
        composable(route = CategoryEntryDestination.route) {
            CategoryEntryScreen(navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() })
        }
        composable(route = PayeeEntryDestination.route) {
            PayeeEntryScreen(navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() })
        }
        composable(route = CurrencyEntryDestination.route) {
            CurrencyEntryScreen(navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() })
        }
        // Routes to settings screen
        composable(route = SettingsDestination.route) {
            SettingsScreen(
                navigateToScreen = { screen -> navController.navigate(screen) },
                navigateBack = { navController.popBackStack() },
            )
        }
        composable(route = SettingsDetailDestination.route + "/{setting}",
            arguments = listOf(navArgument("setting") { type = NavType.StringType }),
            enterTransition = {
                slideInHorizontally(animationSpec = tween(500),
                    initialOffsetX = { fullWidth -> fullWidth } // Slide in from the right
                ) + fadeIn(animationSpec = tween(500))
            },
            exitTransition = {
                slideOutHorizontally(animationSpec = tween(500),
                    targetOffsetX = { fullWidth -> fullWidth } // Slide out to the right
                ) + fadeOut(animationSpec = tween(500))
            }) {
            SettingsDetailScreen(navigateToScreen = { screen -> navController.navigate(screen) },
                navigateBack = { navController.popBackStack() },
                backStackEntry = it.arguments?.getString("setting") ?: "-1",
                onToggleDarkTheme = { x -> onToggleDarkTheme(x) })
        }

        //Route to Onboarding Screen
        composable(route = OnboardingDestination.route) {
            OnboardingScreen(navigateToScreen = { screen -> navController.navigate(screen) })
        }
    }
}
