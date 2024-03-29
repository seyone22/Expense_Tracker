package com.example.expensetracker.ui.screen.entities

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.expensetracker.R
import com.example.expensetracker.SelectedObjects
import com.example.expensetracker.model.Category
import com.example.expensetracker.model.CurrencyFormat
import com.example.expensetracker.model.Payee
import com.example.expensetracker.ui.AppViewModelProvider
import com.example.expensetracker.ui.common.FormattedCurrency
import com.example.expensetracker.ui.common.removeTrPrefix
import com.example.expensetracker.ui.navigation.NavigationDestination
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

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
                    CurrenciesList(
                        list = entityUiState.currenciesList,
                        viewModel = viewModel,
                        coroutineScope = coroutineScope,
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
                    if(group.key == parent.categId) {
                        group.value.forEach {
                            ListItem(
                                headlineContent = { Text(removeTrPrefix(it.categName)) },
                                leadingContent = {
                                    Icon(
                                        Icons.Filled.Bookmark,
                                        contentDescription = null,
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
                                    .padding(24.dp,0.dp,0.dp,0.dp)
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
    list: List<CurrencyFormat>,
    coroutineScope: CoroutineScope,
    longClicked: (CurrencyFormat) -> Unit,
    viewModel: EntityViewModel
) {
    val haptics = LocalHapticFeedback.current

    LazyColumn() {
        items(list, key = { it.currencyId }) {
            ListItem(
                headlineContent = {
                    FormattedCurrency(
                        value = it.baseConvRate,
                        currency = CurrencyFormat(),
                    )
                },
                overlineContent = { Text(removeTrPrefix(it.currencyName)) },
                leadingContent = { Text(
                    text = it.currency_symbol,
                    modifier = Modifier.requiredWidth(48.dp)
                ) },
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