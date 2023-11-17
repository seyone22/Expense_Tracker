package com.example.expensetracker.ui.transaction

import androidx.compose.runtime.Composable
import com.example.expensetracker.R
import com.example.expensetracker.ui.navigation.NavigationDestination

object TransactionEntryDestination : NavigationDestination {
    override val route = "EnterTransaction"
    override val titleRes = R.string.app_name
}
@Composable
fun TransactionEntryScreen() {

}