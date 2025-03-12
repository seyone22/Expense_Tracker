package com.seyone22.expensetracker.ui.screen.settings

import android.app.Activity
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.seyone22.expensetracker.R
import com.seyone22.expensetracker.SharedViewModel
import com.seyone22.expensetracker.data.model.CurrencyFormat
import com.seyone22.expensetracker.data.model.Metadata
import com.seyone22.expensetracker.ui.AppViewModelProvider
import com.seyone22.expensetracker.ui.common.removeTrPrefix
import com.seyone22.expensetracker.ui.navigation.NavigationDestination
import com.seyone22.expensetracker.ui.theme.LocalTheme
import com.seyone22.expensetracker.utils.BiometricHelper
import com.seyone22.expensetracker.utils.BiometricPromptActivityResultContract
import com.seyone22.expensetracker.utils.CryptoManager
import com.seyone22.expensetracker.utils.ScreenLockManager
import com.seyone22.expensetracker.utils.SnackbarManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

object SettingsDetailDestination : NavigationDestination {
    override val route = "SettingsDetail"
    override val titleRes = R.string.app_name
    override val routeId = 15
}

@RequiresApi(Build.VERSION_CODES.R)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsDetailScreen(
    modifier: Modifier = Modifier,
    navigateToScreen: (screen: String) -> Unit,
    onToggleDarkTheme: (Int) -> Unit,
    navigateBack: () -> Unit,
    backStackEntry: String,
    viewModel: SettingsViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val metadataList by viewModel.metadataList.collectAsState(emptyList())

    val snackbarHostState = SnackbarManager.hostState

    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    actionColor = Color.Red // Customize for error messages
                )
            }
        },
        topBar = {
            TopAppBar(colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.background,
                titleContentColor = MaterialTheme.colorScheme.onSurface,
            ), title = { Text(text = backStackEntry) }, navigationIcon = {
                IconButton(onClick = { navigateBack() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null
                    )
                }
            })
        },
    ) { innerPadding ->
        Column(
            Modifier.padding(innerPadding)
        ) {
            when (backStackEntry) {
                "General" -> {
                    GeneralSettingsList(
                        metadata = metadataList, viewModel = viewModel
                    )
                }

                "Appearance" -> {
                    AppearanceSettingsList(
                        metadata = metadataList,
                        onToggleDarkTheme = { onToggleDarkTheme(it) },
                        viewModel = viewModel
                    )
                }

                "Data" -> {
                    DataSettingsList(
                        metadata = metadataList, viewModel = viewModel
                    )
                }

                "Security" -> {
                    SecuritySettingsList(
                    )
                }

                "ImportExport" -> {
                    ImportExportSettingsList(
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
    metadata: List<Metadata?>, viewModel: SettingsViewModel
) {
    val coroutineScope = rememberCoroutineScope()
    var baseCurrencyName by remember { mutableStateOf("") }

    var editCurrency by remember { mutableStateOf(false) }
    var editName by remember { mutableStateOf((false)) }

    Column {
        ListItem(headlineContent = { Text(text = "Username") }, supportingContent = {
            metadata.find { (it?.infoName ?: "USERNAME") == "USERNAME" }
                ?.let { Text(text = it.infoValue) }
        }, modifier = Modifier.clickable { editName = !editName })
        ListItem(headlineContent = { Text(text = "Base Currency") }, supportingContent = {
            metadata.find { (it?.infoName ?: "BASECURRENCYID") == "BASECURRENCYID" }?.let {
                coroutineScope.launch {
                    baseCurrencyName =
                        viewModel.getBaseCurrencyInfo(it.infoValue.toInt()).currencyName
                    Log.d("TAG", "GeneralSettingsList: $baseCurrencyName")
                }
            }
            Text(text = removeTrPrefix(baseCurrencyName))
        }, modifier = Modifier.clickable { editCurrency = !editCurrency }

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
                    modifier = Modifier.fillMaxSize(),
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
                        modifier = Modifier.fillMaxWidth(),
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
        var newCurrency = CurrencyFormat()
        newCurrency.currencyName = removeTrPrefix(baseCurrencyName)
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
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = "Select your new base currency",
                        modifier = Modifier.padding(8.dp),
                    )
                    ExposedDropdownMenuBox(expanded = baseCurrencyExpanded,
                        onExpandedChange = { baseCurrencyExpanded = !baseCurrencyExpanded }) {
                        OutlinedTextField(
                            modifier = Modifier
                                .padding(0.dp, 8.dp)
                                .clickable(enabled = true) { baseCurrencyExpanded = true }
                                .menuAnchor(MenuAnchorType.PrimaryEditable, true),
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
                                DropdownMenuItem(text = { Text(removeTrPrefix(currency.currencyName)) },
                                    onClick = {
                                        newCurrency = currency
                                        baseCurrencyExpanded = false
                                    })
                            }
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
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
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Example image",
            modifier = Modifier.size(180.dp)
        )

    }
    HorizontalDivider()
    Column {
        ListItem(headlineContent = { Text(text = "Version") }, supportingContent = {
            Text(
                text = "${stringResource(id = R.string.app_version)} (${
                    stringResource(
                        id = R.string.release_date
                    )
                } | ${stringResource(id = R.string.release_time)})"
            )
        }, modifier = Modifier.clickable { })
        ListItem(headlineContent = { Text(text = "Check for updates") },
            modifier = Modifier.clickable { })
        ListItem(headlineContent = { Text(text = "What's new") },
            modifier = Modifier.clickable { })
        ListItem(headlineContent = { Text(text = "Open Source licenses") },
            modifier = Modifier.clickable { })
        ListItem(headlineContent = { Text(text = "Privacy Policy") },
            modifier = Modifier.clickable { })
        ListItem(headlineContent = {
            Row(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
//                IconButton(onClick = { openUrl("https://example.com") }) {
//                    Icon(
//                        imageVector = Icons.Default.Language, // Website Icon
//                        contentDescription = "Website",
//                        tint = Color.Blue
//                    )
//                }
//
//                IconButton(onClick = { openUrl("https://discord.com/invite/example") }) {
//                    Icon(
//                        painter = painterResource(id = R.drawable.ic_discord), // Custom drawable
//                        contentDescription = "Discord",
//                        tint = Color(0xFF5865F2) // Discord Blue
//                    )
//                }
//
//                IconButton(onClick = { openUrl("https://bsky.app/profile/example.bsky.social") }) {
//                    Icon(
//                        painter = painterResource(id = R.drawable.ic_bluesky), // Custom drawable
//                        contentDescription = "Bluesky",
//                        tint = Color.Black
//                    )
//                }
//
//                IconButton(onClick = { openUrl("https://github.com/example") }) {
//                    Icon(
//                        imageVector = Icons.Default.GitHub, // Use a GitHub icon vector
//                        contentDescription = "GitHub",
//                        tint = Color.Black
//                    )
//                }
            }
        },
            modifier = Modifier.clickable { })

    }
}

@Composable
fun AppearanceSettingsList(
    metadata: List<Metadata?>, onToggleDarkTheme: (Int) -> Unit, viewModel: SettingsViewModel
) {
    val coroutineScope = rememberCoroutineScope()

    var editTheme by remember { mutableStateOf(false) }

    var selectedTheme by remember { mutableIntStateOf(0) }

    Column {
        ListItem(headlineContent = { Text(text = "Theme") }, supportingContent = {
            if (LocalTheme.current.isDark && !LocalTheme.current.isMidnight) {
                Text(text = "Dark")
                selectedTheme = 1
            } else if (LocalTheme.current.isDark && LocalTheme.current.isMidnight) {
                Text(text = "Midnight")
            } else {
                Text(text = "Light")
                selectedTheme = 0
            }
        }, modifier = Modifier.clickable { editTheme = !editTheme })

        // Edit Theme Dialog
        if (editTheme) {
            Dialog(
                onDismissRequest = { editTheme = !editTheme },
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(275.dp)
                        .padding(16.dp),
                    shape = RoundedCornerShape(16.dp),
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.Start,
                    ) {
                        Text(
                            text = "Theme",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(0.dp, 8.dp)
                        )
                        Row {
                            RadioButton(enabled = true, selected = (selectedTheme == 0), onClick = {
                                selectedTheme = 0
                                coroutineScope.launch { viewModel.setTheme(selectedTheme) }
                                onToggleDarkTheme(selectedTheme)
                            })
                            Text(text = "Light")
                        }
                        Row {
                            RadioButton(enabled = true, selected = (selectedTheme == 1), onClick = {
                                selectedTheme = 1
                                coroutineScope.launch { viewModel.setTheme(selectedTheme) }
                                onToggleDarkTheme(selectedTheme)
                            })
                            Text(text = "Dark")
                        }
                        Row {
                            RadioButton(enabled = true, selected = (selectedTheme == 2), onClick = {
                                selectedTheme = 2
                                coroutineScope.launch { viewModel.setTheme(selectedTheme) }
                                onToggleDarkTheme(selectedTheme)
                            })
                            Text(text = "System Default")
                        }
                        Row {
                            RadioButton(enabled = true,
                                selected = (selectedTheme == 3),
                                onClick = { selectedTheme = 3 })
                            Text(text = "Midnight")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DataSettingsList(
    metadata: List<Metadata?>,
    viewModel: SettingsViewModel,
) {
    val sharedViewModel: SharedViewModel = viewModel(factory = AppViewModelProvider.Factory)

    Column {
        SettingsListItem(
            settingName = "Update Currency Formats",
            settingSubtext = "Exchange rates are updated monthly from the InfoEuro portal",
            action = {
                metadata.find { it?.infoName == "BASECURRENCYID" }?.infoValue?.toIntOrNull()
                    ?.let { baseCurrencyId ->
                        sharedViewModel.getMonthlyRates()
                    }
            }
        )
    }
}

@RequiresApi(Build.VERSION_CODES.R)
@Composable
fun SecuritySettingsList(
    activity: Activity? = LocalActivity.current,
    context: Context? = LocalContext.current,
) {
    val cryptoManager = remember { CryptoManager() }
    val screenLockManager = remember {
        ScreenLockManager(
            context = context!!,
            cryptoManager = cryptoManager
        )
    }
    val sharedViewModel: SharedViewModel = viewModel(factory = AppViewModelProvider.Factory)

    // Checks if secure screen is enabled
    val isSecureScreenEnabled by sharedViewModel.isSecureScreenEnabled.collectAsState()

    LaunchedEffect(Unit) {
        sharedViewModel.getSecureScreenSetting(context!!)
    }

    // Check if biometric authentication is available on the device
    val isBiometricAvailable = remember { BiometricHelper.isBiometricAvailable(activity) }
    val requireUnlock = remember { mutableStateOf(screenLockManager.isScreenLockEnabled()) }

    BiometricHelper.checkBiometricStatus(context = context)

    // Register biometric authentication launcher
    val biometricLauncher =
        rememberLauncherForActivityResult(contract = BiometricPromptActivityResultContract()) { isSuccess ->
            if (isSuccess) {
                Log.d("TAG", "SecuritySettingsList: Succes")
                requireUnlock.value = !requireUnlock.value
                screenLockManager.saveScreenLockPreference(requireUnlock.value)
            } else {
                Log.d("TAG", "SecuritySettingsList: ERROR")
                //throw Exception("Biometric authentication failed!")
            }
        }

    Column {
        // Set the screen lock, enable setting when biometrics are available
        SettingsToggleListItem(
            settingName = "Require Unlock",
            toggle = requireUnlock.value,
            onToggleChange = { newValue ->
                biometricLauncher.launch(Unit) // Authenticate before toggling
            },
            enabled = isBiometricAvailable
        )

        SettingsListItem(settingName = "Lock when idle", settingSubtext = "", action = {

        })
        SettingsToggleListItem(settingName = "Secure screen",
            settingSubtext = "Hides app contents when switching apps, and blocks screenshots",
            toggle = isSecureScreenEnabled,
            enabled = isBiometricAvailable,
            onToggleChange = { newValue ->
                sharedViewModel.saveSecureScreenSetting(context, newValue)
            })
    }
}

@Composable
fun ImportExportSettingsList(
    viewModel: SettingsViewModel, scope: CoroutineScope = rememberCoroutineScope()
) {
    Column {
    }
}