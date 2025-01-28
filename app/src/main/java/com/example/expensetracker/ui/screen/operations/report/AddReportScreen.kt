package com.example.expensetracker.ui.screen.operations.report

import android.annotation.SuppressLint
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.expensetracker.R
import com.example.expensetracker.ui.AppViewModelProvider
import com.example.expensetracker.ui.navigation.NavigationDestination
import kotlinx.coroutines.launch

object ReportEntryDestination : NavigationDestination {
    override val route = "ReportEntry"
    override val titleRes = R.string.app_name
    override val routeId = 12
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportEntryScreen(
    modifier: Modifier = Modifier,
    navigateBack: () -> Unit = {},
    onNavigateUp: () -> Unit = {},
    canNavigateBack: Boolean = true,
    viewModel: AddReportViewModel = viewModel(factory = AppViewModelProvider.Factory),
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
                        text = "Create Report",
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
                                viewModel.saveReport()
                                navigateBack()
                            }
                        },
                        enabled = viewModel.reportUiState.isEntryValid,
                        modifier = modifier.padding(0.dp, 0.dp, 8.dp, 0.dp)
                    ) {
                        Text(text = "Create")
                    }
                }
            )

        }

    ) { paddingValues ->
        ReportEntryBody(
            modifier = modifier.padding(paddingValues),
            onReportValueChange = viewModel::updateUiState,
            reportUiState = viewModel.reportUiState
        )
    }
}

@Composable
fun ReportEntryBody(
    modifier: Modifier = Modifier,
    reportUiState: ReportUiState = ReportUiState(),
    onReportValueChange: (ReportDetails) -> Unit = {},
) {
    LazyColumn(
        modifier = modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        item {
            ReportEntryForm(
                reportDetails = reportUiState.reportDetails,
                onValueChange = onReportValueChange,
                modifier = Modifier,
            )
        }
    }
}

@SuppressLint("UnrememberedMutableState")
@Composable
fun ReportEntryForm(
    modifier: Modifier = Modifier,
    reportDetails: ReportDetails,
    onValueChange: (ReportDetails) -> Unit,
) {
    var active by remember { mutableStateOf(reportDetails.active) }
    val focusManager = LocalFocusManager.current

    Column(
        modifier = modifier
            .focusGroup()
            .padding(0.dp, 8.dp)
    ) {
        OutlinedTextField(
            modifier = Modifier.padding(0.dp, 8.dp),
            value = reportDetails.reportName,
            onValueChange = { onValueChange(reportDetails.copy(reportName = it)) },
            label = { Text("Report Name *") },
            singleLine = true,
            keyboardActions = KeyboardActions(onDone = { focusManager.moveFocus(FocusDirection.Next) })
        )

        OutlinedTextField(
            modifier = Modifier.padding(0.dp, 8.dp),
            value = reportDetails.groupName,
            onValueChange = { onValueChange(reportDetails.copy(groupName = it)) },
            label = { Text("Group Name *") },
            singleLine = true,
            keyboardActions = KeyboardActions(onDone = { focusManager.moveFocus(FocusDirection.Next) })
        )

        Row(
            modifier = Modifier.padding(0.dp, 8.dp),
        ) {
            Checkbox(
                checked = active,
                onCheckedChange = {
                    active = it
                    onValueChange(reportDetails.copy(active = it))
                },
            )
            Text(
                text = "Active",
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.align(CenterVertically)
            )
        }

        OutlinedTextField(
            modifier = Modifier
                .padding(0.dp, 8.dp)
                .height(150.dp), // To make it a text area
            value = reportDetails.sqlContent,
            onValueChange = { onValueChange(reportDetails.copy(sqlContent = it)) },
            label = { Text("SQL Content") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
            singleLine = false,
            keyboardActions = KeyboardActions(onDone = { focusManager.moveFocus(FocusDirection.Next) })
        )

        OutlinedTextField(
            modifier = Modifier
                .padding(0.dp, 8.dp)
                .height(150.dp), // To make it a text area
            value = reportDetails.luaContent,
            onValueChange = { onValueChange(reportDetails.copy(luaContent = it)) },
            label = { Text("Lua Content") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
            singleLine = false,
            keyboardActions = KeyboardActions(onDone = { focusManager.moveFocus(FocusDirection.Next) })
        )

        OutlinedTextField(
            modifier = Modifier
                .padding(0.dp, 8.dp)
                .height(150.dp), // To make it a text area
            value = reportDetails.templateContent,
            onValueChange = { onValueChange(reportDetails.copy(templateContent = it)) },
            label = { Text("Template Content") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
            singleLine = false,
            keyboardActions = KeyboardActions(onDone = { focusManager.moveFocus(FocusDirection.Next) })
        )

        OutlinedTextField(
            modifier = Modifier
                .padding(0.dp, 8.dp)
                .height(150.dp), // To make it a text area
            value = reportDetails.description,
            onValueChange = { onValueChange(reportDetails.copy(description = it)) },
            label = { Text("Description") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
            singleLine = false,
            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() })
        )
    }
}