package com.example.expensetracker.ui.screen.operations.account

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.expensetracker.R
import com.example.expensetracker.ui.AppViewModelProvider
import com.example.expensetracker.ui.common.ExpenseTopBar
import com.example.expensetracker.ui.navigation.NavigationDestination
import com.example.expensetracker.ui.screen.accounts.AccountsDestination
import com.example.expensetracker.ui.screen.settings.SettingsDestination
import com.example.expensetracker.ui.screen.transactions.TransactionList

object AccountDetailDestination : NavigationDestination {
    override val route = "AccountDetails"
    override val titleRes = R.string.app_name
    override val routeId = 13
}

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

    LaunchedEffect( Unit ) {
        viewModel.getTransactions()
        viewModel.getAccount()
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
        topBar = {
            ExpenseTopBar(
                selectedActivity = AccountsDestination.routeId,
                navBarAction = { navController.navigate(AccountEntryDestination.route) },
                navigateToSettings = { navController.navigate(SettingsDestination.route) }
            )
        }
        ) {
        Column(
            modifier = modifier
                .padding(it)
                .padding(0.dp, 100.dp)
        ) {
            OutlinedCard(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(24.dp, 0.dp)
            ) {
                Column(
                    modifier = modifier
                        .padding(16.dp, 8.dp)
                ) {
                    Row {
                        Column {
                            Icon(
                                imageVector = Icons.Outlined.AccountBalanceWallet,
                                contentDescription = null,
                                Modifier.size(36.dp, 36.dp)
                            )
                        }
                        Column {
                            Text(
                                text = accountDetailAccountUiState.account.accountName,
                                style = MaterialTheme.typography.titleLarge
                            )
                            Text(
                                text = accountDetailAccountUiState.account.accountType + " Account",
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                        Column {
                            IconButton(onClick = { /*TODO*/ }) {
                                Icon(
                                    imageVector = Icons.Outlined.Edit,
                                    contentDescription = null,
                                    Modifier.size(36.dp, 36.dp)
                                )
                            }
                        }
                    }
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
            }
            if (!accountDetailTransactionUiState.transactions.isNullOrEmpty()) {
                Column {
                    TransactionList(
                        transactions = accountDetailTransactionUiState.transactions,
                        modifier = modifier,
                        setSelected = { }
                    )
                }
            }
        }
    }
}