package com.seyone22.expensetracker.ui.screen.entities

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.seyone22.expensetracker.R
import com.seyone22.expensetracker.SharedViewModel
import com.seyone22.expensetracker.data.model.CurrencyFormat
import com.seyone22.expensetracker.ui.AppViewModelProvider
import com.seyone22.expensetracker.ui.common.ExpenseTopBar
import com.seyone22.expensetracker.ui.common.FormattedCurrency
import com.seyone22.expensetracker.ui.common.ProfileAvatarWithFallback
import com.seyone22.expensetracker.ui.navigation.NavigationDestination
import com.seyone22.expensetracker.ui.screen.entities.composables.CategoryList
import com.seyone22.expensetracker.ui.screen.entities.composables.CurrenciesList
import com.seyone22.expensetracker.ui.screen.entities.composables.PayeeList
import kotlinx.coroutines.launch
import java.time.LocalDate

object EntitiesDestination : NavigationDestination {
    override val route = "Entities"
    override val titleRes = R.string.app_name
    override val routeId = 1
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EntityScreen(
    modifier: Modifier = Modifier,
    navigateToScreen: (screen: String) -> Unit,
    viewModel: EntityViewModel = viewModel(factory = AppViewModelProvider.Factory),
    navController: NavHostController,
) {
    val coroutineScope = rememberCoroutineScope()
    val sharedViewModel: SharedViewModel = viewModel(factory = AppViewModelProvider.Factory)

    var chartData: Map<LocalDate, Float> by remember { mutableStateOf(mapOf(LocalDate.parse("2022-01-01") to 2f)) }
    var finished: Boolean by remember { mutableStateOf(false) }

    var state by remember { mutableIntStateOf(0) }

    val titles = listOf("Categories", "Payees", "Currencies")
    val entityUiState: EntitiesUiState by viewModel.entitiesUiState.collectAsState(EntitiesUiState())

    // separate state
    val activeCurrencies by viewModel.activeCurrenciesFlow.collectAsState(emptyList())

    val pagerState = rememberPagerState(pageCount = { 3 })
    Scaffold(
        topBar = {
            ExpenseTopBar(
                selectedActivity = EntitiesDestination.route,
                type = "Left",
                navController = navController,
                hasNavigation = false
            )
        }
    ) {
        Column(modifier = modifier
            .fillMaxSize()
            .padding(paddingValues = it)) {
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
                    state = pagerState.currentPage
                    CategoryList(
                        listParent = entityUiState.categoriesParent,
                        listSub = entityUiState.categoriesSub,
                        viewModel = viewModel,
                        coroutineScope = coroutineScope,
                        longClicked = { selected ->
                        },
                    )
                }

                1 -> {
                    state = pagerState.currentPage
                    PayeeList(
                        list = entityUiState.payeesList.sortedBy { p -> p.payeeName[0].lowercaseChar() },
                        viewModel = viewModel,
                        coroutineScope = coroutineScope,
                        longClicked = { selected ->
                        },
                    )
                }

                2 -> {
                    state = pagerState.currentPage

                    Column {

                        LazyRow(
                            modifier = Modifier.padding(12.dp, 12.dp, 0.dp, 12.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items((activeCurrencies.size - 1).coerceAtLeast(0)) { i ->
                                Card(
                                    modifier = Modifier

                                        .width(200.dp),
                                ) {
                                    var currency by remember { mutableStateOf(CurrencyFormat()) }
                                    LaunchedEffect(activeCurrencies) {
                                        currency =
                                            sharedViewModel.getCurrencyById(activeCurrencies[i + 1])!!
                                    }

                                    Column(
                                        modifier = Modifier.padding(12.dp)
                                    ) {
                                        if (currency.currencyName.isNotEmpty()) {
                                            ProfileAvatarWithFallback(
                                                initial = currency.currencyName.first()
                                                    .uppercaseChar().toString(),
                                                size = 48.dp
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(24.dp))

                                        Text(text = currency.currencyName)

                                        FormattedCurrency(
                                            currency = currency,
                                            style = MaterialTheme.typography.headlineMedium,
                                            value = currency.baseConvRate
                                        )
                                    }
                                }
                            }
                        }

                        CurrenciesList(
                            list = entityUiState.currenciesList,
                            viewModel = viewModel,
                            coroutineScope = coroutineScope,
                            onClicked = {
                                coroutineScope.launch {
                                    chartData = viewModel.makeChartModel(it)
                                    finished = true
                                }
                            },
                            longClicked = { selected ->
                            },
                        )
                    }
                }
            }
        }
    }
    }
}