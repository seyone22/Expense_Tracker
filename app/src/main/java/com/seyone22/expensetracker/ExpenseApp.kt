package com.seyone22.expensetracker

import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
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
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
import com.seyone22.expensetracker.data.model.BillsDeposits
import com.seyone22.expensetracker.data.model.Category
import com.seyone22.expensetracker.data.model.CurrencyFormat
import com.seyone22.expensetracker.data.model.Payee
import com.seyone22.expensetracker.data.model.Transaction
import com.seyone22.expensetracker.data.model.toCategoryDetails
import com.seyone22.expensetracker.ui.AppViewModelProvider
import com.seyone22.expensetracker.ui.common.ExpenseFAB
import com.seyone22.expensetracker.ui.common.ExpenseNavBar
import com.seyone22.expensetracker.ui.common.ExpenseTopBar
import com.seyone22.expensetracker.ui.common.dialogs.CategoryEntryDialog
import com.seyone22.expensetracker.ui.common.dialogs.CurrencyEntryDialog
import com.seyone22.expensetracker.ui.common.dialogs.DeleteConfirmationDialog
import com.seyone22.expensetracker.ui.common.dialogs.EditTransactionDialog
import com.seyone22.expensetracker.ui.common.dialogs.PayeeEntryDialog
import com.seyone22.expensetracker.ui.navigation.ExpenseNavHost
import com.seyone22.expensetracker.ui.screen.entities.EntityViewModel
import com.seyone22.expensetracker.ui.screen.operations.account.AccountEntryDestination
import com.seyone22.expensetracker.ui.screen.operations.entity.currency.toCurrencyDetails
import com.seyone22.expensetracker.ui.screen.operations.entity.payee.toPayeeDetails
import com.seyone22.expensetracker.ui.screen.operations.report.ReportEntryDestination
import com.seyone22.expensetracker.ui.screen.operations.transaction.TransactionEntryDestination
import com.seyone22.expensetracker.ui.screen.operations.transaction.TransactionEntryViewModel
import com.seyone22.expensetracker.ui.screen.operations.transaction.toTransactionDetails
import com.seyone22.expensetracker.ui.screen.report.ReportsDestination
import com.seyone22.expensetracker.ui.screen.settings.SettingsDestination
import com.seyone22.expensetracker.ui.screen.transactions.TransactionsViewModel
import com.seyone22.expensetracker.utils.ExpenseNavigationType
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseApp(
    navController: NavHostController = rememberNavController(),
    windowSizeClass: WindowWidthSizeClass,
    onToggleDarkTheme: (Int) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    // Auto scroll for FAB
    var offset by remember { mutableFloatStateOf(0f) }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val navigationType: ExpenseNavigationType = getNavigationType(windowSizeClass)

    var topBarOperation: Int by remember { mutableIntStateOf(0) }
    val showEditDialog = remember { mutableStateOf(false) }
    val showDeleteDialog = remember { mutableStateOf(false) }
    var showNewDialog by remember { mutableStateOf(false) }

    var isSelected by remember { mutableStateOf(false) }
    var selectedObject by remember { mutableStateOf(SelectedObjects()) }

    val viewModel: EntityViewModel = viewModel(factory = AppViewModelProvider.Factory)
    val transactionViewModel: TransactionsViewModel =
        viewModel(factory = AppViewModelProvider.Factory)
    val transactionEntryViewModel: TransactionEntryViewModel =
        viewModel(factory = AppViewModelProvider.Factory)

    val snackbarHostState = remember { SnackbarHostState() }

    val context = LocalContext.current

    if (navBackStackEntry?.destination?.route == ReportsDestination.route) {
        LaunchedEffect(Unit) {
        }
    }

    Row{
        if ((navigationType == ExpenseNavigationType.NAVIGATION_RAIL) or (navigationType == ExpenseNavigationType.PERMANENT_NAVIGATION_DRAWER)) {
            ExpenseNavBar(
                navigateToScreen = { screen -> navController.navigate(screen) },
                type = navigationType,
                currentActivity = navBackStackEntry?.destination?.route
            )
        }
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,

            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },

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
                        navController = navController,
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
                    navBackStackEntry?.destination?.route?.let {
                        ExpenseFAB(navigateToScreen = { screen ->
                            navController.navigate(
                                screen
                            )
                        }, currentActivity = it, extended = false)
                    }
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
                onToggleDarkTheme = { onToggleDarkTheme(it) },
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
                    },
                    coroutineScope = coroutineScope,
                    transactionViewModel = transactionEntryViewModel
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

            12 -> {
                navController.navigate(ReportEntryDestination.route)
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
                    selectedCategory = (selectedObject.category).toCategoryDetails(),
                    coroutineScope = coroutineScope,
                    transactionViewModel = transactionEntryViewModel
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
                EditTransactionDialog(
                    selectedTransaction = (selectedObject.transaction.toTransactionDetails()),
                    onConfirmClick = {
                        coroutineScope.launch {
                            transactionViewModel.transactionUiState =
                                transactionEntryViewModel.transactionUiState.value
                            transactionViewModel.editTransaction(transactionViewModel.transactionUiState.transactionDetails)
                        }
                    },
                    onDismissRequest = { showEditDialog.value = !showEditDialog.value },
                    edit = true,
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

data class SelectedObjects(
    val transaction: Transaction = Transaction(),
    val billsDeposits: BillsDeposits = BillsDeposits(),
    val payee: Payee = Payee(),
    val category: Category = Category(),
    val currency: CurrencyFormat = CurrencyFormat()
)