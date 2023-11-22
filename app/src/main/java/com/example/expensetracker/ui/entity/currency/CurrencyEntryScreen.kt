package com.example.expensetracker.ui.entity.currency

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.expensetracker.R
import com.example.expensetracker.ui.AppViewModelProvider
import com.example.expensetracker.ui.navigation.NavigationDestination
import com.example.expensetracker.ui.theme.ExpenseTrackerTheme
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object CurrencyEntryDestination : NavigationDestination {
    override val route = "EnterCurrency"
    override val titleRes = R.string.app_name
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
        containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
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