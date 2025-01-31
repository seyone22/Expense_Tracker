package com.seyone22.expensetracker.ui.screen.home.composables

import androidx.compose.foundation.layout.Arrangement
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
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(accountList.size) { index ->
            val accountPair = accountList[index]
            AccountCard(
                accountWithBalance = accountPair,
                navigateToScreen = navigateToScreen,
            )
        }
    }
}
