package com.seyone22.expensetracker.ui.screen.home.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.CompareArrows
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CurrencyYen
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.seyone22.expensetracker.ui.screen.operations.account.AccountEntryDestination
import com.seyone22.expensetracker.ui.screen.operations.transaction.TransactionEntryDestination

@Composable
fun QuickActions(modifier: Modifier, navigateToScreen: (screen: String) -> Unit) {
    val actions = listOf(
        Triple("Deposit", Icons.Filled.ArrowDownward, TransactionEntryDestination),
        Triple("Withdraw", Icons.Filled.ArrowUpward, TransactionEntryDestination),
        Triple(
            "Transfer", Icons.AutoMirrored.Filled.CompareArrows, TransactionEntryDestination
        ),
        Triple("Account", Icons.Filled.AccountBalanceWallet, AccountEntryDestination),
        Triple("Payee", Icons.Filled.Person, TransactionEntryDestination),
        Triple("Category", Icons.Filled.Bookmark, TransactionEntryDestination),
        Triple("Tag", Icons.Filled.Tag, TransactionEntryDestination),
        Triple("Currency", Icons.Filled.CurrencyYen, TransactionEntryDestination)
    )

    LazyRow(
        modifier = modifier.padding(top = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(actions.size) { index ->
            Card(modifier = Modifier
                .size(100.dp)
                .clickable { }) {
                Box(
                    contentAlignment = Alignment.Center, // Centers content inside the Box
                    modifier = Modifier.fillMaxSize() // Ensures the Box takes up the full Card size
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally, // Centers content horizontally
                        verticalArrangement = Arrangement.Center // Centers content vertically
                    ) {
                        Icon(
                            actions[index].second,
                            contentDescription = actions[index].first,
                            modifier = Modifier
                                .size(36.dp)
                        )
                        Text(
                            text = actions[index].first,
                            modifier = Modifier.padding(8.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}
