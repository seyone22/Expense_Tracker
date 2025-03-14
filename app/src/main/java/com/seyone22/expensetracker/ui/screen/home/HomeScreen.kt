package com.seyone22.expensetracker.ui.screen.home

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Scaffold
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.seyone22.expensetracker.R
import com.seyone22.expensetracker.SharedViewModel
import com.seyone22.expensetracker.data.model.CurrencyFormat
import com.seyone22.expensetracker.ui.AppViewModelProvider
import com.seyone22.expensetracker.ui.common.ExpenseNavBar
import com.seyone22.expensetracker.ui.common.dialogs.GenericDialog
import com.seyone22.expensetracker.ui.navigation.NavigationDestination
import com.seyone22.expensetracker.ui.screen.home.composables.AccountData
import com.seyone22.expensetracker.ui.screen.home.composables.MySpending
import com.seyone22.expensetracker.ui.screen.home.composables.NetWorth
import com.seyone22.expensetracker.ui.screen.home.composables.QuickActions
import com.seyone22.expensetracker.ui.screen.home.composables.TransactionData
import com.seyone22.expensetracker.ui.screen.onboarding.OnboardingDestination

object HomeDestination : NavigationDestination {
    override val route = "Home"
    override val titleRes = R.string.app_name
    override val routeId = 0
}

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    navigateToScreen: (screen: String) -> Unit,
    viewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory),
    windowSizeClass: WindowWidthSizeClass,
) {
    var offset by remember { mutableFloatStateOf(0f) }

    val accountsUiState by viewModel.accountsUiState.collectAsState()

    // Collect the filtered totals (you can pass "All" or "Current Month" as the filter)
    val totals by viewModel.getFilteredTotal("All").collectAsState(initial = Totals())

    LaunchedEffect(Unit) {
        viewModel.fetchTransactionsForWeek()
    }

    // Code block to get the current currency's detail.
    val sharedViewModel: SharedViewModel = viewModel(factory = AppViewModelProvider.Factory)
    val baseCurrency by sharedViewModel.baseCurrencyFlow.collectAsState(initial = CurrencyFormat())
    val isUsed by sharedViewModel.isUsedFlow.collectAsState(initial = true)

    val currentDialog by viewModel.currentDialog

    currentDialog?.let {
        GenericDialog(dialogAction = it, onDismiss = { viewModel.dismissDialog() })
    }

    if (!isUsed) {
        navigateToScreen(OnboardingDestination.route)
    }
    Scaffold(bottomBar = {
        ExpenseNavBar(
            currentActivity = HomeDestination.route, navigateToScreen = navigateToScreen
        )
    }) {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 320.dp),
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues = it)
                .scrollable(
                    orientation = Orientation.Vertical,
                    state = rememberScrollableState { delta ->
                        offset += delta
                        delta
                    }),
        ) {
            item {
                NetWorth(
                    modifier = Modifier.padding(16.dp, 0.dp),
                    totals = totals,
                    baseCurrencyInfo = baseCurrency ?: CurrencyFormat(),
                )
            }
            item {
                MySpending(
                    modifier = Modifier.padding(16.dp, 0.dp),
                    expensesByWeek = accountsUiState.expensesByWeek,
                    baseCurrencyInfo = baseCurrency ?: CurrencyFormat(),

                    )
            }
            item {
                QuickActions(
                    modifier = modifier.padding(0.dp),
                    navigateToScreen = navigateToScreen,
                    viewModel = viewModel
                )
            }
            item {
                AccountData(
                    modifier = modifier.padding(0.dp),
                    accountsUiState = accountsUiState,
                    navigateToScreen = navigateToScreen,
                )
            }
            item {
                TransactionData(
                    modifier = modifier.padding(16.dp, 0.dp),
                    accountsUiState = accountsUiState,
                    navigateToScreen = navigateToScreen,
                )
            }
        }
    }
}

