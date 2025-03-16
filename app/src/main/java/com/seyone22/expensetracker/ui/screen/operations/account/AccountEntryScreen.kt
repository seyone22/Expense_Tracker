package com.seyone22.expensetracker.ui.screen.operations.account

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.seyone22.expensetracker.R
import com.seyone22.expensetracker.ui.AppViewModelProvider
import com.seyone22.expensetracker.ui.navigation.NavigationDestination
import com.seyone22.expensetracker.ui.screen.home.HomeDestination
import com.seyone22.expensetracker.ui.screen.operations.account.composables.AccountEntryForm
import kotlinx.coroutines.launch

object AccountEntryDestination : NavigationDestination {
    override val route = "EnterAccount"
    override val titleRes = R.string.app_name
    override val routeId = 12
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountEntryScreen(
    modifier: Modifier = Modifier,
    navigateBack: () -> Unit = {},
    onNavigateUp: () -> Unit = {},
    canNavigateBack: Boolean = true,
    navigateToScreen: (screen: String) -> Unit,
    viewModel: AccountEntryViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val coroutineScope = rememberCoroutineScope()

    Scaffold(containerColor = MaterialTheme.colorScheme.background, topBar = {
        TopAppBar(colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
        ), title = {
            Text(
                text = "Create Account", style = MaterialTheme.typography.titleLarge
            )
        }, navigationIcon = {
            IconButton(onClick = {
                navigateBack()
            }) {
                Icon(
                    imageVector = Icons.Filled.Close, contentDescription = "Close"
                )
            }
        }, actions = {
            Button(
                onClick = {
                    coroutineScope.launch {
                        viewModel.saveAccount()
                        navigateToScreen(HomeDestination.route)
                    }
                },
                modifier = modifier.padding(0.dp, 0.dp, 8.dp, 0.dp),
                enabled = viewModel.accountUiState.isEntryValid
            ) {
                Text(text = "Create")
            }
        })

    }

    ) { padding ->
        LazyColumn(
            modifier = modifier
                .fillMaxWidth()
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            item {
                AccountEntryForm(
                    accountDetails = viewModel.accountUiState.accountDetails,
                    onValueChange = viewModel::updateUiState,
                    modifier = Modifier,
                )
            }
        }
    }

}