package com.seyone22.expensetracker.ui.screen.home.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.seyone22.expensetracker.ui.screen.home.HomeUiState
import com.seyone22.expensetracker.ui.screen.home.allAccounts.AllAccountsDestination

@Composable
fun AccountData(
    modifier: Modifier,
    accountsUiState: HomeUiState,
    navigateToScreen: (screen: String) -> Unit,
) {
    Column(
        modifier = modifier.padding(0.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Portfolio",
                style = MaterialTheme.typography.titleLarge,
            )
            TextButton(
                onClick = {
                    navigateToScreen(AllAccountsDestination.route)
                },
            ) {
                Text(
                    text = "View All"
                )
            }
        }

        AccountList(
            modifier = modifier,
            accountList = accountsUiState.accountList,
            navigateToScreen = navigateToScreen,
        )
    }
}

