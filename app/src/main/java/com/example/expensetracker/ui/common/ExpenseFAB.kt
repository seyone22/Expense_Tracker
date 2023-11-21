package com.example.expensetracker.ui.common

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.Balance
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.House
import androidx.compose.material.icons.outlined.TextSnippet
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.expensetracker.R
import com.example.expensetracker.ui.screen.accounts.AccountsDestination
import com.example.expensetracker.ui.transaction.TransactionEntryScreen

@Composable
fun ExpenseFAB(
    navigateToScreen: (screen: String) -> Unit,
) {
    FloatingActionButton(onClick = {
        navigateToScreen("TransactionEntry")
    }) {
        Icon(Icons.Outlined.Edit, "Add")
    }
}