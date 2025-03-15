package com.seyone22.expensetracker.ui.screen.operations.account

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.seyone22.expensetracker.R
import com.seyone22.expensetracker.ui.AppViewModelProvider
import com.seyone22.expensetracker.ui.common.ExpenseTopBar
import com.seyone22.expensetracker.ui.navigation.NavigationDestination
import com.seyone22.expensetracker.ui.screen.operations.account.composables.AccountDetailCard
import com.seyone22.expensetracker.ui.screen.operations.account.composables.AccountHistoryGraph
import com.seyone22.expensetracker.ui.screen.transactions.composables.TransactionList
import kotlinx.coroutines.CoroutineScope

object AccountDetailDestination : NavigationDestination {
    override val route = "Account Details"
    override val titleRes = R.string.app_name
    override val routeId = 13
}

@Composable
fun AccountDetailScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    backStackEntry: String,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    viewModel: AccountDetailViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    // Viewmodel and UI States
    val accountDetailUiState by viewModel.accountDetailUiState.collectAsState()

    LaunchedEffect(Unit, accountDetailUiState.account.currencyId) {
        viewModel.setAccountId(backStackEntry.toInt())
    }
    Scaffold(topBar = {
        ExpenseTopBar(
            selectedActivity = AccountDetailDestination.route,
            navController = navController,
            hasNavigation = true,
            dropdownOptions = listOf(
                "Edit" to { navController.navigate("Edit Account") },
                "Delete" to { navController.navigate("Delete Account") },
                "Make Favourite" to { }
            )
        )
    }) {
        Column(
            modifier = modifier
                .padding(it)
                .padding(16.dp, 0.dp)
        ) {
            AccountHistoryGraph(
                modifier = modifier,
                accountDetailUiState = accountDetailUiState,
            )

            AccountDetailCard(
                modifier = modifier,
                accountDetailUiState = accountDetailUiState,
            )

            Spacer(Modifier.height(32.dp))

            // Title text for the transactions area
            Text(
                text = "Recent Transactions",
                style = MaterialTheme.typography.headlineSmall
            )

            // List of transactions for this account
            TransactionList(
                modifier = modifier,
                showFilter = false,
                forAccountId = backStackEntry.toInt()
            )
        }
    }
}