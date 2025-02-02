package com.seyone22.expensetracker.ui.screen.budget

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.seyone22.expensetracker.R
import com.seyone22.expensetracker.data.model.BudgetEntry
import com.seyone22.expensetracker.data.model.Category
import com.seyone22.expensetracker.ui.AppViewModelProvider
import com.seyone22.expensetracker.ui.common.ExpenseNavBar
import com.seyone22.expensetracker.ui.common.ExpenseTopBar
import com.seyone22.expensetracker.ui.common.SortBar
import com.seyone22.expensetracker.ui.navigation.NavigationDestination
import com.seyone22.expensetracker.utils.getValueWithType

object BudgetDetailDestination : NavigationDestination {
    override val route = "Budget Detail"
    override val titleRes = R.string.app_name
    override val routeId = 88
}

val budgetData = listOf(
    BudgetEntry(1, 1, 1, "Weekly", 2410.0, "Test note"),
    BudgetEntry(2, 1, 2, "Monthly", -2240.0, "Test note"),
    BudgetEntry(3, 1, 3, "Fortnightly", 2340.0, "Test note"),
    BudgetEntry(4, 1, 4, "Weekly", -2430.0, "Test note"),
    BudgetEntry(5, 1, 5, "Every 2 Months", 2470.0, "Test note"),
)

@Composable
fun BudgetDetailScreen(
    modifier: Modifier = Modifier,
    backStackEntry: Int,
    navigateToScreen: (screen: String) -> Unit,
    viewModel: BudgetViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val budgetYearId = backStackEntry
    val budgetUiState: BudgetUiState by viewModel.budgetUiState.collectAsState(BudgetUiState())

    LaunchedEffect(Unit, budgetYearId) {
        viewModel.fetchBudgetEntriesFor(budgetYearId)
    }

    Log.d("TAG", "BudgetDetailScreen: ${budgetUiState.budgetEntries}")

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
            val parentCategories = budgetUiState.categories.filter { it.parentId == -1 }
            val childCategoriesMap = budgetUiState.categories.filter { it.parentId != -1 }
                .groupBy { it.parentId } // Group children by parentId

            val budgetMap = budgetData.associateBy { it.categId } // Map for quick lookup

            item {
                SortBar(modifier = Modifier.padding(16.dp, 0.dp), periodSortAction = {})
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp, 0.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Card(
                        modifier = Modifier.weight(1f) // Distribute space evenly
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                        ) {
                            Text("Income")
                            Text("Estimated: Rs.0.00")
                            Text("Actual: Rs.0.00")
                            Text("Difference: Rs.0.00")
                        }
                    }
                    Card(
                        modifier = Modifier.weight(1f) // Distribute space evenly
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                        ) {
                            Text("Expenses")
                            Text("Estimated: Rs.0.00")
                            Text("Actual: Rs.0.00")
                            Text("Difference: Rs.0.00")
                        }
                    }
                }
            }

            for (parent in parentCategories) {
                // Render parent category heading
                item {
                    ListItem(headlineContent = {
                        Text(
                            text = parent.categName, style = MaterialTheme.typography.titleLarge
                        )
                    }, trailingContent = {
                        Column(
                            horizontalAlignment = Alignment.End,
                        ) {
                            Text(
                                "Estimated: Rs.200,000"
                            )
                            Text(
                                "Actual: Rs.100,000"
                            )
                        }
                    })
                }

                // Render budget item for the parent
                val budgetItem = budgetMap[parent.categId]
                item {
                    if (budgetItem != null) {
                        BudgetItemCard(
                            category = parent, budgetItem = budgetItem, expenseForCategory = 200.0
                        )
                    } else {
                        UnsetBudgetItemCard(parent)
                    }
                }

                // Render child categories for this parent
                childCategoriesMap[parent.categId]?.forEach { child ->
                    val childBudgetItem = budgetMap[child.categId]
                    item {
                        if (childBudgetItem != null) {
                            BudgetItemCard(
                                category = child,
                                budgetItem = childBudgetItem,
                                expenseForCategory = 200.0
                            )
                        } else {
                            UnsetBudgetItemCard(child)
                        }
                    }
                }
            }
        }

    }
}

@Composable
fun UnsetBudgetItemCard(
    category: Category?,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp, 0.dp)
            .clickable { },
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
                    text = "${category?.categName}",
                    fontWeight = if (category?.parentId == -1) FontWeight.Bold else FontWeight.Normal
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

@Composable
fun BudgetItemCard(
    category: Category?,
    budgetItem: BudgetEntry?,
    expenseForCategory: Double,
) {
    val actualValue = getValueWithType(budgetItem?.amount)
    val ratio = expenseForCategory.coerceAtLeast(1.0) / (actualValue?.first ?: 1.0)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp, 0.dp)
            .clickable { },
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.padding(end = 32.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    progress = { ratio.toFloat() },
                    trackColor = MaterialTheme.colorScheme.inversePrimary,
                    strokeCap = StrokeCap.Round,
                    modifier = Modifier.size(54.dp)
                )
                Text(
                    "${
                        (ratio * 100).coerceIn(0.0..1000.0).toInt()
                    }%"
                )
            }
            Column(
                modifier = Modifier.width(225.dp)
            ) {
                Text(
                    text = "${category?.categName}",
                    fontWeight = if (category?.parentId == -1) FontWeight.Bold else FontWeight.Normal
                )
                Text(
                    text = "Rs.${expenseForCategory} out of Rs.${
                        String.format(
                            "%.2f", budgetItem?.amount ?: 0.0
                        )
                    }"
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