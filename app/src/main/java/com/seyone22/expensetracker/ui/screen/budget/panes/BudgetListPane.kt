package com.seyone22.expensetracker.ui.screen.budget.panes

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import androidx.window.core.layout.WindowSizeClass
import androidx.window.core.layout.WindowWidthSizeClass
import com.seyone22.expensetracker.ui.common.ExpenseTopBar
import com.seyone22.expensetracker.ui.common.dialogs.AddEditBudgetYearDialogAction
import com.seyone22.expensetracker.ui.screen.budget.BudgetUiState
import com.seyone22.expensetracker.ui.screen.budget.BudgetViewModel
import com.seyone22.expensetracker.ui.screen.budget.BudgetsDestination
import com.seyone22.expensetracker.ui.screen.budget.composables.BudgetCard
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3AdaptiveApi::class, ExperimentalFoundationApi::class)
@Composable
fun BudgetListPane(
    navigateToScreen: (screen: Int) -> Unit,
    budgetUiState: BudgetUiState,
    selectedBudgetId: Int,
    windowSizeClass: WindowSizeClass,
    viewModel: BudgetViewModel
) {
    val coroutineScope = rememberCoroutineScope()
    val sortedBudgets = budgetUiState.budgetYears.sortedWith { budget1, budget2 ->
        val parts1 = budget1.budgetYearName.split("-")
        val parts2 = budget2.budgetYearName.split("-")
        val year1 = parts1.getOrNull(0)?.toIntOrNull() ?: 0
        val year2 = parts2.getOrNull(0)?.toIntOrNull() ?: 0
        val yearComparison = year1.compareTo(year2)
        if (yearComparison != 0) return@sortedWith yearComparison
        if (parts1.size > 1 && parts2.size > 1) parts1[1].toInt().compareTo(parts2[1].toInt())
        else 0
    }

    val yearBudgets =
        sortedBudgets.filter { !it.budgetYearName.contains("-") }.sortedBy { it.budgetYearName }
    val monthBudgetsMap = sortedBudgets.filter { it.budgetYearName.contains("-") }
        .groupBy { it.budgetYearName.split("-")[0] }

    LazyColumn(modifier = Modifier.windowInsetsPadding(insets = WindowInsets.statusBars)) {
        item {
            ExpenseTopBar(
                selectedActivity = BudgetsDestination.route,
                type = "Center",
                hasNavBarAction = true,
                navBarAction = {
                    viewModel.showDialog(
                        AddEditBudgetYearDialogAction(
                            onAdd = { year, month, baseBudget ->
                                coroutineScope.launch {
                                    viewModel.addBudgetYear(
                                        year, month, baseBudget
                                    )
                                }
                            }, availableBudgets = budgetUiState.budgetYears
                        )
                    )
                },
                navController = rememberNavController()
            )
        }

        yearBudgets.forEach { yearBudget ->
            stickyHeader {
                BudgetCard(budgetYear = yearBudget,
                    isYearBudget = true,
                    isSelected = if (windowSizeClass.windowWidthSizeClass != WindowWidthSizeClass.COMPACT) {
                        yearBudget.budgetYearId == selectedBudgetId
                    } else false,
                    navigateToScreen = { navigateToScreen(yearBudget.budgetYearId) })
            }
            monthBudgetsMap[yearBudget.budgetYearName]?.sortedBy { it.budgetYearName }
                ?.forEach { monthBudget ->
                    item {
                        BudgetCard(budgetYear = monthBudget,
                            isYearBudget = false,
                            isSelected = if (windowSizeClass.windowWidthSizeClass != WindowWidthSizeClass.COMPACT) {
                                monthBudget.budgetYearId == selectedBudgetId
                            } else false,
                            navigateToScreen = { navigateToScreen(monthBudget.budgetYearId) })
                    }
                }
        }
    }
}