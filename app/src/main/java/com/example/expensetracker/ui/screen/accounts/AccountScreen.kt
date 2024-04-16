package com.example.expensetracker.ui.screen.accounts

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.ArrowDownward
import androidx.compose.material.icons.outlined.ArrowUpward
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.expensetracker.R
import com.example.expensetracker.model.Account
import com.example.expensetracker.model.AccountTypes
import com.example.expensetracker.model.CurrencyFormat
import com.example.expensetracker.ui.AppViewModelProvider
import com.example.expensetracker.ui.common.FormattedCurrency
import com.example.expensetracker.ui.navigation.NavigationDestination
import com.example.expensetracker.ui.screen.onboarding.OnboardingDestination
import com.github.tehras.charts.piechart.PieChart
import com.github.tehras.charts.piechart.PieChartData
import com.github.tehras.charts.piechart.animation.simpleChartAnimation
import com.github.tehras.charts.piechart.renderer.SimpleSliceDrawer

object AccountsDestination : NavigationDestination {
    override val route = "Accounts"
    override val titleRes = R.string.app_name
    override val routeId = 0
}

@Composable
fun AccountScreen(
    modifier: Modifier = Modifier,
    navigateToScreen: (screen: String) -> Unit,
    viewModel: AccountViewModel = viewModel(factory = AppViewModelProvider.Factory),
    windowSizeClass: WindowWidthSizeClass,
    setTopBarAction: (Int) -> Unit
) {
    val accountsUiState by viewModel.accountsUiState.collectAsState()
    val totals by viewModel.totals.collectAsState(Totals())

    // Code block to get the current currency's detail.
    val baseCurrencyId by viewModel.baseCurrencyId.collectAsState()
    var baseCurrencyInfo by remember { mutableStateOf(CurrencyFormat()) }

    // Use LaunchedEffect to launch the coroutine when the composable is first recomposed
    LaunchedEffect(baseCurrencyId) {
        baseCurrencyInfo =
            viewModel.getBaseCurrencyInfo(baseCurrencyId = baseCurrencyId.toInt())
        setTopBarAction(9)
    }

    if (viewModel.isUsed.collectAsState().value == "FALSE") {
        navigateToScreen(OnboardingDestination.route)
    }

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 320.dp),
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp, 0.dp),
    ) {
        item() {
            Column {
                Log.d("TAG", "AccountScreen: $totals")

                NetWorth(
                    totals = totals,
                    baseCurrencyInfo = baseCurrencyInfo
                )

                Summary(totals = totals, baseCurrencyInfo = baseCurrencyInfo)
            }
        }
        item(
        ) {
            AccountData(
                modifier = modifier,
                viewModel = viewModel,
                accountsUiState = accountsUiState,
                navigateToScreen = navigateToScreen,
            )
        }
    }
}

@Composable
fun NetWorth(
    totals: Totals,
    baseCurrencyInfo: CurrencyFormat
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(224.dp)
            .padding(0.dp, 24.dp, 0.dp, 0.dp)
    ) {
        FormattedCurrency(
            value = totals.total,
            currency = baseCurrencyInfo,
            modifier = Modifier
                .align(Alignment.CenterHorizontally),
            style = MaterialTheme.typography.displayMedium
        )
        Text(
            text = "Net Worth",
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(0.dp, 0.dp, 0.dp, 24.dp),
            fontStyle = FontStyle.Italic,
            style = MaterialTheme.typography.labelLarge,
            color = Color.Gray
        )

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp, 0.dp, 0.dp, 24.dp),
        ) {
            Card(
                modifier = Modifier
                    .padding(0.dp, 0.dp, 0.dp, 0.dp)
                    .width(165.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ArrowDownward,
                        contentDescription = null,
                        tint = Color(0xff50b381),
                        modifier = Modifier.size(36.dp, 36.dp)
                    )
                    FormattedCurrency(
                        value = totals.income,
                        currency = baseCurrencyInfo,
                        modifier = Modifier.align(Alignment.CenterVertically),
                    )
                }
            }
            Card(
                modifier = Modifier
                    .padding(0.dp, 0.dp, 0.dp, 0.dp)
                    .width(165.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ArrowUpward,
                        contentDescription = null,
                        tint = Color(0xfff75e51),
                        modifier = Modifier.size(36.dp, 36.dp)
                    )
                    FormattedCurrency(
                        value = totals.expenses,
                        currency = baseCurrencyInfo,
                        modifier = Modifier.align(Alignment.CenterVertically),
                    )
                }
            }
        }
    }
}

@Composable
fun Summary(totals: Totals, baseCurrencyInfo: CurrencyFormat) {
    Card(
    ) {
        Box(
            modifier = Modifier
                .padding(24.dp)
                .height(240.dp)
                .fillMaxWidth()
        ) {
            // Add legend items
            Column {
                LegendItem("Income", MaterialTheme.colorScheme.primary)
                LegendItem("Expense", Color(0xfff75e51))
            }
            PieChart(
                pieChartData = PieChartData(
                    listOf(
                        PieChartData.Slice(
                            totals.income.toFloat(),
                            MaterialTheme.colorScheme.primary
                        ), PieChartData.Slice(
                            totals.expenses.toFloat(),
                            MaterialTheme.colorScheme.error,
                        )
                    )
                ),
                // Optional properties.
                modifier = Modifier
                    .fillMaxSize()
                    .offset(36.dp),
                animation = simpleChartAnimation(),
                sliceDrawer = SimpleSliceDrawer()
            )
        }
    }
}

@Composable
fun LegendItem(label: String, color: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(color)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = label)
    }
}

@Composable
fun AccountData(
    modifier: Modifier,
    viewModel: AccountViewModel,
    accountsUiState: AccountsUiState,
    navigateToScreen: (screen: String) -> Unit,
) {
    Column(
        modifier = modifier.padding(0.dp, 24.dp, 0.dp, 0.dp),
    ) {
        Text(
            text = "Summary of Accounts",
            style = MaterialTheme.typography.titleLarge,
        )

        enumValues<AccountTypes>().forEach { accountType ->
            if (viewModel.countInType(
                    accountType,
                    accountsUiState.accountList
                ) != 0
            ) {
                AccountList(
                    modifier = modifier,
                    category = accountType.displayName,
                    accountList = accountsUiState.accountList,
                    viewModel = viewModel,
                    navigateToScreen = navigateToScreen,
                )
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
        modifier = modifier.padding(0.dp, 8.dp, 0.dp, 0.dp)
    ) {
        Text(
            text = "$category Accounts",
            style = MaterialTheme.typography.titleLarge,
            color = Color.Gray,
            fontWeight = FontWeight.Bold
        )

        accountList.forEach { accountPair ->
            if (accountPair.first.accountType == category) {
                AccountCard(
                    accountWithBalance = accountPair,
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

    OutlinedCard(
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
                    value = accountWithBalance.second,
                    currency = accountCurrencyInfo
                )
                FormattedCurrency(
                    value = accountWithBalance.second,
                    currency = accountCurrencyInfo
                )
            }
        }
    }
}