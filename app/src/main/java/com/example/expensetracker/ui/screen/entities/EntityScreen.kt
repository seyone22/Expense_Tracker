package com.example.expensetracker.ui.screen.entities

import androidx.compose.foundation.ExperimentalFoundationApi
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
import com.example.expensetracker.R
import com.example.expensetracker.SelectedObjects
import com.example.expensetracker.SharedViewModel
import com.example.expensetracker.data.model.CurrencyFormat
import com.example.expensetracker.ui.AppViewModelProvider
import com.example.expensetracker.ui.common.FormattedCurrency
import com.example.expensetracker.ui.common.ProfileAvatarWithFallback
import com.example.expensetracker.ui.navigation.NavigationDestination
import com.example.expensetracker.ui.screen.entities.composables.CategoryList
import com.example.expensetracker.ui.screen.entities.composables.CurrenciesList
import com.example.expensetracker.ui.screen.entities.composables.PayeeList
import kotlinx.coroutines.launch
import java.time.LocalDate

object EntitiesDestination : NavigationDestination {
    override val route = "Entities"
    override val titleRes = R.string.app_name
    override val routeId = 1
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun EntityScreen(
    modifier: Modifier = Modifier,
    navigateToScreen: (screen: String) -> Unit,
    setTopBarAction: (Int) -> Unit,
    setIsItemSelected: (Boolean) -> Unit,
    setSelectedObject: (SelectedObjects) -> Unit,
    viewModel: EntityViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val coroutineScope = rememberCoroutineScope()
    val sharedViewModel: SharedViewModel = viewModel(factory = AppViewModelProvider.Factory)

    var chartData: Map<LocalDate, Float> by remember { mutableStateOf(mapOf(LocalDate.parse("2022-01-01") to 2f)) }
    var finished: Boolean by remember { mutableStateOf(false) }

    var state by remember { mutableIntStateOf(0) }
    setTopBarAction(state)

    val titles = listOf("Categories", "Payees", "Currencies")
    val entityUiState: EntitiesUiState by viewModel.entitiesUiState.collectAsState(EntitiesUiState())

    // separate state
    val activeCurrencies by viewModel.activeCurrenciesFlow.collectAsState(listOf())

    val pagerState = rememberPagerState(pageCount = { 3 })

    Column(modifier = modifier.fillMaxSize()) {
        PrimaryTabRow(
            selectedTabIndex = state,
            containerColor = MaterialTheme.colorScheme.background,
        ) {
            titles.forEachIndexed { index, title ->
                Tab(selected = state == index, onClick = {
                    state = index
                    setTopBarAction(state)
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
                            setIsItemSelected(true)
                            setSelectedObject(SelectedObjects(category = selected))
                        },
                    )
                }

                1 -> {
                    state = pagerState.currentPage
                    PayeeList(
                        list = entityUiState.payeesList,
                        viewModel = viewModel,
                        coroutineScope = coroutineScope,
                        longClicked = { selected ->
                            setIsItemSelected(true)
                            setSelectedObject(SelectedObjects(payee = selected))
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
                            items(activeCurrencies.size-1) { i ->
                                Card(
                                    modifier = Modifier

                                        .width(200.dp),
                                ) {
                                    var currency by remember { mutableStateOf(CurrencyFormat()) }
                                    LaunchedEffect(activeCurrencies) {
                                        currency =
                                            sharedViewModel.getCurrencyById(activeCurrencies[i+1])!!
                                    }

                                    Column(
                                        modifier = Modifier.padding(12.dp)
                                    ) {
                                        if (currency.currencyName.isNotEmpty()) {
                                            ProfileAvatarWithFallback(
                                                initial = currency.currencyName.first().uppercaseChar().toString(),
                                                size = 48.dp
                                            )
                                        }

                                        Spacer(modifier=Modifier.height(24.dp))

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
                                setIsItemSelected(true)
                                setSelectedObject(SelectedObjects(currency = selected))
                            },
                        )
                    }
                }
            }
        }
    }
}