package com.example.expensetracker.ui.screen.operations.account

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.expensetracker.R
import com.example.expensetracker.ui.AppViewModelProvider
import com.example.expensetracker.ui.common.ExpenseNavBar
import com.example.expensetracker.ui.common.ExpenseTopBar
import com.example.expensetracker.ui.navigation.NavigationDestination
import com.example.expensetracker.ui.screen.accounts.AccountsDestination
import com.example.expensetracker.ui.screen.settings.SettingsDestination
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

object AccountDetailDestination : NavigationDestination {
    override val route = "AccountDetails"
    override val titleRes = R.string.app_name
    override val routeId = 13
}

@Composable
fun AccountDetailScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    backStackEntry: String,
    viewModel: AccountDetailViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    viewModel.accountId = backStackEntry.toInt()

    val accountDetailAccountUiState by viewModel.accountDetailAccountUiState.collectAsState()
    val accountDetailTransactionUiState by viewModel.accountDetailTransactionUiState.collectAsState()

    LaunchedEffect( Unit ) {
        viewModel.getTransactions()
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
        topBar = {
            ExpenseTopBar(
                selectedActivity = AccountsDestination.routeId,
                navBarAction = { navController.navigate(AccountEntryDestination.route) },
                navigateToSettings = { navController.navigate(SettingsDestination.route) }
            )
        }
        ) {
        Column(
            modifier = modifier
                .padding(it)
                .padding(0.dp, 100.dp)
        ) {
            if (!accountDetailTransactionUiState.transactions.isNullOrEmpty()) {
                Card(

                ) {
                    Text(text = accountDetailTransactionUiState.transactions.toString())
                }
            }
        }
    }

}