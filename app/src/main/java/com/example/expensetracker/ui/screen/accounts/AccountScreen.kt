package com.example.expensetracker.ui.screen.accounts

import android.util.Log
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.expensetracker.R
import com.example.expensetracker.model.Account
import com.example.expensetracker.model.AccountTypes
import com.example.expensetracker.model.CurrencyFormat
import com.example.expensetracker.ui.AppViewModelProvider
import com.example.expensetracker.ui.common.AnimatedCircle
import com.example.expensetracker.ui.common.ExpenseFAB
import com.example.expensetracker.ui.common.ExpenseNavBar
import com.example.expensetracker.ui.common.ExpenseTopBar
import com.example.expensetracker.ui.navigation.NavigationDestination
import com.example.expensetracker.ui.screen.operations.account.AccountEntryDestination
import com.example.expensetracker.ui.screen.settings.SettingsDestination
import kotlinx.coroutines.CoroutineScope

object AccountsDestination : NavigationDestination {
    override val route = "Accounts"
    override val titleRes = R.string.app_name
    override val routeId = 0
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountScreen(
    modifier: Modifier = Modifier
        .padding(16.dp, 12.dp),
    navigateToScreen: (screen: String) -> Unit,
    viewModel: AccountViewModel = viewModel(factory = AppViewModelProvider.Factory),

    ) {
    val accountUiState by viewModel.accountsUiState.collectAsState()
    val baseCurrencyId by viewModel.baseCurrencyId.collectAsState()
    var baseCurrencyInfo = CurrencyFormat(0, "", "", "", "", "", "", "", 0, 0.0, "", "")
    LaunchedEffect(baseCurrencyId) {
        baseCurrencyInfo = viewModel.getBaseCurrencyInfo(baseCurrencyId = baseCurrencyId)
    }
    Log.d("DEBUG", "AccountScreen: BaseCurrencyID is $baseCurrencyId")
    Log.d("DEBUG", "AccountScreen: BaseCurrencyInfo is $baseCurrencyInfo")

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
        topBar = {
            ExpenseTopBar(
                selectedActivity = AccountsDestination.routeId,
                navBarAction = { navigateToScreen(AccountEntryDestination.route) },
                navigateToSettings = { navigateToScreen(SettingsDestination.route) }
            )
        },
        bottomBar = {
            ExpenseNavBar(
                selectedActivity = AccountsDestination.routeId,
                navigateToScreen = navigateToScreen
            )
        },
        floatingActionButton = {
            ExpenseFAB(navigateToScreen = navigateToScreen)
        }
    ) { innerPadding ->
        LazyColumn(
            Modifier.padding(innerPadding)
        ) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                ) {
                    AnimatedCircle(
                        proportions = listOf(0.25f, 0.5f),
                        colors = listOf(Color.Green, Color.Red),
                        modifier = modifier
                            .height(300.dp)
                            .align(Alignment.CenterHorizontally)
                            .fillMaxWidth()
                    )
                }

                Column(
                    modifier = modifier,
                ) {
                    Text("Current Month Summary")
                    Text(text = accountUiState.grandTotal.toString())

                    Text(
                        text = "Summary of Accounts",
                        style = MaterialTheme.typography.titleLarge
                    )
                }

                enumValues<AccountTypes>().forEach { accountType ->
                    if (viewModel.countInType(accountType, accountUiState.accountList) != 0) {
                        val displayName: String = accountType.displayName
                        AccountList(
                            modifier = modifier,
                            category = displayName,
                            accountList = accountUiState.accountList,
                            viewModel = viewModel,
                            navigateToScreen = navigateToScreen
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AccountList(
    modifier: Modifier = Modifier,
    category: String,
    accountList: List<Pair<Account, Double>>,
    viewModel: AccountViewModel,
    navigateToScreen: (screen: String) -> Unit,
) {
    Column(
        modifier = modifier,
    ) {
        Text(
            text = category,
            style = MaterialTheme.typography.titleMedium
        )

        accountList.forEach { accountPair ->
            Log.d("DEBUG", "AccountList: Ping")
            if (accountPair.first.accountType == category) {
                AccountCard(
                    accountWithBalance = accountPair,
                    viewModel = viewModel,
                    navigateToScreen = navigateToScreen
                )
            }
        }
    }

}

@Composable
fun AccountCard(
    accountWithBalance: Pair<Account, Double>,
    modifier: Modifier = Modifier,
    viewModel: AccountViewModel,
    navigateToScreen: (screen: String) -> Unit,
) {
    ElevatedCard(
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(104.dp)
            .padding(0.dp, 12.dp)
            .clickable {
                var accountId = accountWithBalance.first.accountId
                navigateToScreen("AccountDetails/$accountId")
            }
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
                    text = "Rs. " + accountWithBalance.second.toString()
                )
                Text(
                    text = "Rs. " + accountWithBalance.second.toString()
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
