package com.seyone22.expensetracker.ui.screen.home.allAccounts

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.seyone22.expensetracker.R
import com.seyone22.expensetracker.ui.AppViewModelProvider
import com.seyone22.expensetracker.ui.navigation.NavigationDestination
import com.seyone22.expensetracker.ui.screen.home.HomeViewModel

object AllAccountsDestination : NavigationDestination {
    override val route = "All Accounts"
    override val titleRes = R.string.app_name
    override val routeId = 32
}

@Composable
fun AllAccountScreen(
    modifier: Modifier = Modifier,
    navigateBack: () -> Unit,
    navigateToScreen: (screen: String) -> Unit,
    viewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {

}