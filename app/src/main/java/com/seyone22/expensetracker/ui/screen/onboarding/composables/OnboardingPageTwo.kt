package com.seyone22.expensetracker.ui.screen.onboarding.composables

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.seyone22.expensetracker.ui.screen.onboarding.OnboardingViewModel

@Composable
fun OnboardingPageTwo(
    pageIndex: Int = 2,
    viewModel: OnboardingViewModel,
    setButtonState: (Boolean) -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        OnboardingForm(
            modifier = Modifier.align(Alignment.BottomCenter),
            viewModel = viewModel,
            setButtonState = setButtonState
        )
    }
}