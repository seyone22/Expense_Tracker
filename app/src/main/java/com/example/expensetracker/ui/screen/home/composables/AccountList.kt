package com.example.expensetracker.ui.screen.home.composables

import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.expensetracker.data.model.Account

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
