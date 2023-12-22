package com.example.expensetracker.ui.screen.transactions

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.expensetracker.R
import com.example.expensetracker.data.transaction.TransactionsRepository
import com.example.expensetracker.model.CurrencyFormat
import com.example.expensetracker.model.TransactionWithDetails
import com.example.expensetracker.ui.AppViewModelProvider
import com.example.expensetracker.ui.common.ExpenseFAB
import com.example.expensetracker.ui.common.ExpenseNavBar
import com.example.expensetracker.ui.common.ExpenseTopBar
import com.example.expensetracker.ui.common.FormattedCurrency
import com.example.expensetracker.ui.common.SortBar
import com.example.expensetracker.ui.navigation.NavigationDestination
import com.example.expensetracker.ui.screen.operations.account.AccountEntryDestination
import com.example.expensetracker.ui.screen.settings.SettingsDestination

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
                            IconButton(onClick = { isSelected = !isSelected }) {
                                Icon(
                                    imageVector = Icons.Filled.Edit,
                                    contentDescription = "Edit"
                                )
                            }
                            IconButton(onClick = { isSelected = !isSelected }) {
                                Icon(
                                    imageVector = Icons.Filled.Delete,
                                    contentDescription = "Delete"
                                )
                            }
                            IconButton(onClick = { isSelected = !isSelected }) {
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
            SortBar()
            TransactionList(
                transactions = transactionsUiState.transactions,
                longClicked = { isSelected = !isSelected },
                viewModel = viewModel
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TransactionList(
    modifier: Modifier = Modifier,
    transactions: List<TransactionWithDetails>,
    longClicked: () -> Unit,
    viewModel: TransactionsViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val haptics = LocalHapticFeedback.current
    LazyColumn(
        modifier = modifier
    ) {
        items(count = transactions.size) {
            ListItem(
                headlineContent = {
                    Text(text = transactions[it].payeeName)
                },
                supportingContent = {
                    Text(text = transactions[it].categName)
                },
                trailingContent = {
                    val accountId = transactions[it].accountId
                    var currencyFormat : CurrencyFormat = CurrencyFormat()

                    LaunchedEffect(accountId) {
                        val currencyFormatFunction =
                            viewModel.getAccountFromId(accountId)
                                ?.let { it1 -> viewModel.getCurrencyFormatById(it1.currencyId) }
                        currencyFormat = currencyFormatFunction!!
                    }
                    // Now you can use 'currencyFormat' in your FormattedCurrency composable
                    //TODO : DOESN'T WORK
                    FormattedCurrency(value = transactions[it].transAmount, currency = currencyFormat)
                },
                leadingContent = {
                    Text(text = transactions[it].transCode[0].toString())
                },
                overlineContent = {
                    Text(text = transactions[it].transDate!!)
                },
                modifier = Modifier.combinedClickable(
                    onClick = {},
                    onLongClick = {
                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                        longClicked()
                    },
                    onLongClickLabel = "  "
                )
            )
            HorizontalDivider()

        }
    }
}