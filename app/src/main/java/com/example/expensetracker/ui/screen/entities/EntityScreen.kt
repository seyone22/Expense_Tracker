package com.example.expensetracker.ui.screen.entities

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.expensetracker.R
import com.example.expensetracker.SelectedObjects
import com.example.expensetracker.data.model.Category
import com.example.expensetracker.data.model.CurrencyFormat
import com.example.expensetracker.data.model.CurrencyHistory
import com.example.expensetracker.data.model.Payee
import com.example.expensetracker.ui.AppViewModelProvider
import com.example.expensetracker.ui.common.FormattedCurrency
import com.example.expensetracker.ui.common.removeTrPrefix
import com.example.expensetracker.ui.navigation.NavigationDestination
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.time.LocalDate
import kotlin.math.log

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

    var chartData: Map<LocalDate, Float> by remember { mutableStateOf(mapOf(LocalDate.parse("2022-01-01") to 2f)) }
    var finished: Boolean by remember { mutableStateOf(false) }

    var state by remember { mutableIntStateOf(0) }
    setTopBarAction(state)

    val titles = listOf("Categories", "Payees", "Currencies")
    val entityUiState: EntitiesUiState by viewModel.entitiesUiState.collectAsState(EntitiesUiState())

    val pagerState = rememberPagerState(pageCount = { 3 })

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
                        // TODO : DO the chart here.
                        /*                        if(finished) {
                                                    val scrollState = rememberVicoScrollState()
                                                    val zoomState = rememberVicoZoomState()

                                                    val xToDateMapKey = ExtraStore.Key<Map<Float, LocalDate>>()
                                                    var xToDates: Map<Float, LocalDate> = mapOf()

                                                    val dateTimeFormatter = DateTimeFormatter.ofPattern("MMM yyyy")
                                                    var x: ValueFormatter
                                                    val dataLoaded = remember { mutableStateOf(false) }

                                                    val modelProducer = remember { CartesianChartModelProducer.build() }
                                                    LaunchedEffect(Unit) {
                                                        xToDates = chartData.keys.associateBy { it.toEpochDay().toFloat() }

                                                        modelProducer.tryRunTransaction {
                                                            columnSeries { series(xToDates.keys, chartData.values) }
                                                        }
                                                        dataLoaded.value = true
                                                    }

                                                    if (dataLoaded.value) {
                                                        ProvideVicoTheme(theme = rememberM3VicoTheme()) {
                                                            CartesianChartHost(
                                                                chart = rememberCartesianChart(
                                                                    rememberColumnCartesianLayer(),
                                                                    startAxis = rememberStartAxis(),
                                                                    bottomAxis = rememberBottomAxis(
                                                                        valueFormatter = AxisValueFormatter<AxisPosition.Horizontal.Bottom> { x, chartValues, _ ->
                                                                            (chartValues.model.extraStore[xToDateMapKey][x] ?: LocalDate.ofEpochDay(x.toLong()))
                                                                                .format(dateTimeFormatter)
                                                                        }
                                                                    )
                                                                ),
                                                                modelProducer = modelProducer,
                                                                scrollState = scrollState,
                                                                zoomState = zoomState
                                                            )
                                                        }
                                                    }
                                                }*/

                        CurrenciesList(
                            list = entityUiState.currenciesList,
                            viewModel = viewModel,
                            coroutineScope = coroutineScope,
                            onClicked = {
                                coroutineScope.launch {
                                    chartData = viewModel.makeChartModel(it)
                                    finished = true
                                }
                                Log.d("TAG", "EntityScreen: $chartData")
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


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CategoryList(
    modifier: Modifier = Modifier,
    listParent: List<Category>,
    listSub: List<Category>,
    viewModel: EntityViewModel,
    longClicked: (Category) -> Unit,
    coroutineScope: CoroutineScope
) {
    val haptics = LocalHapticFeedback.current
    val groupedList = (listSub.groupBy { it.parentId })

    LazyColumn() {
        item {
            listParent.forEach { parent ->
                ListItem(
                    headlineContent = { Text(removeTrPrefix(parent.categName)) },
                    leadingContent = {
                        Icon(
                            Icons.Filled.Bookmark,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    modifier = Modifier.combinedClickable(
                        onClick = {},
                        onLongClick = {
                            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                            longClicked(parent)
                        },
                        onLongClickLabel = "  "
                    )
                )

                groupedList.forEach { group ->
                    if (group.key == parent.categId) {
                        group.value.forEach {
                            ListItem(
                                headlineContent = { Text(removeTrPrefix(it.categName)) },
                                leadingContent = {
                                    Icon(
                                        Icons.Filled.Bookmark,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                },
                                modifier = Modifier
                                    .combinedClickable(
                                        onClick = {},
                                        onLongClick = {
                                            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                            longClicked(it)
                                        },
                                        onLongClickLabel = "  "
                                    )
                                    .padding(24.dp, 0.dp, 0.dp, 0.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PayeeList(
    modifier: Modifier = Modifier,
    list: List<Payee>,
    coroutineScope: CoroutineScope,
    longClicked: (Payee) -> Unit,
    viewModel: EntityViewModel
) {
    val haptics = LocalHapticFeedback.current

    LazyColumn() {
        items(list, key = { it.payeeId }) {
            ListItem(
                headlineContent = { Text(it.payeeName) },
                overlineContent = { Text(it.payeeId.toString()) },
                leadingContent = {
                    Icon(
                        Icons.Filled.Favorite,
                        contentDescription = "Localized description",
                    )
                },
                modifier = Modifier.combinedClickable(
                    onClick = {},
                    onLongClick = {
                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                        longClicked(it)
                    },
                    onLongClickLabel = "  "
                )
            )
            HorizontalDivider()
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CurrenciesList(
    modifier: Modifier = Modifier,
    list: Pair<List<CurrencyFormat>, List<CurrencyHistory>?>,
    coroutineScope: CoroutineScope,
    longClicked: (CurrencyFormat) -> Unit,
    onClicked: (String) -> Unit,
    viewModel: EntityViewModel
) {
    val haptics = LocalHapticFeedback.current

    LazyColumn() {
        items(list.first, key = { it.currencyId }) {
            Log.d("TAG", "CurrenciesList: ${list.second}")
            val x = list.second?.find { historyEntry -> historyEntry.currencyId == it.currencyId }
            ListItem(
                headlineContent = {
                    Row{
                        FormattedCurrency(
                            value = it.baseConvRate,
                            currency = CurrencyFormat(),
                        )
                        if (x != null) {
                            val difference = x.currValue.minus(it.baseConvRate)
                            if (x.currValue > it.baseConvRate) {
                                // Red down triangle and difference in red
                                Icon(
                                    imageVector = Icons.Filled.ArrowDownward,
                                    contentDescription = null,
                                    tint = Color.Red,
                                    modifier = Modifier.padding(start = 4.dp)
                                )
                                Text(
                                    text = String.format("%.2f", difference),
                                    color = Color.Red,
                                    fontSize = 16.sp,
                                    modifier = Modifier.padding(start = 4.dp)
                                )
                            } else if (x.currValue < it.baseConvRate) {
                                // Green up triangle and difference in green
                                Icon(
                                    imageVector = Icons.Filled.ArrowUpward,
                                    contentDescription = null,
                                    tint = Color.Green,
                                    modifier = Modifier.padding(start = 4.dp)
                                )
                                Text(
                                    text = String.format("%.2f", -difference!!),
                                    color = Color.Green,
                                    fontSize = 16.sp,
                                    modifier = Modifier.padding(start = 4.dp)
                                )
                            }
                            // If the values are the same (to 2 decimal places), nothing is shown
                        }
                    }
                },
                overlineContent = { Text(removeTrPrefix(it.currencyName)) },
                leadingContent = {
                    Text(
                        text = it.currency_symbol,
                        modifier = Modifier.requiredWidth(48.dp)
                    )
                },
                modifier = Modifier.combinedClickable(
                    onClick = {
                        onClicked(it.currency_symbol)
                    },
                    onLongClick = {
                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                        longClicked(it)
                    },
                    onLongClickLabel = "  "
                )
            )
            HorizontalDivider()
        }
    }
}