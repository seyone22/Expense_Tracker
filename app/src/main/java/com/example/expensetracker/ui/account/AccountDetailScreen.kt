package com.example.expensetracker.ui.account

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
    viewModel: EntityViewModel = viewModel(factory = AppViewModelProvider.Factory),
    navController: NavController
) {
    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val variableReceived = navBackStackEntry.value?.arguments?.getInt("accountId") ?: -1

    Text(text = variableReceived.toString())
}