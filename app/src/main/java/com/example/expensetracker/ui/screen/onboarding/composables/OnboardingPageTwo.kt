package com.example.expensetracker.ui.screen.onboarding.composables

import OnboardingForm
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.expensetracker.ui.screen.onboarding.OnboardingViewModel

@OptIn(ExperimentalMaterial3Api::class)
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