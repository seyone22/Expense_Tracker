package com.example.expensetracker.ui.screen.settings

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CatchingPokemon
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.expensetracker.R
import com.example.expensetracker.model.CurrencyFormat
import com.example.expensetracker.model.Metadata
import com.example.expensetracker.ui.AppViewModelProvider
import com.example.expensetracker.ui.navigation.NavigationDestination
import kotlinx.coroutines.launch

object SettingsDetailDestination : NavigationDestination {
    override val route = "SettingsDetail"
    override val titleRes = R.string.app_name
    override val routeId = 15
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsDetailScreen(
    modifier: Modifier = Modifier,
    navigateToScreen: (screen: String) -> Unit,
    navigateBack: () -> Unit,
    backStackEntry: String,
    viewModel: SettingsViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val metadataList by viewModel.metadataList.collectAsState(listOf())

    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                ),
                title = { Text(text = backStackEntry) },
                navigationIcon = {
                    IconButton(onClick = { navigateBack() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = null
                        )
                    }
                }
            )
        },
    ) { innerPadding ->
        Column(
            Modifier.padding(innerPadding)
        ) {
            when (backStackEntry) {
                "General" -> {
                    GeneralSettingsList(
                        metadata = metadataList,
                        viewModel = viewModel
                    )
                }

                "Appearance" -> {
                    GeneralSettingsList(
                        metadata = metadataList,
                        viewModel = viewModel
                    )
                }

                "About" -> {
                    AboutList(

                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GeneralSettingsList(
    metadata: List<Metadata?>,
    viewModel: SettingsViewModel
) {
    val coroutineScope = rememberCoroutineScope()
    var baseCurrencyName by remember { mutableStateOf("") }

    var editCurrency by remember { mutableStateOf(false) }
    var editName by remember { mutableStateOf((false)) }

    Column {
        ListItem(
            headlineContent = { Text(text = "Username") },
            supportingContent = {
                metadata.find { (it?.infoName ?: "USERNAME") == "USERNAME" }
                    ?.let { Text(text = it.infoValue) }
            },
            modifier = Modifier.clickable { editName = !editName }
        )
        ListItem(
            headlineContent = { Text(text = "Base Currency") },
            supportingContent = {
                metadata.find { (it?.infoName ?: "BASECURRENCYID") == "BASECURRENCYID" }?.let {
                    coroutineScope.launch {
                        baseCurrencyName =
                            viewModel.getBaseCurrencyInfo(it.infoValue.toInt()).currencyName
                        Log.d("TAG", "GeneralSettingsList: $baseCurrencyName")
                    }
                }
                Text(text = baseCurrencyName)
            },
            modifier = Modifier.clickable { editCurrency = !editCurrency }

        )
    }

    // Edit name Dialog
    if (editName) {
        var newName: String by remember {
            mutableStateOf(
                metadata.find { it?.infoName == "USERNAME" }?.infoValue ?: ""
            )
        }

        Dialog(onDismissRequest = { editName = !editName }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(275.dp)
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = "Enter your new username",
                        modifier = Modifier.padding(8.dp),
                    )
                    OutlinedTextField(
                        value = newName,
                        onValueChange = { newName = it },
                        modifier = Modifier.padding(8.dp),
                        label = { Text("Username") },
                        singleLine = true,
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        TextButton(
                            onClick = { editName = !editName },
                            modifier = Modifier.padding(8.dp),
                        ) {
                            Text("Dismiss")
                        }
                        TextButton(
                            onClick = {
                                editName = !editName
                                coroutineScope.launch {
                                    viewModel.changeUsername(newName)
                                }
                            },
                            modifier = Modifier.padding(8.dp),
                        ) {
                            Text("Confirm")
                        }
                    }
                }
            }
        }
    }
    // Edit Currency Dialog
    if (editCurrency) {
        val currencyList by viewModel.currencyList.collectAsState()
        var newCurrencyId: String by remember {
            mutableStateOf(
                metadata.find { it?.infoName == "BASECURRENCYID" }?.infoValue ?: ""
            )
        }
        var newCurrency: CurrencyFormat = CurrencyFormat()
        var baseCurrencyExpanded by remember { mutableStateOf(false) }

        Dialog(
            onDismissRequest = { editCurrency = !editCurrency },
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(275.dp)
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = "Select your new base currency",
                        modifier = Modifier.padding(8.dp),
                    )
                    ExposedDropdownMenuBox(
                        expanded = baseCurrencyExpanded,
                        onExpandedChange = { baseCurrencyExpanded = !baseCurrencyExpanded }) {
                        OutlinedTextField(
                            modifier = Modifier
                                .padding(0.dp, 8.dp)
                                .clickable(enabled = true) { baseCurrencyExpanded = true }
                                .menuAnchor(),
                            value = newCurrency.currencyName,
                            readOnly = true,
                            onValueChange = {

                            },
                            label = { Text("Base Currency") },
                            singleLine = true,
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
                                        newCurrency = currency
                                        baseCurrencyExpanded = false
                                    }
                                )
                            }
                        }
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        TextButton(
                            onClick = { editCurrency = !editCurrency },
                            modifier = Modifier.padding(8.dp),
                        ) {
                            Text("Dismiss")
                        }
                        TextButton(
                            onClick = {
                                editCurrency = !editCurrency
                                coroutineScope.launch {
                                    viewModel.changeCurrency(newCurrency.currencyId)
                                }
                            },
                            modifier = Modifier.padding(8.dp),
                        ) {
                            Text("Confirm")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AboutList() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(0.dp, 24.dp)
    ) {
        Icon(
            imageVector = Icons.Filled.CatchingPokemon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(100.dp)
        )
    }
    HorizontalDivider()
    Column {
        ListItem(
            headlineContent = { Text(text = "Version") },
            supportingContent = { Text(text = "Alpha v0.1.1 (25/11/2023 : 00:00)") },
            modifier = Modifier.clickable { }
        )
    }
}