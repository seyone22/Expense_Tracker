package com.example.expensetracker

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
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.expensetracker.model.Account
import com.example.expensetracker.ui.AppViewModelProvider
import com.example.expensetracker.ui.account.AccountEntryDestination
import com.example.expensetracker.ui.account.AccountEntryScreen
import com.example.expensetracker.ui.navigation.NavigationDestination
import com.example.expensetracker.ui.theme.ExpenseTrackerTheme
import com.example.expensetracker.ui.transaction.TransactionEntryDestination

enum class AccountTypes(val displayName: String) {
    CASH("Cash"),
    CHECKING("Checking"),
    CREDIT_CARD("Credit Card"),
    LOAN("Loan"),
    TERM("Term"),
    INVESTMENT("Investment"),
    ASSET("Asset"),
    SHARES("Shares")
}

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
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text(activitiesAndIcons[selectedActivity].activity)
                },
                navigationIcon = {
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(
                            imageVector = Icons.Filled.Menu,
                            contentDescription = "Description"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(
                            imageVector = Icons.Outlined.AccountCircle,
                            contentDescription = "User"
                        )
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                activitiesAndIcons.forEachIndexed { index, pair ->
                    NavigationBarItem(
                        icon = { Icon(pair.icon, contentDescription = pair.activity) },
                        label = { Text(pair.activity) },
                        selected = selectedActivity == index,
                        onClick = { selectedActivity = index; navigateToScreen(pair.activity) }
                    )
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                navigateToScreen(TransactionEntryDestination.route)
            }) {
                Icon(Icons.Outlined.Edit, "Add")
            }
        }
    ) { innerPadding ->
        LazyColumn(
            Modifier.padding(innerPadding)
        ) {
            item {
                Text("Current Month Summary")
                Text("Your Accounts")
                enumValues<AccountTypes>().forEach { accountType ->
                    val displayName: String = accountType.displayName
                    AccountList(
                        category = displayName,
                        accountList = accountUiState.accountList,
                    )
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
    accountList: List<Account>,
    modifier: Modifier = Modifier

) {
    Column(
        Modifier.padding(16.dp, 12.dp),

    ) {
        Text(text = category, style = MaterialTheme.typography.titleLarge)
        Column(modifier = modifier) {
            accountList.forEach { account ->
                AccountCard(
                    account = account,
                    modifier = Modifier
                )
            }
        }
    }
}

@Composable
fun AccountCard(account: Account, modifier: Modifier = Modifier) {
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
                    contentDescription = "Description",
                    Modifier.size(36.dp, 36.dp)
                )
            }
            Column(
                Modifier.weight(3f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = account.accountName,
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    text = account.status,
                    style = MaterialTheme.typography.labelLarge
                )
            }
            Column(
                Modifier.weight(2f)
                    .fillMaxHeight()
                    .padding(0.dp, 0.dp, 12.dp, 0.dp),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = account.initialBalance.toString()
                )
                Text(
                    text = "Rs. 80.22"
                )
            }
        }
    }
}

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
}