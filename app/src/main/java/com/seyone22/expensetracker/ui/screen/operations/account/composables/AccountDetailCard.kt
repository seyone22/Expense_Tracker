package com.seyone22.expensetracker.ui.screen.operations.account.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.seyone22.expensetracker.SharedViewModel
import com.seyone22.expensetracker.data.model.CurrencyFormat
import com.seyone22.expensetracker.ui.AppViewModelProvider
import com.seyone22.expensetracker.ui.common.FormattedCurrency
import com.seyone22.expensetracker.ui.screen.operations.account.AccountDetailUiState

@Composable
fun AccountDetailCard(
    modifier: Modifier,
    accountDetailUiState: AccountDetailUiState,
) {
    val sharedViewModel: SharedViewModel = viewModel(factory = AppViewModelProvider.Factory)
    var expanded by remember { mutableStateOf(false) }

    // Get Currency data for the account
    var currencyData by remember { mutableStateOf(CurrencyFormat()) }

    LaunchedEffect(Unit, accountDetailUiState.account.currencyId) {
        currencyData = sharedViewModel.getCurrencyById(accountDetailUiState.account.currencyId)
            ?: CurrencyFormat()
    }

    Card(
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = modifier.padding(16.dp, 8.dp, 0.dp, 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(
                modifier = Modifier.weight(9f),
            ) {
                FormattedCurrency(
                    modifier = Modifier,
                    style = MaterialTheme.typography.displaySmall,
                    value = accountDetailUiState.balance,
                    currency = currencyData,
                    defaultColor = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = accountDetailUiState.account.accountName,
                    style = MaterialTheme.typography.headlineSmall
                )
                Text(
                    text = accountDetailUiState.account.accountType + " Account",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}