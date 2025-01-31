package com.seyone22.expensetracker.ui.screen.budget

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.seyone22.expensetracker.R
import com.seyone22.expensetracker.ui.AppViewModelProvider
import com.seyone22.expensetracker.ui.navigation.NavigationDestination

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
    var state by remember { mutableIntStateOf(0) }
    val titles = listOf("Categories", "Payees", "Currencies")


}