package com.example.expensetracker.ui.screen.home.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.expensetracker.SharedViewModel
import com.example.expensetracker.data.model.Account
import com.example.expensetracker.data.model.CurrencyFormat
import com.example.expensetracker.ui.AppViewModelProvider
import com.example.expensetracker.ui.common.FormattedCurrency

@Composable
fun AccountCard(
    accountWithBalance: Pair<Account, Double>,
    modifier: Modifier = Modifier,
    navigateToScreen: (screen: String) -> Unit,
) {
    // Code block to get the current currency's detail.
    val sharedViewModel: SharedViewModel = viewModel(factory = AppViewModelProvider.Factory)
    val baseCurrency by sharedViewModel.baseCurrencyFlow.collectAsState(initial = CurrencyFormat())

    Card(
        colors = CardDefaults.cardColors().copy(containerColor = MaterialTheme.colorScheme.primaryContainer),
        modifier = Modifier
            .width(260.dp)
            .height(160.dp)
            .clickable {
                val accountId = accountWithBalance.first.accountId
                navigateToScreen("Account Details/$accountId")
            }
    ) {
        Box(
            modifier = Modifier.padding(16.dp).fillMaxSize()
        ) {
            Column(
                modifier = Modifier.align(Alignment.TopStart)
            ) {
                Text(
                    text = accountWithBalance.first.accountName,
                    style = MaterialTheme.typography.labelLarge
                )
                FormattedCurrency(
                    value = accountWithBalance.second,
                    currency = baseCurrency ?: CurrencyFormat(),
                    style = MaterialTheme.typography.headlineLarge
                )
            }

            Column(
                modifier = Modifier.align(Alignment.BottomStart)
            ) {
                Text(
                    text = accountWithBalance.first.accountNum.toString(),
                    style = MaterialTheme.typography.labelLarge
                )
                Text(
                    text = accountWithBalance.first.heldAt ?: "",
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}