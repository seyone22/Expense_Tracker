package com.example.expensetracker.ui.screen.operations.entity.currency

import android.annotation.SuppressLint
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.expensetracker.R
import com.example.expensetracker.ui.AppViewModelProvider
import com.example.expensetracker.ui.navigation.NavigationDestination
import com.example.expensetracker.ui.theme.ExpenseTrackerTheme
import kotlinx.coroutines.launch

object CurrencyEntryDestination : NavigationDestination {
    override val route = "EnterCurrency"
    override val titleRes = R.string.app_name
    override val routeId = 16
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrencyEntryScreen(
    navigateBack: () -> Unit = {},
    onNavigateUp: () -> Unit = {},
    canNavigateBack: Boolean = true,
    viewModel: CurrencyEntryViewModel = viewModel(factory = AppViewModelProvider.Factory),
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                ),
                title = {
                    Text(
                        text = "Create Currency",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        navigateBack()
                    }) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Close"
                        )
                    }
                },
                actions = {
                    Button(
                        onClick = {
                        coroutineScope.launch {
                            viewModel.saveCurrency()
                            navigateBack()
                        }
                        },
                        modifier = modifier.padding(0.dp,0.dp,8.dp,0.dp),
                        enabled = viewModel.currencyUiState.isEntryValid
                    ) {
                        Text(text = "Create")
                    }
                }
            )

        }

    ) { padding ->
        CurrencyEntryBody(
            currencyUiState = viewModel.currencyUiState,
            onCurrencyValueChange = viewModel::updateUiState,
            modifier = modifier.padding(padding)
        )
    }

}

@Composable
fun CurrencyEntryBody(
    currencyUiState: CurrencyUiState = CurrencyUiState(),
    onCurrencyValueChange: (CurrencyDetails) -> Unit = {},
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        item {
            CurrencyEntryForm(
                currencyDetails = currencyUiState.currencyDetails,
                onValueChange = onCurrencyValueChange,
                modifier = Modifier
            )
        }
    }
}

@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrencyEntryForm(
    currencyDetails: CurrencyDetails,
    onValueChange: (CurrencyDetails) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    var currencyTypeExpanded by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current

    Column(
        modifier = modifier
            .focusGroup()
            .padding(0.dp, 8.dp)
    )
    {

    }

}





@Preview(showBackground = true)
@Composable
fun CurrencyEntryFormPreview() {
    ExpenseTrackerTheme {
        CurrencyEntryScreen()
    }
}