package com.seyone22.expensetracker.ui.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.seyone22.expensetracker.ui.screen.budget.BudgetScreen
import com.seyone22.expensetracker.ui.screen.budget.BudgetsDestination
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
    var currentDestination by rememberSaveable { mutableStateOf(MainNavigationDestinations.HOME) }

    // Track the current back stack entry to detect when navigation has happened
    val currentBackStackEntry by navController.currentBackStackEntryAsState()

    // Update currentDestination based on the current route in the back stack
    LaunchedEffect(currentBackStackEntry?.destination?.route) {
        val route = currentBackStackEntry?.destination?.route
        currentDestination = when (route) {
            HomeDestination.route -> MainNavigationDestinations.HOME
            ReportsDestination.route -> MainNavigationDestinations.REPORTS
            BudgetsDestination.route -> MainNavigationDestinations.BUDGETS
            SettingsDestination.route -> MainNavigationDestinations.MORE
            EntitiesDestination.route -> MainNavigationDestinations.MORE
            else -> MainNavigationDestinations.HOME
        }
    }

    NavHost(
        navController = navController,
        startDestination = HomeDestination.route,
        enterTransition = { fadeIn(animationSpec = tween(500)) },
        exitTransition = { fadeOut(animationSpec = tween(500)) },
    ) {
        // Routes to main Navbar destinations
        composable(route = HomeDestination.route) {
            NavigationSuiteScaffoldWrapper(
                currentDestination = currentDestination,
                navigateToScreen = { screen ->
                    navController.navigate(screen)
                }
            ) { modifier ->
                HomeScreen(
                    navigateToScreen = { screen ->
                        navController.navigate(screen)
                    },
                    windowSizeClass = windowSizeClass,
                )
            }
        }
        composable(route = ReportsDestination.route) {
            NavigationSuiteScaffoldWrapper(
                currentDestination = currentDestination,
                navigateToScreen = { screen ->
                    navController.navigate(screen)
                }
            ) { modifier ->
                ReportScreen(
                    navigateToScreen = { screen -> navController.navigate(screen) }
                )
            }
        }
        // Routes to budget destinations
        composable(route = BudgetsDestination.route) {
            NavigationSuiteScaffoldWrapper(
                currentDestination = currentDestination,
                navigateToScreen = { screen ->
                    navController.navigate(screen)
                }
            ) { modifier ->
                BudgetScreen(
                    navigateToScreen = { screen -> navController.navigate(screen) }
                )
            }
        }
        composable(route = SettingsDestination.route) {
            NavigationSuiteScaffoldWrapper(
                currentDestination = currentDestination,
                navigateToScreen = { screen ->
                    navController.navigate(screen)
                }
            ) { modifier ->
                SettingsScreen(
                    navigateToScreen = { screen -> navController.navigate(screen) },
                    navigateBack = { navController.popBackStack() },
                )
            }
        }

        composable(route = EntitiesDestination.route) {
            NavigationSuiteScaffoldWrapper(
                currentDestination = currentDestination,
                navigateToScreen = { screen ->
                    navController.navigate(screen)
                }
            ) { modifier ->
                EntityScreen(
                    navigateToScreen = { screen -> navController.navigate(screen) },
                    navController = navController,
                    modifier = modifier
                )
            }
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
                accountId = ""
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

@Composable
fun NavigationSuiteScaffoldWrapper(
    currentDestination: MainNavigationDestinations,
    navigateToScreen: (String) -> Unit,
    content: @Composable (Modifier) -> Unit
) {
    NavigationSuiteScaffold(navigationSuiteItems = {
        MainNavigationDestinations.entries.forEach { destination ->
            item(icon = {
                Icon(destination.navigationDestination.icon!!, "")
            },
                selected = destination == currentDestination,
                onClick = { navigateToScreen(destination.navigationDestination.route) },
                label = { Text(destination.navigationDestination.route) })
        }
    }) {
        content(Modifier)
    }
}
