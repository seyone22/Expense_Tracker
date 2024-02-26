package com.example.expensetracker

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.expensetracker.ui.AppViewModelProvider
import com.example.expensetracker.ui.common.ExpenseFAB
import com.example.expensetracker.ui.common.ExpenseNavBar
import com.example.expensetracker.ui.common.ExpenseTopBar
import com.example.expensetracker.ui.common.dialogs.CategoryEntryDialog
import com.example.expensetracker.ui.common.dialogs.CurrencyEntryDialog
import com.example.expensetracker.ui.common.dialogs.DeleteConfirmationDialog
import com.example.expensetracker.ui.common.dialogs.PayeeEntryDialog
import com.example.expensetracker.ui.navigation.ExpenseNavHost
import com.example.expensetracker.ui.screen.entities.EntityViewModel
import com.example.expensetracker.ui.screen.operations.account.AccountEntryDestination
import com.example.expensetracker.ui.screen.operations.entity.category.toCategoryDetails
import com.example.expensetracker.ui.screen.operations.entity.currency.toCurrencyDetails
import com.example.expensetracker.ui.screen.operations.entity.payee.toPayeeDetails
import com.example.expensetracker.ui.screen.settings.SettingsDestination
import com.example.expensetracker.ui.utils.ExpenseNavigationType
import kotlinx.coroutines.launch

@Composable
fun ExpenseApp(
    navController: NavHostController = rememberNavController(),
    windowSizeClass: WindowWidthSizeClass
) {
    val coroutineScope = rememberCoroutineScope()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val navigationType: ExpenseNavigationType = getNavigationType(windowSizeClass)

    var topBarOperation: Int by remember { mutableIntStateOf(0) }
    val openEditDialog = remember { mutableStateOf(false) }
    val openDeleteAlertDialog = remember { mutableStateOf(false) }
    var showNewDialog by remember { mutableStateOf(false) }

    val viewModel: EntityViewModel = viewModel(factory = AppViewModelProvider.Factory)

    Row(
    ) {
        if ((navigationType == ExpenseNavigationType.NAVIGATION_RAIL) or (navigationType == ExpenseNavigationType.PERMANENT_NAVIGATION_DRAWER)) {
            ExpenseNavBar(
                navigateToScreen = { screen -> navController.navigate(screen) },
                type = navigationType,
                currentActivity = navBackStackEntry?.destination?.route
            )
        }
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,

            topBar = {
                ExpenseTopBar(
                    selectedActivity = navBackStackEntry?.destination?.route,
                    navBarAction = { showNewDialog = true },
                    navigateToSettings = { navController.navigate(SettingsDestination.route) },
                    type = navigationType,
                )
            },
            bottomBar = {
                if (navigationType == ExpenseNavigationType.BOTTOM_NAVIGATION) {
                    ExpenseNavBar(
                        navigateToScreen = { screen -> navController.navigate(screen) },
                        type = navigationType,
                        currentActivity = navBackStackEntry?.destination?.route
                    )
                }
            },

            floatingActionButton = {
                if (navigationType == ExpenseNavigationType.BOTTOM_NAVIGATION) {
                    ExpenseFAB(navigateToScreen = { screen -> navController.navigate(screen) })
                }
            }
        ) { innerPadding ->
            val paddingValues =
                if ((navBackStackEntry?.destination?.route != AccountEntryDestination.route) and (navBackStackEntry?.destination?.route != SettingsDestination.route) and (navBackStackEntry?.destination?.route != "SettingsDetail/{setting}"))
                    innerPadding
                else
                    PaddingValues()

            ExpenseNavHost(
                navController = navController,
                windowSizeClass = windowSizeClass,
                setTopBarAction = { action: Int -> topBarOperation = action },
                innerPadding = paddingValues
            )
        }
    }


    if (showNewDialog) {
        when (topBarOperation) {
            0 -> {
                CategoryEntryDialog(
                    onDismissRequest = { showNewDialog = false },
                    onConfirmClick = {
                        coroutineScope.launch {
                            viewModel.saveCategory()

                        }
                    }
                )
            }

            1 -> {
                PayeeEntryDialog(
                    onDismissRequest = { showNewDialog = false },
                    onConfirmClick = {
                        coroutineScope.launch {
                            viewModel.savePayee()
                        }
                    }
                )
            }

            2 -> {
                CurrencyEntryDialog(
                    onDismissRequest = { showNewDialog = false },
                    onConfirmClick = {
                        coroutineScope.launch {
                            viewModel.saveCurrency()
                        }
                    }
                )
            }

            9 -> {
                navController.navigate(AccountEntryDestination.route)
                showNewDialog = false
            }
        }
    }

    if (openDeleteAlertDialog.value) {
        when (topBarOperation) {
            0 -> {
                DeleteConfirmationDialog({ openDeleteAlertDialog.value = false }, {
                    coroutineScope.launch {
                        viewModel.deleteCategory(viewModel.selectedCategory)
                    }
                }, "Are you sure you want to delete this category, big boi?")
            }

            1 -> {
                DeleteConfirmationDialog({ openDeleteAlertDialog.value = false }, {
                    coroutineScope.launch {
                        viewModel.deletePayee(viewModel.selectedPayee)
                    }
                }, "Are you sure you want to delete this payee, big boi?")
            }

            2 -> {
                DeleteConfirmationDialog({ openDeleteAlertDialog.value = false }, {
                    coroutineScope.launch {
                        viewModel.deleteCurrency(viewModel.selectedCurrency)
                    }
                }, "Are you sure you want to delete this currency, big boi?")
            }
        }
    }
    if (openEditDialog.value) {
        when (topBarOperation) {
            0 -> {
                CategoryEntryDialog(
                    onConfirmClick = {
                        coroutineScope.launch {
                            viewModel.editCategory()
                        }
                    },
                    onDismissRequest = { openEditDialog.value = !openEditDialog.value },
                    edit = true,
                    title = "Edit Category",
                    selectedCategory = viewModel.selectedCategory.toCategoryDetails()
                )
            }

            1 -> {
                PayeeEntryDialog(
                    onConfirmClick = {
                        coroutineScope.launch {
                            viewModel.editPayee()
                        }
                    },
                    onDismissRequest = { openEditDialog.value = !openEditDialog.value },
                    edit = true,
                    title = "Edit Payee",
                    selectedPayee = viewModel.selectedPayee.toPayeeDetails()
                )
            }

            2 -> {
                CurrencyEntryDialog(
                    onConfirmClick = {
                        coroutineScope.launch {
                            viewModel.editCurrency()
                        }
                    },
                    onDismissRequest = { openEditDialog.value = !openEditDialog.value },
                    edit = true,
                    title = "Edit Currency",
                    selectedCurrency = viewModel.selectedCurrency.toCurrencyDetails()
                )
            }
        }
    }
}

fun getNavigationType(windowSizeClass: WindowWidthSizeClass): ExpenseNavigationType {
    return when (windowSizeClass) {
        WindowWidthSizeClass.Compact -> ExpenseNavigationType.BOTTOM_NAVIGATION
        WindowWidthSizeClass.Medium -> ExpenseNavigationType.NAVIGATION_RAIL
        WindowWidthSizeClass.Expanded -> {
            // Need to fix this!!
            if (false) {
                ExpenseNavigationType.NAVIGATION_RAIL
            } else {
                ExpenseNavigationType.PERMANENT_NAVIGATION_DRAWER
            }
        }

        else -> ExpenseNavigationType.BOTTOM_NAVIGATION
    }
}