package com.example.expensetracker.ui.screen.report

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.expensetracker.R
import com.example.expensetracker.ui.AppViewModelProvider
import com.example.expensetracker.ui.common.ExpenseFAB
import com.example.expensetracker.ui.common.ExpenseNavBar
import com.example.expensetracker.ui.common.ExpenseTopBar
import com.example.expensetracker.ui.navigation.NavigationDestination
import com.example.expensetracker.ui.screen.settings.SettingsDestination

object BudgetsDestination : NavigationDestination {
    override val route = "Budgets"
    override val titleRes = R.string.app_name
    override val routeId = 2
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetScreen(
    modifier: Modifier = Modifier,
    navigateToScreen: (screen: String) -> Unit,
    viewModel: BudgetViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    var state by remember { mutableIntStateOf(0) }
    val titles = listOf("Categories", "Payees", "Currencies")
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            ExpenseTopBar(
                selectedActivity = BudgetsDestination.routeId,
                navBarAction = {  },
                navigateToSettings = { navigateToScreen(SettingsDestination.route) }
            )
        },
        bottomBar = {
            ExpenseNavBar(selectedActivity = BudgetsDestination.routeId, navigateToScreen = navigateToScreen)
        },
        floatingActionButton = {
            ExpenseFAB(navigateToScreen = navigateToScreen)
        }
    ) { innerPadding ->
        Column(
            Modifier.padding(innerPadding)
        ) {

        }
    }
}