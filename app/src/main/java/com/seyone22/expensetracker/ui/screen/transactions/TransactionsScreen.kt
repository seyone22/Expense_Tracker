package com.seyone22.expensetracker.ui.screen.transactions

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.seyone22.expensetracker.R
import com.seyone22.expensetracker.SelectedObjects
import com.seyone22.expensetracker.ui.AppViewModelProvider
import com.seyone22.expensetracker.ui.common.ExpenseTopBar
import com.seyone22.expensetracker.ui.navigation.NavigationDestination
import com.seyone22.expensetracker.ui.screen.entities.EntitiesDestination
import com.seyone22.expensetracker.ui.screen.operations.transaction.TransactionEntryViewModel
import com.seyone22.expensetracker.ui.screen.transactions.composables.ScheduledTransactionList
import com.seyone22.expensetracker.ui.screen.transactions.composables.TransactionList
import kotlinx.coroutines.launch

object TransactionsDestination : NavigationDestination {
    override val route = "Entries"
    override val titleRes = R.string.app_name
    override val routeId = 3
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionsScreen(
    modifier: Modifier = Modifier,
    navigateToScreen: (screen: String) -> Unit,
    viewModel: TransactionsViewModel = viewModel(factory = AppViewModelProvider.Factory),
    navController: NavHostController,
) {
    val transactionsUiState by viewModel.transactionsUiState.collectAsState()

    val coroutineScope = rememberCoroutineScope()

    var state by remember { mutableIntStateOf(0) }

    val titles = listOf("Transactions", "Scheduled")

    val pagerState = rememberPagerState(pageCount = { 2 })
    Scaffold(topBar = {
        ExpenseTopBar(
            selectedActivity = EntitiesDestination.route,
            type = "Left",
            navController = navController,
            hasNavigation = false
        )
    }) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues = it)
        ) {
            PrimaryTabRow(
                selectedTabIndex = state,
                containerColor = MaterialTheme.colorScheme.background,
            ) {
                titles.forEachIndexed { index, title ->
                    Tab(selected = state == index, onClick = {
                        state = index
                        coroutineScope.launch { pagerState.animateScrollToPage(index) }
                    }, text = {
                        Text(
                            text = title, maxLines = 2, overflow = TextOverflow.Ellipsis
                        )
                    })
                }
            }
            HorizontalPager(
                state = pagerState,
                verticalAlignment = Alignment.Top,
                modifier = Modifier.fillMaxHeight()

            ) { page ->
                when (page) {
                    0 -> {
                        val entryViewModel: TransactionEntryViewModel =
                            viewModel(factory = AppViewModelProvider.Factory)
                        val transactionUiState by entryViewModel.transactionUiState.collectAsState()

                        state = pagerState.currentPage
                        TransactionList(
                            useLazyColumn = true,
                            transactions = transactionsUiState.transactions,
                            viewModel = viewModel,
                            showFilter = true
                        )
                    }

                    1 -> {
                        state = pagerState.currentPage
                        ScheduledTransactionList(
                            billsDeposits = transactionsUiState.billsDeposits,
                            longClicked = { selected ->
                                val selObj = SelectedObjects(billsDeposits = selected)
                            },
                            viewModel = viewModel
                        )
                    }
                }
            }
        }
    }
}