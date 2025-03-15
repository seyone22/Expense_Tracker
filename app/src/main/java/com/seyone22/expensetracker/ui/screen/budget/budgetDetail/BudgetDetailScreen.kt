package com.seyone22.expensetracker.ui.screen.budget.budgetDetail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.seyone22.expensetracker.R
import com.seyone22.expensetracker.SharedViewModel
import com.seyone22.expensetracker.data.model.Category
import com.seyone22.expensetracker.data.model.CurrencyFormat
import com.seyone22.expensetracker.ui.AppViewModelProvider
import com.seyone22.expensetracker.ui.common.ExpenseTopBar
import com.seyone22.expensetracker.ui.common.FormattedCurrency
import com.seyone22.expensetracker.ui.common.dialogs.AddEditBudgetEntryDialogAction
import com.seyone22.expensetracker.ui.common.dialogs.GenericDialog
import com.seyone22.expensetracker.ui.navigation.NavigationDestination
import com.seyone22.expensetracker.ui.screen.budget.composables.BudgetItemCard
import com.seyone22.expensetracker.ui.screen.budget.composables.UnsetBudgetItemCard
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

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
    navController: NavController,
    viewModel: BudgetDetailViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val budgetDetailUiState by viewModel.budgetDetailUiState.collectAsState(BudgetDetailUiState())
    val currentDialog by viewModel.currentDialog

    val showOnlyActive = remember { mutableStateOf(false) }

    // Code block to get the current currency's detail.
    val sharedViewModel: SharedViewModel = viewModel(factory = AppViewModelProvider.Factory)
    val baseCurrency by sharedViewModel.baseCurrencyFlow.collectAsState(initial = CurrencyFormat())

    currentDialog?.let {
        GenericDialog(dialogAction = it, onDismiss = { viewModel.dismissDialog() })
    }

    LaunchedEffect(backStackEntry) {
        viewModel.fetchBudgetYearFor(backStackEntry)
        viewModel.fetchBudgetEntriesFor(backStackEntry)
        viewModel.fetchStatistics()
    }

    Scaffold(topBar = {
        ExpenseTopBar(
            selectedActivity = "Details for ${budgetDetailUiState.selectedBudgetYear?.budgetYearName}",
            dropdownOptions = listOf(
                (if (showOnlyActive.value) "Show all Entries" else "Show only Active") to {
                    showOnlyActive.value = !showOnlyActive.value
                },
                "Delete Budget" to { /* action for option 1 */ },
            ),
            navController = navController,
            type = "Left",
            hasNavBarAction = true,
            hasNavigation = true
        )
    }) { paddingValues ->
        if (baseCurrency != null) {
            BudgetDetailContent(
                modifier = Modifier.padding(paddingValues),
                budgetDetailUiState = budgetDetailUiState,
                viewModel = viewModel,
                backStackEntry = backStackEntry,
                currencyFormat = baseCurrency!!,
                showOnlyActive = showOnlyActive
            )
        }
    }
}

@Composable
private fun BudgetDetailContent(
    modifier: Modifier,
    currencyFormat: CurrencyFormat,
    budgetDetailUiState: BudgetDetailUiState,
    viewModel: BudgetDetailViewModel,
    backStackEntry: Int,
    showOnlyActive: MutableState<Boolean>
) {
    // Code block to get the current currency's detail.
    val sharedViewModel: SharedViewModel = viewModel(factory = AppViewModelProvider.Factory)
    val baseCurrency by sharedViewModel.baseCurrencyFlow.collectAsState(initial = CurrencyFormat())

    val parentCategories = budgetDetailUiState.categories.filter { it.parentId == -1 }
    val childCategoriesMap =
        budgetDetailUiState.categories.filter { it.parentId != -1 }.groupBy { it.parentId }

    LazyColumn(
        modifier = modifier, verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { IncomeExpenseSummary(baseCurrency, viewModel) }

        parentCategories.forEach { parent ->
            item {
                ParentCategoryItem(
                    parent,
                    childCategoriesMap,
                    currencyFormat,
                    viewModel,
                    backStackEntry,
                    showOnlyActive
                )
            }
        }
    }
}

@Composable
private fun IncomeExpenseSummary(
    currencyFormat: CurrencyFormat?,
    viewModel: BudgetDetailViewModel
) {
    val income by viewModel.incomeStatistics.collectAsState()
    val expenses by viewModel.expenseStatistics.collectAsState()

    val estimatedIncome = viewModel.getEstimatedIncome()
    val estimatedExpenses = viewModel.getEstimatedExpenses()

    Card(
        modifier = Modifier.padding(horizontal = 16.dp),
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                FormattedCurrency(
                    value = estimatedIncome - estimatedExpenses,
                    currency = currencyFormat ?: CurrencyFormat(),
                    style = TextStyle(
                        fontSize = 28.sp
                    ),
                    modifier = Modifier.padding(top = 32.dp, bottom = 8.dp)
                )
                Text(
                    text = "Left to budget", style = TextStyle(
                        fontSize = 16.sp
                    ), modifier = Modifier.padding(top = 0.dp, bottom = 0.dp)
                )
            }

            if (estimatedIncome != 0.0) {
                SummaryCard(
                    title = "Income",
                    estimated = estimatedIncome.absoluteValue,
                    actual = income,
                    currencyFormat = currencyFormat ?: CurrencyFormat(),
                )
            }
            if (estimatedExpenses != 0.0) {
                SummaryCard(
                    title = "Expenses",
                    estimated = estimatedExpenses.absoluteValue,
                    actual = expenses,
                    currencyFormat = currencyFormat ?: CurrencyFormat()
                )
            }
        }
    }
}

@Composable
private fun SummaryCard(
    title: String, estimated: Double, actual: Double, currencyFormat: CurrencyFormat
) {
    val invert = title == "Income"

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 2.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = title, fontWeight = FontWeight.Bold)
            FormattedCurrency(
                defaultColor = Color.Gray,
                value = estimated,
                currency = currencyFormat,
                decorativeText = " budget"
            )
        }
        LinearProgressIndicator(
            progress = {
                (actual / estimated).toFloat()
            },
            modifier = Modifier.fillMaxWidth(),
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            FormattedCurrency(
                value = actual,
                currency = currencyFormat,
                decorativeText = if (invert) " earned" else " spent"
            )
            FormattedCurrency(
                value = estimated - actual,
                currency = currencyFormat,
                decorativeText = if (invert) if (estimated - actual >= 0) " to earn" else " extra" else if (estimated - actual >= 0) " remains" else " excess",
                invert = invert
            )
        }
    }

}

@Composable
private fun ParentCategoryItem(
    parent: Category,
    childCategoriesMap: Map<Int, List<Category>>,
    currencyFormat: CurrencyFormat,
    viewModel: BudgetDetailViewModel,
    backStackEntry: Int,
    showOnlyActive: MutableState<Boolean>
) {
    val selectedBudgetYear =
        viewModel.budgetDetailUiState.collectAsState(BudgetDetailUiState()).value.selectedBudgetYear

    var categoryStatistics by remember { mutableStateOf(Pair(0.0, 0.0)) }

    LaunchedEffect(parent.categId, selectedBudgetYear) {
        categoryStatistics =
            viewModel.fetchCategoryStatisticsFor(parent.categId, selectedBudgetYear)
    }

    ListItem(headlineContent = {
        Text(
            text = parent.categName, style = MaterialTheme.typography.titleLarge
        )
    }, trailingContent = {
        Column(horizontalAlignment = Alignment.End) {
            Text("Estimated: ${categoryStatistics.first}")
            Text("Actual: ${categoryStatistics.second}")
        }
    })

    BudgetCategoryItem(
        parent,
        currencyFormat,
        viewModel,
        backStackEntry,
        showOnlyActive = showOnlyActive
    )

    childCategoriesMap[parent.categId]?.forEach { child ->
        BudgetCategoryItem(
            child,
            currencyFormat,
            viewModel,
            backStackEntry,
            showOnlyActive = showOnlyActive
        )
    }
}

@Composable
private fun BudgetCategoryItem(
    category: Category,
    currencyFormat: CurrencyFormat,
    viewModel: BudgetDetailViewModel,
    backStackEntry: Int,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    showOnlyActive: MutableState<Boolean>
) {
    val selectedBudgetYear =
        viewModel.budgetDetailUiState.collectAsState(BudgetDetailUiState()).value.selectedBudgetYear
    val budgetEntries =
        viewModel.budgetDetailUiState.collectAsState(BudgetDetailUiState()).value.budgetEntries
    val budgetItem = budgetEntries.find { it.categId == category.categId }

    var expenseForCategory by remember { mutableDoubleStateOf(0.0) }

    LaunchedEffect(category.categId, selectedBudgetYear) {
        expenseForCategory = viewModel.getExpensesForCategory(category.categId, selectedBudgetYear)
    }

    if (budgetItem != null || expenseForCategory != 0.0) {
        BudgetItemCard(modifier = Modifier.padding(bottom = 8.dp, start = 16.dp, end = 16.dp),
            category = category,
            budgetItem = budgetItem,
            expenseForCategory = expenseForCategory,
            currencyFormat = currencyFormat,
            targetPeriod = viewModel.fetchBudgetPeriod(),
            cardClickAction = {
                viewModel.showDialog(
                    AddEditBudgetEntryDialogAction(
                        onEdit = { budgetEntry ->
                            coroutineScope.launch {
                                viewModel.editBudgetEntry(budgetEntry)
                                viewModel.fetchBudgetEntriesFor(backStackEntry)
                            }
                        },
                        initialEntry = budgetItem,
                        categId = category.categId,
                        budgetYearId = backStackEntry
                    )
                )
            })
    } else {
        if (!showOnlyActive.value) {
            UnsetBudgetItemCard(modifier = Modifier.padding(
                bottom = 8.dp,
                start = 16.dp,
                end = 16.dp
            ),
                category = category,
                cardClickAction = {
                    viewModel.showDialog(
                        AddEditBudgetEntryDialogAction(
                            onAdd = { budgetEntry ->
                                coroutineScope.launch {
                                    viewModel.addBudgetEntry(budgetEntry)
                                    viewModel.fetchBudgetEntriesFor(backStackEntry)
                                }
                            }, categId = category.categId, budgetYearId = backStackEntry
                        )
                    )
                })
        }
    }
}
