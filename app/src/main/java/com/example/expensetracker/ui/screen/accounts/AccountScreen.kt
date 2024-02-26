package com.example.expensetracker.ui.screen.accounts

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.expensetracker.R
import com.example.expensetracker.model.Account
import com.example.expensetracker.model.AccountTypes
import com.example.expensetracker.model.CurrencyFormat
import com.example.expensetracker.ui.AppViewModelProvider
import com.example.expensetracker.ui.common.DonutChart
import com.example.expensetracker.ui.common.DonutChartData
import com.example.expensetracker.ui.common.DonutChartDataCollection
import com.example.expensetracker.ui.common.FormattedCurrency
import com.example.expensetracker.ui.navigation.NavigationDestination
import com.example.expensetracker.ui.utils.ExpenseNavigationType

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
    navigationType: ExpenseNavigationType,
    setTopBarAction: (Int) -> Unit
) {
    val accountsUiState by viewModel.accountsUiState.collectAsState()
    val totals by viewModel.totals.collectAsState(Totals())

    LazyColumn() {
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
            ) {
                // Code block to get the current currency's detail.
                val baseCurrencyId by viewModel.baseCurrencyId.collectAsState()
                var baseCurrencyInfo by remember { mutableStateOf(CurrencyFormat()) }
                // Use LaunchedEffect to launch the coroutine when the composable is first recomposed
                LaunchedEffect(baseCurrencyId) {
                    baseCurrencyInfo =
                        viewModel.getBaseCurrencyInfo(baseCurrencyId = baseCurrencyId.toInt())

                    setTopBarAction(9)
                }
                DonutChart(
                    data = DonutChartDataCollection(
                        listOf(
                            DonutChartData(
                                totals.income.toFloat(),
                                MaterialTheme.colorScheme.primary,
                                "Income"
                            ),
                            DonutChartData(
                                totals.expenses.toFloat(),
                                MaterialTheme.colorScheme.error,
                                "Expense"
                            )
                        )
                    )
                ) { selected ->
                    AnimatedContent(targetState = selected, label = "") {
                        if (it != null) {
                            Column(modifier = Modifier.width(100.dp)) {
                                Text(
                                    text = it.title ?: "",
                                    textAlign = TextAlign.Center
                                )
                                FormattedCurrency(
                                    value = (it.amount ?: 0).toDouble(),
                                    currency = baseCurrencyInfo
                                )
                            }
                        }
                    }
                }
            }

            Column(
                modifier = modifier,
            ) {
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
            val balance: Double =
                data.balancesList.find { it.accountId == accountPair.first.accountId }?.balance
                    ?: 0.0
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
    // Code block to get the current currency's detail.
    var accountCurrencyInfo by remember { mutableStateOf(CurrencyFormat()) }
    // Use LaunchedEffect to launch the coroutine when the composable is first recomposed
    LaunchedEffect(accountWithBalance.first.currencyId) {
        accountCurrencyInfo =
            viewModel.getBaseCurrencyInfo(baseCurrencyId = accountWithBalance.first.currencyId)
    }

    ElevatedCard(
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(104.dp)
            .padding(0.dp, 12.dp)
            .clickable {
                val accountId = accountWithBalance.first.accountId
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
                FormattedCurrency(
                    value = accountWithBalance.first.initialBalance?.plus(balance)!!,
                    currency = accountCurrencyInfo
                )
                FormattedCurrency(
                    value = accountWithBalance.first.initialBalance?.plus(balance)!!,
                    currency = accountCurrencyInfo
                )
            }
        }
    }
}