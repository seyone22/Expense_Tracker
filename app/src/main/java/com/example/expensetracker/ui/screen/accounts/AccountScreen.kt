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
import androidx.lifecycle.ViewModel
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
import com.example.expensetracker.ui.screen.accounts.AccountViewModel
import com.example.expensetracker.ui.screen.operations.account.AccountEntryDestination
import com.example.expensetracker.ui.screen.settings.SettingsDestination
import kotlinx.coroutines.CoroutineScope
import kotlin.math.log

object AccountsDestination : NavigationDestination {
    override val route = "Accounts"
    override val titleRes = R.string.app_name
    override val routeId = 0
}

@Composable
fun AccountScreen(
    modifier: Modifier = Modifier
        .padding(16.dp, 12.dp),
    navigateToScreen: (screen: String) -> Unit,
    viewModel: AccountViewModel = viewModel(factory = AppViewModelProvider.Factory),

    ) {
    val accountsUiState by viewModel.accountsUiState.collectAsState()
    val totals by viewModel.totals.collectAsState(Totals())
    Log.d("DEBUG", "AccountScreen: $totals")
    val isUsed by viewModel.isUsed.collectAsState()

    when (isUsed) {
        "FALSE" -> {
            navigateToScreen("Onboarding")
        }

        "TRUE" -> {
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
                                proportions = listOf( (totals.income / totals.total).toFloat() , (totals.expenses / totals.total).toFloat()),
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
                            Text(
                                text = accountsUiState.grandTotal.toString()
                            )

                            Text(
                                text = "Summary of Accounts",
                                style = MaterialTheme.typography.titleLarge
                            )
                        }

                        enumValues<AccountTypes>().forEach { accountType ->
                            if (viewModel.countInType(
                                    accountType,
                                    accountsUiState.accountList
                                ) != 0
                            ) {
                                val displayName: String = accountType.displayName
                                AccountList(
                                    modifier = modifier,
                                    category = displayName,
                                    accountList = accountsUiState.accountList,
                                    viewModel = viewModel,
                                    navigateToScreen = navigateToScreen,
                                )
                            }
                        }
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
    val data by viewModel.data.collectAsState()
    Column(
        modifier = modifier,
    ) {
        Text(
            text = category,
            style = MaterialTheme.typography.titleMedium
        )

        accountList.forEach { accountPair ->
            val balance: Double = data.balancesList.find { it.accountId == accountPair.first.accountId }?.balance ?: 0.0
            if (accountPair.first.accountType == category) {
                AccountCard(
                    accountWithBalance = accountPair,
                    balance = balance,
                    viewModel = viewModel,
                    navigateToScreen = navigateToScreen,
                )
            }
        }
    }

}

@Composable
fun AccountCard(
    accountWithBalance: Pair<Account, Double>,
    modifier: Modifier = Modifier,
    balance: Double,
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
                    text = (accountWithBalance.first.initialBalance?.plus(
                        balance)).toString()
                )
                Text(
                    text = (accountWithBalance.first.initialBalance?.plus(
                        balance)).toString()
                )
            }
        }
    }
}


fun formatAsCurrency(value: Double, format: CurrencyFormat): String {
    return if (format.pfx_symbol.isNotBlank()) {
        format.pfx_symbol + " " + String.format("%.2f", value)
    } else {
        String.format("%.2f", value) + " " + format.sfx_symbol
    }
}