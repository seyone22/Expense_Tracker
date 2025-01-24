package com.example.expensetracker.ui.screen.onboarding

import PaginationDotsIndicator
import android.content.Context
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.expensetracker.R
import com.example.expensetracker.ui.AppViewModelProvider
import com.example.expensetracker.ui.navigation.NavigationDestination
import com.example.expensetracker.ui.screen.home.HomeDestination
import com.example.expensetracker.ui.screen.onboarding.composables.OnboardingPageOne
import com.example.expensetracker.ui.screen.onboarding.composables.OnboardingPageTwo
import kotlinx.coroutines.launch

object OnboardingDestination : NavigationDestination {
    override val route = "Onboarding"
    override val titleRes = R.string.app_name
    override val routeId = 0
}

@Composable
fun OnboardingScreen(
    modifier: Modifier = Modifier.padding(16.dp, 12.dp),
    navigateToScreen: (screen: String) -> Unit,
    viewModel: OnboardingViewModel = viewModel(factory = AppViewModelProvider.Factory),
    context: Context = LocalContext.current
) {
    val coroutineScope = rememberCoroutineScope() // Required for calling suspend functions
    val pagerState = rememberPagerState(pageCount = { 2 }) // Adjust pageCount to match actual pages

    var buttonEnabled by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        viewModel.prepopulateDB(context)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.ime)
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f) // Allow pager to take up available space
        ) { page ->
            when (page) {
                0 -> OnboardingPageOne(setButtonState = { v -> buttonEnabled = v })
                1 -> OnboardingPageTwo(viewModel = viewModel,
                    setButtonState = { v -> buttonEnabled = v })
                // Add a third page if needed
            }
        }

        Row(
            modifier = Modifier
                .height(96.dp) // Adjust height for proper spacing
                .padding(top = 16.dp, start = 16.dp, end = 16.dp)
                .fillMaxWidth(),
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            PaginationDotsIndicator(
                totalDots = 2,
                selectedIndex = pagerState.currentPage // Use currentPage for dynamic selection
            )

            FilledIconButton(
                enabled = buttonEnabled,
                onClick = {
                    coroutineScope.launch {
                        if (pagerState.currentPage == 0) {
                            pagerState.animateScrollToPage(1)
                        } else if (pagerState.currentPage == 1) {
                            viewModel.saveItems()
                            navigateToScreen(HomeDestination.route)
                        }
                    }
                },
                modifier = Modifier.then(
                    // Use animated values for size
                    Modifier
                        .height(
                            animateDpAsState(
                                targetValue = if (pagerState.currentPage == 0) 64.dp else 64.dp,
                                animationSpec = tween(durationMillis = 300),
                                label = "" // Animation duration
                            ).value
                        )
                        .width(
                            animateDpAsState(
                                targetValue = if (pagerState.currentPage == 0) 64.dp else 128.dp,
                                animationSpec = tween(durationMillis = 300),
                                label = "" // Animation duration
                            ).value
                        )
                ),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp, 0.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if (pagerState.currentPage == 1) {
                        Text(
                            text = "Begin",
                            modifier = Modifier.padding(end = 8.dp),
                            style = MaterialTheme.typography.titleMedium,
                            maxLines = 1, // Ensures only 1 line is allowed
                            overflow = TextOverflow.Ellipsis // Optional, if you want to truncate text with ellipsis
                        )
                    }
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ArrowForward,
                        contentDescription = null,
                        Modifier.size(36.dp)
                    )
                }
            }
        }
    }
}
