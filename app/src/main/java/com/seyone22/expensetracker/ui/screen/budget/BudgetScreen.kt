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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.seyone22.expensetracker.R
import com.seyone22.expensetracker.data.model.BudgetEntry
import com.seyone22.expensetracker.data.model.BudgetYear
import com.seyone22.expensetracker.ui.AppViewModelProvider
import com.seyone22.expensetracker.ui.common.ExpenseNavBar
import com.seyone22.expensetracker.ui.common.ExpenseTopBar
import com.seyone22.expensetracker.ui.navigation.NavigationDestination

object BudgetsDestination : NavigationDestination {
    override val route = "Budgets"
    override val titleRes = R.string.app_name
    override val routeId = 2
}

val budgets = listOf(
    BudgetYear(1, "2025"),
    BudgetYear(2, "2025-01"),
    BudgetYear(3, "2024"),
    BudgetYear(4, "2025-02"),
    BudgetYear(5, "2025-03"),
)

val budgetData = listOf(
    BudgetEntry(1, 1, 1, "Weekly", 240.0, "Test note"),
    BudgetEntry(2, 1, 2, "Monthly", 240.0, "Test note"),
    BudgetEntry(3, 1, 3, "Fortnightly", 240.0, "Test note"),
    BudgetEntry(4, 1, 4, "Weekly", 240.0, "Test note"),
    BudgetEntry(5, 1, 5, "Every 2 Months", 240.0, "Test note"),
)

val categoryExpenses = listOf(
    Pair(1, 300.0),
    Pair(2, 200.0),
    Pair(3, 100.0),
    Pair(4, 400.0),
    Pair(5, 500.0),
)

@Composable
fun BudgetScreen(
    modifier: Modifier = Modifier,
    navigateToScreen: (screen: String) -> Unit,
    viewModel: BudgetViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val budgetUiState: BudgetUiState by viewModel.budgetUiState.collectAsState(BudgetUiState())

    Scaffold(bottomBar = {
        ExpenseNavBar(
            currentActivity = BudgetsDestination.route, navigateToScreen = navigateToScreen
        )
    }, topBar = {
        ExpenseTopBar(
            selectedActivity = BudgetsDestination.route,
            navController = rememberNavController(),
            type = "Center"
        )
    }) {
        LazyColumn(
            modifier = Modifier.padding(it), verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            items(count = budgetUiState.categoriesParent.size) { idx ->
                ListItem(
                    headlineContent = {
                        Text(
                            text = budgetUiState.categoriesParent[idx].categName,
                            style = MaterialTheme.typography.titleLarge
                        )
                    },
                )
            }
        }
    }
}

@Composable
fun BudgetItemCard(idx: Int) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp, 0.dp)
            .clickable { },
    ) {
        Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            Box(
                modifier = Modifier.padding(end = 32.dp),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                CircularProgressIndicator(
                    progress = { (categoryExpenses[idx].second / budgetData[idx].amount).toFloat() },
                    trackColor = MaterialTheme.colorScheme.inversePrimary,
                    strokeCap = StrokeCap.Round,
                    modifier = Modifier.size(54.dp)
                )
                Text(
                    "${
                        ((categoryExpenses[idx].second / budgetData[idx].amount) * 100).coerceIn(0.0..1000.0)
                            .toInt()
                    }%"
                )
            }
            Column(
                modifier = Modifier.width(225.dp)
            ) {
                Text(text = "Rs.${categoryExpenses[idx].second} out of Rs.${budgetData[idx].amount}")
                Text(text = "Rs.${budgetData[idx].amount} ${budgetData[idx].period}")
            }
            Box(
                modifier = Modifier.height(54.dp),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                Icon(
                    Icons.Filled.ChevronRight, "", modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}