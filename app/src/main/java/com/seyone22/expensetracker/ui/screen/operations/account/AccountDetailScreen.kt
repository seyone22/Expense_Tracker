package com.seyone22.expensetracker.ui.screen.operations.account

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.seyone22.expensetracker.R
import com.seyone22.expensetracker.SharedViewModel
import com.seyone22.expensetracker.ui.AppViewModelProvider
import com.seyone22.expensetracker.ui.common.ExpenseTopBar
import com.seyone22.expensetracker.ui.common.FormattedCurrency
import com.seyone22.expensetracker.ui.common.dialogs.DeleteItemDialogAction
import com.seyone22.expensetracker.ui.common.dialogs.GenericDialog
import com.seyone22.expensetracker.ui.navigation.NavigationDestination
import com.seyone22.expensetracker.ui.screen.operations.account.composables.AccountDetailCard
import com.seyone22.expensetracker.ui.screen.operations.account.composables.AccountHistoryGraph
import com.seyone22.expensetracker.ui.screen.operations.transaction.TransactionEntryDestination
import com.seyone22.expensetracker.ui.screen.transactions.TransactionsViewModel
import com.seyone22.expensetracker.ui.screen.transactions.composables.TransactionFilters
import com.seyone22.expensetracker.ui.screen.transactions.composables.TransactionList
import com.seyone22.expensetracker.data.model.CurrencyFormat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
object AccountDetailDestination : NavigationDestination {
    override val route = "Account Details"
    override val titleRes = R.string.app_name
    override val routeId = 13
    override val icon = null
}

@Composable
fun AccountDetailScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    backStackEntry: String,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    viewModel: AccountDetailViewModel = viewModel(factory = AppViewModelProvider.Factory),
    transactionsViewModel: TransactionsViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    // Viewmodel and UI States
    val accountDetailUiState by viewModel.accountDetailUiState.collectAsState()
    val sharedViewModel: SharedViewModel = viewModel(factory = AppViewModelProvider.Factory)
    var currencyData by remember { mutableStateOf(CurrencyFormat()) }

    val currentDialog by viewModel.currentDialog
    currentDialog?.let {
        GenericDialog(dialogAction = it, onDismiss = { viewModel.dismissDialog() })
    }

    LaunchedEffect(Unit, accountDetailUiState.account.currencyId) {
        viewModel.setAccountId(backStackEntry.toInt())
        transactionsViewModel.setFilters(TransactionFilters(accountFilter = accountDetailUiState.account))
    }

    LaunchedEffect(accountDetailUiState.account.currencyId) {
        if (accountDetailUiState.account.currencyId != 0) {
            currencyData = sharedViewModel.getCurrencyById(accountDetailUiState.account.currencyId)
                ?: CurrencyFormat()
        }
    }

    val isFavorite = accountDetailUiState.account.favoriteAccount == "TRUE"
    val favoriteLabel = if (isFavorite) "Remove Favourite" else "Make Favourite"

    Scaffold(topBar = {
        ExpenseTopBar(
            selectedActivity = AccountDetailDestination.route,
            navController = navController,
            hasNavigation = true,
            dropdownOptions = listOf(
                "Edit" to { navController.navigate(AccountEntryDestination.route + "/$backStackEntry") },
                "Reconcile" to { navController.navigate("Reconcile/$backStackEntry") },
                "Delete" to {
                    viewModel.showDialog(
                        DeleteItemDialogAction(
                            onAdd = {
                                coroutineScope.launch {
                                    viewModel.deleteAccount(accountDetailUiState.account)
                                    navController.popBackStack()
                                }
                            }, itemName = accountDetailUiState.account.accountName
                        )
                    )
                },
                favoriteLabel to {
                    coroutineScope.launch {
                        viewModel.toggleFavoriteAccount(accountDetailUiState.account)
                    }
                })
        )
    }) {
        LazyColumn(
            modifier = modifier
                .padding(it)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Hero Balance Section
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = accountDetailUiState.account.accountName,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    FormattedCurrency(
                        style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Bold),
                        value = accountDetailUiState.balance,
                        currency = currencyData,
                        defaultColor = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = accountDetailUiState.account.accountType + " Account",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            // Quick Actions Button Row
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AssistChip(
                        onClick = { navController.navigate("Reconcile/$backStackEntry") },
                        label = { Text("Reconcile") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    )
                    AssistChip(
                        onClick = { navController.navigate(AccountEntryDestination.route + "/$backStackEntry") },
                        label = { Text("Edit") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    )
                    AssistChip(
                        onClick = { navController.navigate(TransactionEntryDestination.route + "/Withdrawal") },
                        label = { Text("Add Txn") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    )
                }
            }

            // Graph card
            item {
                AccountHistoryGraph(
                    modifier = Modifier,
                    accountDetailUiState = accountDetailUiState,
                )
            }

            // Account details card
            item {
                AccountDetailCard(
                    modifier = Modifier,
                    accountDetailUiState = accountDetailUiState,
                )
            }

            // Title text for the transactions area
            item {
                Text(
                    text = "Recent Transactions",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                )
            }

            // List of transactions for this account
            item {
                TransactionList(modifier = Modifier, showFilter = false)
            }
        }
    }
}