package com.example.expensetracker.ui.screen.accounts

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.expensetracker.R
import com.example.expensetracker.activitiesAndIcons
import com.example.expensetracker.model.Account
import com.example.expensetracker.model.AccountTypes
import com.example.expensetracker.ui.AppViewModelProvider
import com.example.expensetracker.ui.account.AccountEntryDestination
import com.example.expensetracker.ui.common.ExpenseFAB
import com.example.expensetracker.ui.common.ExpenseNavBar
import com.example.expensetracker.ui.common.ExpenseTopBar
import com.example.expensetracker.ui.navigation.NavigationDestination
import com.example.expensetracker.ui.transaction.TransactionEntryScreen

object AccountsDestination : NavigationDestination {
    override val route = "Accounts"
    override val titleRes = R.string.app_name
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountScreen(
    navigateToAccountEntry: () -> Unit,
    navigateToScreen: (screen: String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AccountViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    var selectedActivity by remember { mutableIntStateOf(0) }
    val accountUiState by viewModel.accountsUiState.collectAsState()

    Scaffold(
        topBar = {
            ExpenseTopBar(selectedActivity = selectedActivity)
        },
        bottomBar = {
            ExpenseNavBar(selectedActivity = selectedActivity, navigateToScreen = navigateToScreen)
        },
        floatingActionButton = {
            ExpenseFAB(navigateToScreen = navigateToScreen)
        }
    ) { innerPadding ->
        LazyColumn(
            Modifier.padding(innerPadding)
        ) {
            item {
                Text("Current Month Summary")
                Text(text = accountUiState.grandTotal.toString())
                Text("Your Accounts")
                enumValues<AccountTypes>().forEach { accountType ->
                    if(viewModel.countInType(accountType, accountUiState.accountList) != 0) {
                        val displayName: String = accountType.displayName
                        AccountList(
                            category = displayName,
                            accountList = accountUiState.accountList,
                            viewModel = viewModel
                        )
                    }
                }
                Button(onClick = { navigateToScreen(AccountEntryDestination.route) }) {
                    Text(text = "New Account")
                }
            }
        }
    }
}

@Composable
fun AccountList(
    category: String,
    accountList: List<Pair<Account,Double>>,
    modifier: Modifier = Modifier,
    viewModel: AccountViewModel
) {
    Column(
        Modifier.padding(16.dp, 12.dp),

    ) {
        Text(text = category, style = MaterialTheme.typography.titleLarge)
        Column(modifier = modifier) {
            accountList.forEach { accountPair ->
                Log.d("DEBUG", "AccountList: Ping")
                if(accountPair.first.accountType == category) {
                    AccountCard(
                        accountWithBalance = accountPair,
                        modifier = Modifier,
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}

@Composable
fun AccountCard(
    accountWithBalance: Pair<Account, Double>,
    modifier: Modifier = Modifier,
    viewModel : AccountViewModel
) {
    ElevatedCard(
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(104.dp)
            .padding(0.dp, 12.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,

                ) {
                Icon(
                    imageVector = Icons.Outlined.AccountBalanceWallet,
                    contentDescription = null,
                    Modifier.size(36.dp, 36.dp)
                )
            }
            Column(
                Modifier
                    .weight(3f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = accountWithBalance.first.accountName,
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    text = accountWithBalance.first.status,
                    style = MaterialTheme.typography.labelLarge
                )
            }
            Column(
                Modifier
                    .weight(2f)
                    .fillMaxHeight()
                    .padding(0.dp, 0.dp, 12.dp, 0.dp),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Rs. "+accountWithBalance.second.toString()
                )
                Text(
                    text = "Rs. "+accountWithBalance.second.toString()
                )
            }
        }
    }
}

/*
@Preview(showBackground = true)
@Composable
fun AccountListPreview() {
    ExpenseTrackerTheme {
        AccountList(
             category = "Checking", accountList = listOf(
                Account(
                    0,
                    "8130107852 (BOC)",
                    "Checking",
                    "",
                    "Open",
                    "",
                    "",
                    "",
                    "",
                    "",
                    0.0,
                    "",
                    "",
                    0,
                    0,
                    "",
                    0.0,
                    0.0,
                    0.0,
                    "",
                    0.0
                ),
                Account(
                    0,
                    "8130107852 (BOC)",
                    "Checking",
                    "",
                    "Open",
                    "",
                    "",
                    "",
                    "",
                    "",
                    0.0,
                    "",
                    "",
                    0,
                    0,
                    "",
                    0.0,
                    0.0,
                    0.0,
                    "",
                    0.0
                ),
                Account(
                    0,
                    "8130107852 (BOC)",
                    "Checking",
                    "",
                    "Closed",
                    "",
                    "",
                    "",
                    "",
                    "",
                    0.0,
                    "",
                    "",
                    0,
                    0,
                    "",
                    0.0,
                    0.0,
                    0.0,
                    "",
                    0.0
                )
            )
        )
    }
}*/
