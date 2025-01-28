package com.example.expensetracker.ui.screen.transactions

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.expensetracker.R
import com.example.expensetracker.SelectedObjects
import com.example.expensetracker.ui.AppViewModelProvider
import com.example.expensetracker.ui.navigation.NavigationDestination
import com.example.expensetracker.ui.screen.transactions.composables.ScheduledTransactionList
import com.example.expensetracker.ui.screen.transactions.composables.TransactionList
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
    setTopBarAction: (Int) -> Unit,
    setIsItemSelected: (Boolean) -> Unit,
    setSelectedObject: (SelectedObjects) -> Unit,
    viewModel: TransactionsViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val transactionsUiState by viewModel.transactionsUiState.collectAsState()

    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    var state by remember { mutableIntStateOf(0) }
    setTopBarAction(8)

    val titles = listOf("Transactions", "Scheduled")

    val pagerState = rememberPagerState(pageCount = { 2 })

    Column(modifier = modifier.fillMaxSize()) {
        PrimaryTabRow(
            selectedTabIndex = state,
            containerColor = MaterialTheme.colorScheme.background,
        ) {
            titles.forEachIndexed { index, title ->
                Tab(
                    selected = state == index,
                    onClick = {
                        state = index
                        setTopBarAction(state)
                        coroutineScope.launch { pagerState.animateScrollToPage(index) }
                    },
                    text = {
                        Text(
                            text = title,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                )
            }
        }
        HorizontalPager(
            state = pagerState,
            verticalAlignment = Alignment.Top,
            modifier = Modifier.fillMaxHeight()

        ) { page ->
            when (page) {
                0 -> {
                    state = pagerState.currentPage
                    TransactionList(
                        transactions = transactionsUiState.transactions,
                        longClicked = { selected ->
                            setIsItemSelected(true)
                            val selObj = SelectedObjects(transaction = selected)
                            setSelectedObject(selObj)
                        },
                        viewModel = viewModel,
                        showFilter = true
                    )
                }

                1 -> {
                    state = pagerState.currentPage
                    ScheduledTransactionList(
                        billsDeposits = transactionsUiState.billsDeposits,
                        longClicked = { selected ->
                            setIsItemSelected(true)
                            val selObj = SelectedObjects(billsDeposits = selected)
                            setSelectedObject(selObj)
                        },
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}