package com.seyone22.expensetracker.ui.navigation

import androidx.compose.ui.graphics.vector.ImageVector
import com.seyone22.expensetracker.ui.screen.budget.BudgetsDestination
import com.seyone22.expensetracker.ui.screen.home.HomeDestination
import com.seyone22.expensetracker.ui.screen.report.ReportsDestination
import com.seyone22.expensetracker.ui.screen.settings.SettingsDestination

/**
 * Interface to describe the navigation destinations for the app
 */
interface NavigationDestination {
    /**
     * Unique name to define the path for a composable
     */
    val route: String

    /**
     * String resource id to that contains title to be displayed for the screen.
     */
    val titleRes: Int

    val routeId: Int

    val icon: ImageVector?
}

enum class MainNavigationDestinations(
    val navigationDestination: NavigationDestination,
) {
    HOME(HomeDestination),
    BUDGETS(BudgetsDestination),
    REPORTS(ReportsDestination),
    MORE(SettingsDestination)
}