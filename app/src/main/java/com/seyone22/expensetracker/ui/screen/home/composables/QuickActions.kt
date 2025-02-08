package com.seyone22.expensetracker.ui.screen.home.composables

import androidx.compose.foundation.background
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.seyone22.expensetracker.ui.navigation.NavigationDestination
import com.seyone22.expensetracker.ui.screen.home.HomeViewModel
import com.seyone22.expensetracker.ui.screen.operations.account.AccountEntryDestination
import com.seyone22.expensetracker.ui.screen.operations.transaction.TransactionEntryDestination
import kotlinx.coroutines.CoroutineScope

data class QuickAction(
    val title: String,
    val icon: ImageVector,
    val destination: NavigationDestination,
    val color: Color
)

@Composable
fun QuickActions(
    modifier: Modifier,
    navigateToScreen: (screen: String) -> Unit,
    viewModel: HomeViewModel,
    coroutineScope: CoroutineScope = rememberCoroutineScope()
) {
    val actions = listOf(
        QuickAction(
            "Deposit",
            Icons.Filled.ArrowDownward,
            TransactionEntryDestination,
            MaterialTheme.colorScheme.primaryContainer
        ),
        QuickAction(
            "Withdraw",
            Icons.Filled.ArrowUpward,
            TransactionEntryDestination,
            MaterialTheme.colorScheme.primaryContainer
        ),
        QuickAction(
            "Transfer",
            Icons.AutoMirrored.Filled.CompareArrows,
            TransactionEntryDestination,
            MaterialTheme.colorScheme.primaryContainer
        ),
        QuickAction(
            "Account",
            Icons.Filled.AccountBalanceWallet,
            AccountEntryDestination,
            MaterialTheme.colorScheme.tertiaryContainer
        ),
        QuickAction(
            "Payee",
            Icons.Filled.Person,
            TransactionEntryDestination,
            MaterialTheme.colorScheme.inversePrimary
        ),
        QuickAction(
            "Category",
            Icons.Filled.Bookmark,
            TransactionEntryDestination,
            MaterialTheme.colorScheme.inversePrimary
        ),
        QuickAction(
            "Tag",
            Icons.Filled.Tag,
            TransactionEntryDestination,
            MaterialTheme.colorScheme.inversePrimary
        ),
        QuickAction(
            "Currency",
            Icons.Filled.CurrencyYen,
            TransactionEntryDestination,
            MaterialTheme.colorScheme.inversePrimary
        )
    )

    LazyRow(
        modifier = modifier.padding(top = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(actions.size) { index ->
            Card(modifier = Modifier
                .size(100.dp)
                .clickable {
                    if (actions[index].title == "Deposit" || actions[index].title == "Withdraw" || actions[index].title == "Transfer" || actions[index].title == "Account") {
                        navigateToScreen(actions[index].destination.route)
                    } else {
                        /*                        viewModel.showDialog(
                                                    AddEditBudgetYearDialogAction(
                                                        onAdd = { year, month, baseBudget ->
                                                            // month is nullable
                                                            coroutineScope.launch {

                                                            }

                                                        }, availableBudgets = null`
                                                    )
                                                )*/
                    }
                }) {
                Box(
                    contentAlignment = Alignment.Center, // Centers content inside the Box
                    modifier = Modifier
                        .fillMaxSize()
                        .background(actions[index].color) // Ensures the Box takes up the full Card size
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally, // Centers content horizontally
                        verticalArrangement = Arrangement.Center // Centers content vertically
                    ) {
                        Icon(
                            actions[index].icon,
                            contentDescription = actions[index].title,
                            modifier = Modifier.size(36.dp)
                        )
                        Text(
                            text = actions[index].title,
                            modifier = Modifier.padding(8.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}
