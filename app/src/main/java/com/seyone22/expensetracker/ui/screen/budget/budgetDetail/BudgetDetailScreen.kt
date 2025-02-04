package com.seyone22.expensetracker.ui.screen.budget.budgetDetail

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
import androidx.compose.material.icons.filled.NorthEast
import androidx.compose.material.icons.filled.SouthWest
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import com.seyone22.expensetracker.ui.common.dialogs.AddEditBudgetEntryDialogAction
import com.seyone22.expensetracker.ui.common.dialogs.GenericDialog
import com.seyone22.expensetracker.ui.navigation.NavigationDestination
import com.seyone22.expensetracker.ui.screen.budget.BudgetsDestination
import com.seyone22.expensetracker.utils.TransactionType
import com.seyone22.expensetracker.utils.getValueWithType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

object BudgetDetailDestination : NavigationDestination {
    override val route = "Budget Detail"
    override val titleRes = R.string.app_name
    override val routeId = 88
}

@Composable
fun BudgetDetailScreen(
    modifier: Modifier = Modifier,
    backStackEntry: Int,
    navigateToScreen: (screen: String) -> Unit,
    viewModel: BudgetDetailViewModel = viewModel(factory = AppViewModelProvider.Factory),
    coroutineScope: CoroutineScope = rememberCoroutineScope()
) {
    Log.d("TAG", "BudgetDetailScreen: $backStackEntry")

    val budgetDetailUiState: BudgetDetailUiState by viewModel.budgetDetailUiState.collectAsState(
        BudgetDetailUiState()
    )

    val currentDialog by viewModel.currentDialog

    currentDialog?.let {
        GenericDialog(dialogAction = it, onDismiss = { viewModel.dismissDialog() })
    }

    LaunchedEffect(Unit, backStackEntry) {
        viewModel.fetchBudgetEntriesFor(backStackEntry)
        viewModel.fetchBudgetYearFor(backStackEntry)
        viewModel.fetchStatistics(backStackEntry)
    }

    Log.d("TAG", "BudgetDetailScreen: ${budgetDetailUiState.selectedBudgetYear}")

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
            val parentCategories = budgetDetailUiState.categories.filter { it.parentId == -1 }
            val childCategoriesMap = budgetDetailUiState.categories.filter { it.parentId != -1 }
                .groupBy { it.parentId } // Group children by parentId

            val budgetMap =
                budgetDetailUiState.budgetEntries.associateBy { it.categId } // Map for quick lookup

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
                var expenseForCategory = 0.0
                coroutineScope.launch {
                    expenseForCategory = viewModel.getExpensesForCategory(
                        parent.categId, budgetDetailUiState.selectedBudgetYear
                    )
                }

                item {
                    if (budgetItem != null) {
                        BudgetItemCard(category = parent,
                            budgetItem = budgetItem,
                            expenseForCategory = expenseForCategory,
                            cardClickAction = {
                                viewModel.showDialog(
                                    AddEditBudgetEntryDialogAction(
                                        onEdit = { b ->
                                            coroutineScope.launch {
                                                viewModel.editBudgetEntry(b)
                                                viewModel.fetchBudgetEntriesFor(backStackEntry)
                                            }

                                        },
                                        initialEntry = budgetItem,
                                        categId = parent.categId,
                                        budgetYearId = backStackEntry
                                    )
                                )
                            })
                    } else {
                        UnsetBudgetItemCard(parent, cardClickAction = {
                            viewModel.showDialog(
                                AddEditBudgetEntryDialogAction(
                                    onAdd = { budgetEntry ->
                                        coroutineScope.launch {
                                            viewModel.addBudgetEntry(budgetEntry)
                                            viewModel.fetchBudgetEntriesFor(backStackEntry)
                                        }

                                    }, categId = parent.categId, budgetYearId = backStackEntry
                                )
                            )
                        })
                    }
                }

                // Render child categories for this parent
                childCategoriesMap[parent.categId]?.forEach { child ->
                    val childBudgetItem = budgetMap[child.categId]
                    var expenseForCategoryChild = 0.0
                    coroutineScope.launch {
                        expenseForCategoryChild = viewModel.getExpensesForCategory(
                            child.categId, budgetDetailUiState.selectedBudgetYear
                        )
                    }
                    item {
                        if (childBudgetItem != null) {
                            BudgetItemCard(category = child,
                                budgetItem = childBudgetItem,
                                expenseForCategory = expenseForCategoryChild,
                                cardClickAction = {
                                    viewModel.showDialog(
                                        AddEditBudgetEntryDialogAction(
                                            onEdit = { b ->
                                                coroutineScope.launch {
                                                    viewModel.editBudgetEntry(b)
                                                    viewModel.fetchBudgetEntriesFor(backStackEntry)
                                                }
                                            },
                                            initialEntry = childBudgetItem,
                                            categId = child.categId,
                                            budgetYearId = backStackEntry
                                        )
                                    )
                                })
                        } else {
                            UnsetBudgetItemCard(child, cardClickAction = {
                                viewModel.showDialog(
                                    AddEditBudgetEntryDialogAction(
                                        onAdd = { budgetEntry ->
                                            coroutineScope.launch {
                                                viewModel.addBudgetEntry(budgetEntry)
                                                viewModel.fetchBudgetEntriesFor(backStackEntry)
                                            }
                                        }, categId = child.categId, budgetYearId = backStackEntry
                                    )
                                )
                            })
                        }
                    }
                }
            }
        }

    }
}

@Composable
fun UnsetBudgetItemCard(
    category: Category?, cardClickAction: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp, 0.dp)
            .clickable { cardClickAction() },
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
    cardClickAction: () -> Unit
) {
    val actualValue = getValueWithType(budgetItem?.amount)
    val ratio = expenseForCategory.coerceAtLeast(1.0) / (actualValue?.first ?: 1.0)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp, 0.dp)
            .clickable { cardClickAction() },
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.padding(end = 32.dp), contentAlignment = Alignment.Center
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
                Row {
                    if (actualValue?.second == TransactionType.INCOME) {
                        Icon(
                            Icons.Filled.SouthWest,
                            "",
                            tint = Color.Green
                        )
                    } else {
                        Icon(
                            Icons.Filled.NorthEast,
                            "",
                            tint = Color.Red
                        )
                    }
                    Text(
                        text = "${category?.categName}",
                        fontWeight = if (category?.parentId == -1) FontWeight.Bold else FontWeight.Normal
                    )
                }
                Text(
                    text = "Rs.${expenseForCategory} out of Rs.${
                        String.format(
                            "%.2f", actualValue?.first ?: 0.0
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