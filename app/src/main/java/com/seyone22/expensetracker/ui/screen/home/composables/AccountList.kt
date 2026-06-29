package com.seyone22.expensetracker.ui.screen.home.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.seyone22.expensetracker.data.model.Account

@Composable
fun AccountList(
    modifier: Modifier = Modifier,
    accountList: List<Pair<Account, Double>>,
    navigateToScreen: (screen: String) -> Unit,
    hideBalances: Boolean = false
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Spacer(modifier = Modifier) }
        items(accountList.size) { index ->
            val accountPair = accountList[index]
            AccountCard(
                accountWithBalance = accountPair,
                navigateToScreen = navigateToScreen,
                hideBalances = hideBalances
            )
        }
        item { Spacer(modifier = Modifier) }
    }
}
