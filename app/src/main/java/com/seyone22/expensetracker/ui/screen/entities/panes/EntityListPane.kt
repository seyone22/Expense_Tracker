package com.seyone22.expensetracker.ui.screen.entities.panes

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.ThreePaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.window.core.layout.WindowSizeClass
import androidx.window.core.layout.WindowWidthSizeClass
import com.seyone22.expensetracker.SharedViewModel
import com.seyone22.expensetracker.ui.AppViewModelProvider
import com.seyone22.expensetracker.ui.common.ExpenseTopBar
import com.seyone22.expensetracker.ui.screen.entities.EntitiesDestination
import com.seyone22.expensetracker.ui.screen.entities.EntitiesUiState
import com.seyone22.expensetracker.ui.screen.entities.EntityViewModel
import com.seyone22.expensetracker.ui.screen.entities.composables.CategoryList
import com.seyone22.expensetracker.ui.screen.entities.composables.CurrenciesList
import com.seyone22.expensetracker.ui.screen.entities.composables.PayeeList
import com.seyone22.expensetracker.ui.screen.entities.composables.TagList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun EntityListPane(
    modifier: Modifier = Modifier,
    viewModel: EntityViewModel,
    windowSizeClass: WindowSizeClass = currentWindowAdaptiveInfo().windowSizeClass,
    navController: NavHostController,
    scaffoldNavigator: ThreePaneScaffoldNavigator<Int>,
    coroutineScope: CoroutineScope = rememberCoroutineScope()
) {
    val sharedViewModel: SharedViewModel = viewModel(factory = AppViewModelProvider.Factory)
    var state by remember { mutableIntStateOf(0) }
    val titles = listOf("Category", "Payee", "Currency", "Tag")
    val entityUiState: EntitiesUiState by viewModel.entitiesUiState.collectAsState(EntitiesUiState())
    val pagerState = rememberPagerState(pageCount = { 4 })

    LaunchedEffect(state) {
        viewModel.setSelectedEntity("")
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.statusBars)
    ) {
        ExpenseTopBar(
            selectedActivity = EntitiesDestination.route,
            type = "Left",
            hasNavBarAction = false,
            navController = navController,
            hasNavigation = true
        )

        PrimaryTabRow(selectedTabIndex = state) {
            titles.forEachIndexed { index, title ->
                Tab(selected = state == index, onClick = {
                    state = index
                    coroutineScope.launch { pagerState.animateScrollToPage(index) }
                }, text = { Text(text = title, maxLines = 2, overflow = TextOverflow.Ellipsis) })
            }
        }

        HorizontalPager(
            state = pagerState,
            verticalAlignment = Alignment.Top,
            modifier = Modifier.fillMaxHeight()
        ) { page ->
            state = pagerState.currentPage
            when (page) {
                0 -> CategoryList(listParent = entityUiState.categoriesParent,
                    listSub = entityUiState.categoriesSub,
                    isSelected = windowSizeClass.windowWidthSizeClass != WindowWidthSizeClass.COMPACT,
                    viewModel = viewModel,
                    coroutineScope = coroutineScope,
                    onClicked = {
                        coroutineScope.launch {
                            scaffoldNavigator.navigateTo(
                                pane = ListDetailPaneScaffoldRole.Detail,
                                contentKey = it
                            )
                        }
                    },
                    longClicked = {})

                1 -> PayeeList(list = entityUiState.payeesList.sortedBy { it.payeeName.lowercase() },
                    viewModel = viewModel,
                    isSelected = windowSizeClass.windowWidthSizeClass != WindowWidthSizeClass.COMPACT,
                    coroutineScope = coroutineScope,
                    onClicked = {
                        coroutineScope.launch {
                            scaffoldNavigator.navigateTo(
                                pane = ListDetailPaneScaffoldRole.Detail,
                                contentKey = it
                            )
                        }
                    },
                    longClicked = {})

                2 -> CurrenciesList(list = entityUiState.currenciesList,
                    viewModel = viewModel,
                    isSelected = windowSizeClass.windowWidthSizeClass != WindowWidthSizeClass.COMPACT,
                    coroutineScope = coroutineScope,
                    onClicked = {
                        coroutineScope.launch {
                            scaffoldNavigator.navigateTo(
                                pane = ListDetailPaneScaffoldRole.Detail,
                                contentKey = it
                            )
                        }
                    },
                    longClicked = {})

                3 -> TagList(
                    viewModel = viewModel,
                    coroutineScope = coroutineScope,
                    isSelected = windowSizeClass.windowWidthSizeClass != WindowWidthSizeClass.COMPACT,
                    onClicked = {
                        coroutineScope.launch {
                            scaffoldNavigator.navigateTo(
                                pane = ListDetailPaneScaffoldRole.Detail,
                                contentKey = it
                            )
                        }
                    },
                )
            }
        }
    }
}