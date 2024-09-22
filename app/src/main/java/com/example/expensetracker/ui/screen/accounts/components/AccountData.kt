package com.example.expensetracker.ui.screen.accounts.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.expensetracker.data.model.Account
import com.example.expensetracker.data.model.AccountTypes
import com.example.expensetracker.data.model.CurrencyFormat
import com.example.expensetracker.ui.common.FormattedCurrency
import com.example.expensetracker.ui.screen.accounts.AccountViewModel
import com.example.expensetracker.ui.screen.accounts.AccountsUiState

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

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .height(104.dp)
            .padding(0.dp, 12.dp)
            .clickable {
                val accountId = accountWithBalance.first.accountId
                navigateToScreen("Account Details/$accountId")
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
                FormattedCurrency(
                    value = accountWithBalance.second,
                    currency = accountCurrencyInfo
                )
                Text(
                    text = accountWithBalance.first.accountName,
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

            }
        }
    }
}