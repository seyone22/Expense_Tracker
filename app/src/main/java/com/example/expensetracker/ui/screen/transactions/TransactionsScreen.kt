package com.example.expensetracker.ui.screen.transactions

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.expensetracker.R
import com.example.expensetracker.model.CurrencyFormat
import com.example.expensetracker.model.Transaction
import com.example.expensetracker.model.TransactionWithDetails
import com.example.expensetracker.model.toTransaction
import com.example.expensetracker.ui.AppViewModelProvider
import com.example.expensetracker.ui.common.ExpenseFAB
import com.example.expensetracker.ui.common.ExpenseNavBar
import com.example.expensetracker.ui.common.ExpenseTopBar
import com.example.expensetracker.ui.common.FormattedCurrency
import com.example.expensetracker.ui.common.SortBar
import com.example.expensetracker.ui.common.removeTrPrefix
import com.example.expensetracker.ui.navigation.NavigationDestination
import com.example.expensetracker.ui.screen.entities.CategoryEntryDialog
import com.example.expensetracker.ui.screen.entities.EntityViewModel
import com.example.expensetracker.ui.screen.operations.account.AccountEntryDestination
import com.example.expensetracker.ui.screen.operations.entity.category.toCategoryDetails
import com.example.expensetracker.ui.screen.operations.entity.currency.CurrencyDetails
import com.example.expensetracker.ui.screen.settings.SettingsDestination
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object TransactionsDestination : NavigationDestination {
    override val route = "Entries"
    override val titleRes = R.string.app_name
    override val routeId = 3
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionsScreen(
    modifier: Modifier = Modifier,
    navigateToScreen: (screen: String) -> Unit,
    viewModel: TransactionsViewModel = viewModel(factory = AppViewModelProvider.Factory),

    ) {
    val transactionsUiState by viewModel.transactionsUiState.collectAsState()
    var isSelected by remember { mutableStateOf(false) }
    var selectedTransaction by remember { mutableStateOf(Transaction()) }

    val openEditDialog = remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            if(isSelected) {
                TopAppBar(
                    title = { Text(text = TransactionsDestination.route) },
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
                                openEditDialog.value = !openEditDialog.value
                            }) {
                                Icon(
                                    imageVector = Icons.Filled.Edit,
                                    contentDescription = "Edit"
                                )
                            }
                            IconButton(
                                onClick = {
                                    isSelected = !isSelected
                                    coroutineScope.launch { viewModel.deleteTransaction(selectedTransaction) }
                                }

                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Delete,
                                    contentDescription = "Delete"
                                )
                            }
                            IconButton(onClick = {
                                isSelected = !isSelected
                                Toast.makeText(context, "Unimplemented", Toast.LENGTH_SHORT).show()
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
                    selectedActivity = TransactionsDestination.routeId,
                    navBarAction = { navigateToScreen(AccountEntryDestination.route) },
                    navigateToSettings = { navigateToScreen(SettingsDestination.route) }
                )
            }
        },
        bottomBar = {
            ExpenseNavBar(
                selectedActivity = TransactionsDestination.routeId,
                navigateToScreen = navigateToScreen
            )
        },
        floatingActionButton = {
            ExpenseFAB(navigateToScreen = navigateToScreen)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding)
        ) {

            TransactionList(
                transactions = transactionsUiState.transactions,
                longClicked = { selected ->
                    isSelected = !isSelected
                    selectedTransaction = selected
                              },
                viewModel = viewModel
            )
        }
    }

    if (openEditDialog.value) {
        TransactionEditDialog(
            onConfirmClick = {
                coroutineScope.launch {
                    viewModel.editTransaction()
                }
            },
            onDismissRequest = { openEditDialog.value = !openEditDialog.value },
            viewModel = viewModel,
            edit = true,
            title = "Edit Transaction",
            selectedTransaction = selectedTransaction
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TransactionList(
    modifier: Modifier = Modifier,
    transactions: List<TransactionWithDetails>,
    longClicked: (Transaction) -> Unit,
    viewModel: TransactionsViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val haptics = LocalHapticFeedback.current
    var filteredTransactions by remember { mutableStateOf(transactions) }

    SortBar(
        periodSortAction = { sortCase ->
            filteredTransactions = when (sortCase) {
                0 -> transactions.filter {
                    val transactionDate = LocalDate.parse(it.transDate, DateTimeFormatter.ISO_LOCAL_DATE)
                    transactionDate.monthValue == LocalDate.now().monthValue
                }
                // Add more cases as needed
                else -> transactions // No filtering for other cases
            }
        }
    )

    LazyColumn(
        modifier = modifier
    ) {
        items(count = filteredTransactions.size) {
            ListItem(
                headlineContent = {
                    Text(text = filteredTransactions[it].payeeName)
                },
                supportingContent = {
                    Text(text =  removeTrPrefix(filteredTransactions[it].categName))
                },
                trailingContent = {
                    val accountId = filteredTransactions[it].accountId
                    var currencyFormat = CurrencyFormat()

                    LaunchedEffect(accountId) {
                        val currencyFormatFunction =
                            viewModel.getAccountFromId(accountId)
                                ?.let { it1 -> viewModel.getCurrencyFormatById(it1.currencyId) }
                        currencyFormat = currencyFormatFunction!!
                    }
                    // Now you can use 'currencyFormat' in your FormattedCurrency composable
                    //TODO : DOESN'T WORK
                    FormattedCurrency(value = filteredTransactions[it].transAmount, currency = currencyFormat)
                },
                leadingContent = {
                    Text(text = filteredTransactions[it].transCode[0].toString())
                },
                overlineContent = {
                    Text(text = filteredTransactions[it].transDate!!)
                },
                modifier = Modifier.combinedClickable(
                    onClick = {},
                    onLongClick = {
                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                        longClicked(filteredTransactions[it].toTransaction())
                    },
                    onLongClickLabel = "  "
                )
            )
            HorizontalDivider()
        }
    }
}

@SuppressLint("UnrememberedMutableState")
@Composable
fun TransactionEditDialog(
    modifier: Modifier = Modifier,
    title : String,
    selectedTransaction : Transaction,
    onConfirmClick: () -> Unit,
    onDismissRequest: () -> Unit,
    viewModel: TransactionsViewModel,
    edit : Boolean = false
) {
    val focusManager = LocalFocusManager.current
    var transactionSelected by remember { mutableStateOf(selectedTransaction) }

/*    viewModel.updateCurrencyState(
        viewModel.currencyUiState.currencyDetails.copy(
            currencyName = selectedCurrency.currencyName
        )
    )*/
    Dialog(
        onDismissRequest = { onDismissRequest() }
    )
    {
        // Draw a rectangle shape with rounded corners inside the dialog
        Card(
            modifier = modifier
                .fillMaxWidth()
                .height(225.dp)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp, 0.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(text = transactionSelected.transCode)
            }
        }
    }
}