package com.example.expensetracker

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.expensetracker.model.Category
import com.example.expensetracker.model.CurrencyFormat
import com.example.expensetracker.model.Payee
import com.example.expensetracker.model.Transaction
import com.example.expensetracker.ui.AppViewModelProvider
import com.example.expensetracker.ui.common.ExpenseFAB
import com.example.expensetracker.ui.common.ExpenseNavBar
import com.example.expensetracker.ui.common.ExpenseTopBar
import com.example.expensetracker.ui.common.TransactionEditDialog
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
import com.example.expensetracker.ui.screen.operations.transaction.TransactionEntryDestination
import com.example.expensetracker.ui.screen.settings.SettingsDestination
import com.example.expensetracker.ui.screen.transactions.TransactionsViewModel
import com.example.expensetracker.ui.utils.ExpenseNavigationType
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseApp(
    navController: NavHostController = rememberNavController(),
    windowSizeClass: WindowWidthSizeClass,
) {
    val coroutineScope = rememberCoroutineScope()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val navigationType: ExpenseNavigationType = getNavigationType(windowSizeClass)

    var topBarOperation: Int by remember { mutableIntStateOf(0) }
    val showEditDialog = remember { mutableStateOf(false) }
    val showDeleteDialog = remember { mutableStateOf(false) }
    var showNewDialog by remember { mutableStateOf(false) }

    var isSelected by remember { mutableStateOf(false) }
    var selectedObject by remember {  mutableStateOf(SelectedObjects()) }

    val viewModel: EntityViewModel = viewModel(factory = AppViewModelProvider.Factory)
    val transactionViewModel: TransactionsViewModel =
        viewModel(factory = AppViewModelProvider.Factory)

    val context = LocalContext.current

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
                    if (isSelected) {
                        TopAppBar(
                            title = { Text(text = navBackStackEntry?.destination?.route ?: "") },
                            navigationIcon = {
                                IconButton(onClick = { isSelected = !isSelected }) {
                                    Icon(
                                        imageVector = Icons.Filled.Close,
                                        contentDescription = "Close"
                                    )
                                }
                            },
                            actions = {
                                Row {
                                    IconButton(onClick = {
                                        isSelected = !isSelected
                                        showEditDialog.value = !showEditDialog.value

                                        Log.d("TAG", "ExpenseApp: $showEditDialog $topBarOperation")
                                    }) {
                                        Icon(
                                            imageVector = Icons.Filled.Edit,
                                            contentDescription = "Edit"
                                        )
                                    }
                                    IconButton(
                                        onClick = {
                                            isSelected = !isSelected
                                            showDeleteDialog.value = !showDeleteDialog.value
                                        }

                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.Delete,
                                            contentDescription = "Delete"
                                        )
                                    }
                                    IconButton(onClick = {
                                        isSelected = !isSelected
                                        Toast.makeText(context, "Unimplemented", Toast.LENGTH_SHORT)
                                            .show()
                                    }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.Share,
                                            contentDescription = "Share"
                                        )
                                    }
                                }
                            }
                        )
                    } else {
                        ExpenseTopBar(
                            selectedActivity = navBackStackEntry?.destination?.route,
                            navBarAction = { showNewDialog = true },
                            navigateToSettings = { navController.navigate(SettingsDestination.route) },
                            type = navigationType,
                        )
                    }
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
                    if ((navBackStackEntry?.destination?.route != AccountEntryDestination.route) and (navBackStackEntry?.destination?.route != SettingsDestination.route) and (navBackStackEntry?.destination?.route != "SettingsDetail/{setting}") and (navBackStackEntry?.destination?.route != TransactionEntryDestination.route))
                        innerPadding
                    else
                        PaddingValues()

                ExpenseNavHost(
                    navController = navController,
                    windowSizeClass = windowSizeClass,
                    setTopBarAction = { action: Int -> topBarOperation = action },
                    setIsItemSelected = { boolean: Boolean -> isSelected = boolean },
                    setSelectedObject = { item ->
                        selectedObject = item
                                        },
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

        if (showDeleteDialog.value) {
            when (topBarOperation) {
                0 -> {
                    DeleteConfirmationDialog({ showDeleteDialog.value = false }, {
                        coroutineScope.launch {
                            viewModel.deleteCategory(selectedObject.category)
                        }
                    }, "Are you sure you want to delete this category, big boi?")
                }

                1 -> {
                    DeleteConfirmationDialog({ showDeleteDialog.value = false }, {
                        coroutineScope.launch {
                            viewModel.deletePayee(selectedObject.payee)
                        }
                    }, "Are you sure you want to delete this payee, big boi?")
                }

                2 -> {
                    DeleteConfirmationDialog({ showDeleteDialog.value = false }, {
                        coroutineScope.launch {
                            viewModel.deleteCurrency(selectedObject.currency)
                        }
                    }, "Are you sure you want to delete this currency, big boi?")
                }

                8 -> {
                    Log.d("TAG", "ExpenseApp: $selectedObject")
                    DeleteConfirmationDialog({ showDeleteDialog.value = false }, {
                        coroutineScope.launch {
                            transactionViewModel.deleteTransaction(selectedObject.transaction)
                        }
                    }, "Are you sure you want to delete this transaction, big boi?")
                }
            }
        }
        if (showEditDialog.value) {
            when (topBarOperation) {
                0 -> {
                    Log.d("TAG", "ExpenseApp: ${viewModel.selectedCategory}")
                    CategoryEntryDialog(
                        onConfirmClick = {
                            coroutineScope.launch {
                                viewModel.editCategory()
                            }
                        },
                        onDismissRequest = { showEditDialog.value = !showEditDialog.value },
                        edit = true,
                        title = "Edit Category",
                        selectedCategory = (selectedObject.category).toCategoryDetails()
                    )
                }

                1 -> {
                    PayeeEntryDialog(
                        onConfirmClick = {
                            coroutineScope.launch {
                                viewModel.editPayee()
                            }
                        },
                        onDismissRequest = { showEditDialog.value = !showEditDialog.value },
                        edit = true,
                        title = "Edit Payee",
                        selectedPayee = (selectedObject.payee).toPayeeDetails()
                    )
                }

                2 -> {
                    CurrencyEntryDialog(
                        onConfirmClick = {
                            coroutineScope.launch {
                                viewModel.editCurrency()
                            }
                        },
                        onDismissRequest = { showEditDialog.value = !showEditDialog.value },
                        edit = true,
                        title = "Edit Currency",
                        selectedCurrency = (selectedObject.currency).toCurrencyDetails()
                    )
                }

                8 -> {
                    Log.d("TAG", "TransactionsScreen: ${transactionViewModel.hashCode()}")

                    TransactionEditDialog(
                        selectedTransaction = (selectedObject.transaction),
                        onConfirmClick = {
                            coroutineScope.launch {
                                transactionViewModel.editTransaction()
                            }
                        },
                        onDismissRequest = { showEditDialog.value = !showEditDialog.value })
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

data class SelectedObjects(
    val transaction : Transaction = Transaction(),
    val payee : Payee = Payee(),
    val category : Category = Category(),
    val currency : CurrencyFormat = CurrencyFormat()
)