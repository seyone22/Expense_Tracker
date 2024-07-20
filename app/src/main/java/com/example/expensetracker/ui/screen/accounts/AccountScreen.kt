package com.example.expensetracker.ui.screen.accounts

import android.util.Log
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.expensetracker.R
import com.example.expensetracker.data.model.CurrencyFormat
import com.example.expensetracker.ui.AppViewModelProvider
import com.example.expensetracker.ui.navigation.NavigationDestination
import com.example.expensetracker.ui.screen.accounts.components.AccountData
import com.example.expensetracker.ui.screen.accounts.components.NetWorth
import com.example.expensetracker.ui.screen.accounts.components.Summary
import com.example.expensetracker.ui.screen.onboarding.OnboardingDestination

object AccountsDestination : NavigationDestination {
    override val route = "Accounts"
    override val titleRes = R.string.app_name
    override val routeId = 0
}

@Composable
fun AccountScreen(
    modifier: Modifier = Modifier,
    navigateToScreen: (screen: String) -> Unit,
    viewModel: AccountViewModel = viewModel(factory = AppViewModelProvider.Factory),
    windowSizeClass: WindowWidthSizeClass,
    setTopBarAction: (Int) -> Unit
) {
    var offset by remember { mutableStateOf(0f) }

    val accountsUiState by viewModel.accountsUiState.collectAsState()
    val totals by viewModel.totals.collectAsState(Totals())

    // Code block to get the current currency's detail.
    val baseCurrencyId by viewModel.baseCurrencyId.collectAsState()
    var baseCurrencyInfo by remember { mutableStateOf(CurrencyFormat()) }

    // Use LaunchedEffect to launch the coroutine when the composable is first recomposed
    LaunchedEffect(baseCurrencyId) {
        baseCurrencyInfo =
            viewModel.getBaseCurrencyInfo(baseCurrencyId = baseCurrencyId.toInt())
        setTopBarAction(9)
    }

    if (viewModel.isUsed.collectAsState().value == "FALSE") {
        navigateToScreen(OnboardingDestination.route)
    }

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 320.dp),
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp, 0.dp)
            .scrollable(
                orientation = Orientation.Vertical,
                state = rememberScrollableState { delta ->
                    offset += delta
                    delta
                }
            ),
    ) {
        item() {
            Column {
                Log.d("TAG", "AccountScreen: $totals")

                NetWorth(
                    totals = totals,
                    baseCurrencyInfo = baseCurrencyInfo
                )

                Summary(totals = totals, baseCurrencyInfo = baseCurrencyInfo)
            }
        }
        item(
        ) {
            AccountData(
                modifier = modifier,
                viewModel = viewModel,
                accountsUiState = accountsUiState,
                navigateToScreen = navigateToScreen,
            )
        }
    }
}