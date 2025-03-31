package com.seyone22.expensetracker.ui.screen.home

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.seyone22.expensetracker.R
import com.seyone22.expensetracker.SharedViewModel
import com.seyone22.expensetracker.data.model.CurrencyFormat
import com.seyone22.expensetracker.ui.AppViewModelProvider
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
    override val icon = Icons.Outlined.Home
}

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    navigateToScreen: (screen: String) -> Unit,
    viewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory),
    sharedViewModel: SharedViewModel = viewModel(factory = AppViewModelProvider.Factory),
    windowSizeClass: WindowWidthSizeClass,
) {
    val accountsUiState by viewModel.accountsUiState.collectAsState()
    val totals by viewModel.getFilteredTotal("All").collectAsState(initial = Totals())

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        sharedViewModel.nukeAllWorkManagers(context = context)
    }

    val baseCurrency by sharedViewModel.baseCurrencyFlow.collectAsState(initial = CurrencyFormat())
    val isUsed by sharedViewModel.isUsedFlow.collectAsState(initial = true)

    val currentDialog by viewModel.currentDialog
    currentDialog?.let {
        GenericDialog(dialogAction = it, onDismiss = { viewModel.dismissDialog() })
    }

    if (!isUsed) {
        navigateToScreen(OnboardingDestination.route)
    }

    // Define item list and reorder based on windowSizeClass
    val itemList = when (windowSizeClass) {
        WindowWidthSizeClass.Compact -> listOf(
            "NetWorth", "MySpending", "QuickActions", "AccountData", "TransactionData"
        )

        else -> listOf(
            "NetWorth", "AccountData", "MySpending", "TransactionData", "QuickActions"
        )
    }

    // Map keys to composables
    val itemMap: Map<String, @Composable () -> Unit> = mapOf(
        "NetWorth" to {
            NetWorth(
                modifier = Modifier.padding(16.dp, 0.dp),
                totals = totals,
                baseCurrencyInfo = baseCurrency ?: CurrencyFormat(),
            )
        },
        "MySpending" to {
            MySpending(
                modifier = Modifier.padding(16.dp, 0.dp),
                baseCurrencyInfo = baseCurrency ?: CurrencyFormat(),
                viewModel = viewModel
            )
        },
        "QuickActions" to {
            QuickActions(
                modifier = modifier.padding(0.dp),
                navigateToScreen = navigateToScreen,
                viewModel = viewModel
            )
        },
        "AccountData" to {
            AccountData(
                modifier = modifier.padding(0.dp, 16.dp),
                accountsUiState = accountsUiState,
                navigateToScreen = navigateToScreen,
            )
        },
        "TransactionData" to {
            TransactionData(
                modifier = modifier.padding(16.dp, 0.dp),
                accountsUiState = accountsUiState,
                navigateToScreen = navigateToScreen,
            )
        }
    )

    LazyVerticalGrid(
        columns = GridCells.Adaptive(600.dp),
        userScrollEnabled = true,
        modifier = modifier
            .windowInsetsPadding(insets = WindowInsets.statusBars)
    ) {
        itemList.forEach { key ->
            item { itemMap[key]?.invoke() }
        }
    }
}
