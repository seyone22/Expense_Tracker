package com.example.expensetracker.ui.screen.operations.account

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.expensetracker.R
import com.example.expensetracker.ui.AppViewModelProvider
import com.example.expensetracker.ui.navigation.NavigationDestination

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
    LaunchedEffect(backStackEntry) {
        viewModel.getAccount(backStackEntry.toInt())
    }
    val accountDetailAccountUiState by viewModel.accountDetailAccountUiState.collectAsState()

    val x = accountDetailAccountUiState.account.accountName
    Log.d("DEBUG", "AccountDetailScreen: $x")
    Scaffold(
        containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
        ) {
        Column(
            modifier = modifier
                .padding(it)
        ) {
            Button(onClick = { /*TODO*/ }) {
                Text(text = "ALOHA NIGGERS")
            }
        }
    }

}