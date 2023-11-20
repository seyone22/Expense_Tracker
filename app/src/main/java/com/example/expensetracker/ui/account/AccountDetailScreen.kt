package com.example.expensetracker.ui.account

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.expensetracker.R
import com.example.expensetracker.ui.AppViewModelProvider
import com.example.expensetracker.ui.navigation.NavigationDestination
import com.example.expensetracker.ui.screen.entities.EntityViewModel

object AccountDetailDestination : NavigationDestination {
    override val route = "AccountDetails"
    override val titleRes = R.string.app_name
}
@Composable
fun AccountDetailScreen(
    navigateToEntityEntry: () -> Unit,
    navigateToScreen: (screen: String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AccountDetailViewModel = viewModel(factory = AppViewModelProvider.Factory),
    navController: NavController
) {
    val accountDetailAccountUiState by viewModel.accountDetailAccountUiState.collectAsState()

    Text(text = accountDetailAccountUiState.balance.toString())
}