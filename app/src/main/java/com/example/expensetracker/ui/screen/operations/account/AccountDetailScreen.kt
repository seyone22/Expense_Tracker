package com.example.expensetracker.ui.screen.operations.account

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.expensetracker.R
import com.example.expensetracker.model.Account
import com.example.expensetracker.model.Transaction
import com.example.expensetracker.ui.AppViewModelProvider
import com.example.expensetracker.ui.common.EntryFields
import com.example.expensetracker.ui.common.ExpenseTopBar
import com.example.expensetracker.ui.common.TransactionEditDialog
import com.example.expensetracker.ui.navigation.NavigationDestination
import com.example.expensetracker.ui.screen.accounts.AccountsDestination
import com.example.expensetracker.ui.screen.settings.SettingsDestination
import com.example.expensetracker.ui.screen.transactions.TransactionList
import com.example.expensetracker.ui.screen.transactions.TransactionsDestination
import kotlinx.coroutines.launch

object AccountDetailDestination : NavigationDestination {
    override val route = "AccountDetails"
    override val titleRes = R.string.app_name
    override val routeId = 13
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountDetailScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    backStackEntry: String,
    viewModel: AccountDetailViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    viewModel.accountId = backStackEntry.toInt()

    val accountDetailAccountUiState by viewModel.accountDetailAccountUiState.collectAsState()
    val accountDetailTransactionUiState by viewModel.accountDetailTransactionUiState.collectAsState()

    var isSelected by remember { mutableStateOf(false) }
    var selectedTransaction by remember { mutableStateOf(Transaction()) }
    val openEditDialog = remember { mutableStateOf(false) }
    val openEditType = remember { mutableStateOf(EntryFields.ACCOUNT) }

    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.getTransactions()
        viewModel.getAccount()
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            if (isSelected) {
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
                                openEditType.value = EntryFields.TRANSACTION
                            }) {
                                Icon(
                                    imageVector = Icons.Filled.Edit,
                                    contentDescription = "Edit"
                                )
                            }
                            IconButton(
                                onClick = {
                                    isSelected = !isSelected
                                    coroutineScope.launch {
                                        viewModel.deleteTransaction(
                                            selectedTransaction
                                        )
                                    }
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
                    selectedActivity = AccountsDestination.routeId,
                    navBarAction = { navController.navigate(AccountEntryDestination.route) },
                    navigateToSettings = { navController.navigate(SettingsDestination.route) }
                )
            }
        }
    ) {
        Column(
            modifier = modifier
                .padding(it)
                .padding(0.dp, 100.dp)
        ) {
            Card(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(24.dp, 0.dp)
            ) {
                Row(
                    modifier = modifier
                        .padding(16.dp, 8.dp, 0.dp, 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(
                        modifier = Modifier.width(300.dp)
                    ) {
                            Column {
                                Text(
                                    text = accountDetailAccountUiState.account.accountName,
                                    style = MaterialTheme.typography.headlineSmall
                                )
                                Text(
                                    text = accountDetailAccountUiState.account.accountType + " Account",
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                        HorizontalDivider()
                        Text(
                            text = "Account Balance : " + (accountDetailAccountUiState.account.initialBalance?.plus(
                                accountDetailAccountUiState.balance
                            )).toString(),
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "Reconciled Balance : " + (accountDetailAccountUiState.account.initialBalance?.plus(
                                accountDetailAccountUiState.balance
                            )).toString(),
                            style = MaterialTheme.typography.titleSmall
                        )
                    }
                    IconButton(onClick = {
                        openEditDialog.value = !openEditDialog.value
                        openEditType.value = EntryFields.ACCOUNT
                    }) {
                        Icon(
                            imageVector = Icons.Outlined.Edit,
                            contentDescription = null,
                            Modifier.size(24.dp, 24.dp)
                        )
                    }
                }
            }
            if (accountDetailTransactionUiState.transactions.isNotEmpty()) {
                Column {
                    TransactionList(
                        transactions = accountDetailTransactionUiState.transactions,
                        modifier = modifier,
                        longClicked = { selected ->
                            isSelected = !isSelected
                            selectedTransaction = selected
                        },
                    )
                }
            }

            if (openEditDialog.value and (openEditType.value == EntryFields.TRANSACTION)) {
                TransactionEditDialog(
                    onConfirmClick = {
                        coroutineScope.launch {
                            viewModel.editTransaction()
                        }
                    },
                    onDismissRequest = { openEditDialog.value = !openEditDialog.value },
                    edit = true,
                    title = "Edit Transaction",
                    selectedTransaction = selectedTransaction
                )
            }
            if (openEditDialog.value and (openEditType.value == EntryFields.ACCOUNT)) {
                AccountEditDialog(
                    onConfirmClick = {
                        coroutineScope.launch {
                            viewModel.editTransaction()
                        }
                    },
                    onDismissRequest = { openEditDialog.value = !openEditDialog.value },
                    viewModel = viewModel,
                    edit = true,
                    title = "Edit Account",
                    selectedAccount = accountDetailAccountUiState.account
                )
            }
        }
    }
}



@SuppressLint("UnrememberedMutableState")
@Composable
fun AccountEditDialog(
    modifier: Modifier = Modifier,
    title: String,
    selectedAccount: Account,
    onConfirmClick: () -> Unit,
    onDismissRequest: () -> Unit,
    viewModel: ViewModel,
    edit: Boolean = false
) {
    val focusManager = LocalFocusManager.current
    val accountSelected by remember { mutableStateOf(selectedAccount.toAccountDetails()) }

    /*    viewModel.updateCurrencyState(
            viewModel.currencyUiState.currencyDetails.copy(
                currencyName = selectedCurrency.currencyName
            )
        )*/
    Dialog(
        onDismissRequest = { onDismissRequest() }
    )
    {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .height(1000.dp)
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
                LazyColumn() {
                    item {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleLarge
                        )

                        AccountEntryForm(
                            accountDetails = accountSelected
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                        ) {
                            TextButton(
                                onClick = { onDismissRequest() },
                                modifier = Modifier.padding(8.dp),
                            ) {
                                Text("Dismiss")
                            }
                            TextButton(
                                onClick = {
                                    onConfirmClick()
                                    onDismissRequest()
                                },
                                modifier = Modifier.padding(8.dp),
                                enabled = true
                            ) {
                                Text("Confirm")
                            }
                        }
                    }
                }
            }
        }
    }
}

