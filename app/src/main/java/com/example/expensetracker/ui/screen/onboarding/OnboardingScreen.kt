package com.example.expensetracker.ui.screen.onboarding

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.expensetracker.R
import com.example.expensetracker.model.CurrencyFormat
import com.example.expensetracker.model.Metadata
import com.example.expensetracker.ui.AppViewModelProvider
import com.example.expensetracker.ui.navigation.NavigationDestination
import kotlinx.coroutines.launch

object OnboardingDestination : NavigationDestination {
    override val route = "Onboarding"
    override val titleRes = R.string.app_name
    override val routeId = 0
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(
    modifier: Modifier = Modifier
        .padding(16.dp, 12.dp),
    navigateToScreen: (screen: String) -> Unit,
    viewModel: OnboardingViewModel = viewModel(factory = AppViewModelProvider.Factory),

    ) {

    OnboardingSheet(
        modifier = modifier,
        viewModel = viewModel,
        navigateToScreen = navigateToScreen
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingSheet(
    modifier: Modifier,
    viewModel: OnboardingViewModel,
    navigateToScreen: (screen: String) -> Unit,
    context: Context = LocalContext.current
) {
    val focusManager = LocalFocusManager.current
    val coroutineScope = rememberCoroutineScope()

    var username by remember { mutableStateOf("") }
    val currencyList by viewModel.currencyList.collectAsState()
    var currentCurrency by remember { mutableStateOf(CurrencyFormat()) }
    var baseCurrencyExpanded by remember { mutableStateOf(false) }


    LaunchedEffect(Unit) {
        viewModel.prepopulateDB(context)
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxHeight()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier
                .fillMaxWidth()
                .focusGroup()
        ) {
            Text(
                text = "Hello! How should we call you?",
                style = MaterialTheme.typography.titleLarge
            )
            OutlinedTextField(
                modifier = Modifier.padding(0.dp, 8.dp),
                value = username,
                onValueChange = {
                    username = it
                    viewModel.updateUiState(
                        viewModel.metadataUiState.metadataDetails.copy(
                            usernameMetadata = Metadata(6, "USERNAME", username)
                        )
                    )
                },
                label = { Text("Username") },
                singleLine = true,
                keyboardActions = KeyboardActions(onDone = { focusManager.moveFocus(FocusDirection.Next) })
            )

            Text(text = "Set your base Currency")
            ExposedDropdownMenuBox(
                expanded = baseCurrencyExpanded,
                onExpandedChange = { baseCurrencyExpanded = !baseCurrencyExpanded }) {
                OutlinedTextField(
                    modifier = Modifier
                        .padding(0.dp, 8.dp)
                        .clickable(enabled = true) { baseCurrencyExpanded = true }
                        .menuAnchor(),
                    value = currentCurrency.currencyName,
                    readOnly = true,
                    onValueChange = { },
                    label = { Text("Base Currency") },
                    singleLine = true,
                    keyboardActions = KeyboardActions(onDone = {
                        focusManager.moveFocus(
                            FocusDirection.Next
                        )
                    }),
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = baseCurrencyExpanded) },
                )

                ExposedDropdownMenu(
                    expanded = baseCurrencyExpanded,
                    onDismissRequest = { baseCurrencyExpanded = false },
                ) {
                    currencyList.currenciesList.forEach { currency ->
                        DropdownMenuItem(
                            text = { Text(currency.currencyName) },
                            onClick = {
                                currentCurrency = currency
                                viewModel.updateUiState(
                                    viewModel.metadataUiState.metadataDetails.copy(
                                        baseCurrencyMetadata = Metadata(
                                            5,
                                            "BASECURRENCYID",
                                            currentCurrency.currencyId.toString()
                                        )
                                    )
                                )
                                baseCurrencyExpanded = false
                            }
                        )
                    }
                }
            }

            Button(
                enabled = viewModel.metadataUiState.isEntryValid,
                onClick = {
                    coroutineScope.launch {
                        viewModel.saveItems()
                        navigateToScreen("EnterAccount")
                    }
                },
            ) {
                Text(text = "Get Started")
            }
        }
    }

}