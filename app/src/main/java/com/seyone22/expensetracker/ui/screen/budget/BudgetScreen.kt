package com.seyone22.expensetracker.ui.screen.budget

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.seyone22.expensetracker.R
import com.seyone22.expensetracker.data.model.BudgetYear
import com.seyone22.expensetracker.ui.AppViewModelProvider
import com.seyone22.expensetracker.ui.common.ExpenseNavBar
import com.seyone22.expensetracker.ui.common.ExpenseTopBar
import com.seyone22.expensetracker.ui.common.dialogs.AddEditBudgetYearDialogAction
import com.seyone22.expensetracker.ui.common.dialogs.GenericDialog
import com.seyone22.expensetracker.ui.navigation.NavigationDestination
import com.seyone22.expensetracker.ui.screen.budget.budgetDetail.BudgetDetailDestination
import kotlinx.coroutines.launch

object BudgetsDestination : NavigationDestination {
    override val route = "Budgets"
    override val titleRes = R.string.app_name
    override val routeId = 2
}

@Composable
fun BudgetScreen(
    modifier: Modifier = Modifier,
    navigateToScreen: (screen: String) -> Unit,
    viewModel: BudgetViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val budgetUiState: BudgetUiState by viewModel.budgetUiState.collectAsState(BudgetUiState())
    val coroutineScope = rememberCoroutineScope()

    val sortedBudgets = budgetUiState.budgetYears.sortedWith { budget1, budget2 ->
        val parts1 = budget1.budgetYearName.split("-")
        val parts2 = budget2.budgetYearName.split("-")

        // Sort by the year first, then by month if available
        val year1 =
            parts1.getOrNull(0)?.let { if (it.isNotEmpty()) it.toIntOrNull() else null } ?: 0
        val year2 =
            parts2.getOrNull(0)?.let { if (it.isNotEmpty()) it.toIntOrNull() else null } ?: 0

        val yearComparison = year1.compareTo(year2)
        if (yearComparison != 0) {
            return@sortedWith yearComparison
        }

        if (parts1.size > 1 && parts2.size > 1) {
            // Compare months if both are month-based
            parts1[1].toInt().compareTo(parts2[1].toInt())
        } else {
            // Keep year-only budgets above the month-based budgets
            0
        }
    }

    val currentDialog by viewModel.currentDialog

    currentDialog?.let {
        GenericDialog(dialogAction = it, onDismiss = { viewModel.dismissDialog() })
    }

    Scaffold(bottomBar = {
        ExpenseNavBar(
            currentActivity = BudgetsDestination.route, navigateToScreen = navigateToScreen
        )
    }, topBar = {
        ExpenseTopBar(selectedActivity = BudgetsDestination.route,
            navController = rememberNavController(),
            type = "Center",
            hasNavBarAction = true,
            navBarAction = {
                viewModel.showDialog(
                    AddEditBudgetYearDialogAction(
                        onAdd = { year, month, baseBudget ->
                            // month is nullable
                            coroutineScope.launch {
                                viewModel.addBudgetYear(year, month, baseBudget)
                            }

                        }, availableBudgets = budgetUiState.budgetYears
                    )
                )
            })
    }) {
        LazyColumn(
            modifier = Modifier.padding(it),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            val yearBudgets = sortedBudgets.filter { !it.budgetYearName.contains("-") }
                .sortedBy { it.budgetYearName } // Ensure years are sorted
            val monthBudgetsMap = sortedBudgets.filter { it.budgetYearName.contains("-") }
                .groupBy { it.budgetYearName.split("-")[0] } // Group months by year

            // Iterate over the years, adding their corresponding months immediately after
            yearBudgets.forEach { yearBudget ->
                item {
                    BudgetCard(
                        budgetYear = yearBudget,
                        isYearBudget = true,
                        navigateToScreen = navigateToScreen
                    )
                }

                monthBudgetsMap[yearBudget.budgetYearName]?.sortedBy { it.budgetYearName }
                    ?.forEach { monthBudget ->
                    item {
                        BudgetCard(
                            budgetYear = monthBudget,
                            isYearBudget = false,
                            navigateToScreen = navigateToScreen
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BudgetCard(
    budgetYear: BudgetYear, isYearBudget: Boolean, navigateToScreen: (screen: String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp, 0.dp)
            .clickable { navigateToScreen("${BudgetDetailDestination.route}/${budgetYear.budgetYearId}") },
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.height(24.dp),
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    fontWeight = if (isYearBudget) {
                        FontWeight.Bold
                    } else {
                        FontWeight.Normal
                    }, text = budgetYear.budgetYearName
                )
            }
            Box(
                modifier = Modifier, contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Filled.ChevronRight, "", modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}
