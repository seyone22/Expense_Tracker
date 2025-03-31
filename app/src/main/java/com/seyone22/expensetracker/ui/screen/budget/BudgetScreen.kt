package com.seyone22.expensetracker.ui.screen.budget

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Balance
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.NavigableListDetailPaneScaffold
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.seyone22.expensetracker.R
import com.seyone22.expensetracker.ui.AppViewModelProvider
import com.seyone22.expensetracker.ui.common.dialogs.GenericDialog
import com.seyone22.expensetracker.ui.navigation.NavigationDestination
import com.seyone22.expensetracker.ui.screen.budget.panes.BudgetDetailPane
import com.seyone22.expensetracker.ui.screen.budget.panes.BudgetListPane
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

object BudgetsDestination : NavigationDestination {
    override val route = "Budgets"
    override val titleRes = R.string.app_name
    override val routeId = 2
    override val icon: ImageVector = Icons.Outlined.Balance
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun BudgetScreen(
    modifier: Modifier = Modifier,
    navigateToScreen: (screen: String) -> Unit,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    navController: NavController = rememberNavController(),
    viewModel: BudgetViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val scaffoldNavigator = rememberListDetailPaneScaffoldNavigator<Int>()
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass

    val budgetUiState: BudgetUiState by viewModel.budgetUiState.collectAsState(BudgetUiState())
    var selectedBudgetId by remember { mutableIntStateOf(-1) }

    val currentDialog by viewModel.currentDialog

    currentDialog?.let {
        GenericDialog(dialogAction = it, onDismiss = { viewModel.dismissDialog() })
    }

    NavigableListDetailPaneScaffold(navigator = scaffoldNavigator, listPane = {
        AnimatedPane {
            BudgetListPane(
                budgetUiState = budgetUiState,
                navigateToScreen = { key ->

                    selectedBudgetId = key
                    coroutineScope.launch {
                        scaffoldNavigator.navigateTo(
                            pane = ListDetailPaneScaffoldRole.Detail, contentKey = key
                        )
                    }
                },
                selectedBudgetId = selectedBudgetId,
                windowSizeClass = windowSizeClass,
                viewModel = viewModel
            )
        }
    }, detailPane = {
        AnimatedPane {
            val selectedBudget = scaffoldNavigator.currentDestination
            if (selectedBudget != null && selectedBudgetId != -1) {
                BudgetDetailPane(
                    backStackEntry = (selectedBudget.contentKey ?: 0),
                    scaffoldNavigator = scaffoldNavigator,
                    navController = navController
                )
            } else {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text("Select a budget to view details")
                }
            }
        }
    })
}