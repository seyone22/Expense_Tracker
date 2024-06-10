package com.example.expensetracker.ui.screen.operations.entity.category

import android.annotation.SuppressLint
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.expensetracker.R
import com.example.expensetracker.data.model.CategoryDetails
import com.example.expensetracker.data.model.CategoryUiState
import com.example.expensetracker.ui.navigation.NavigationDestination
import com.example.expensetracker.ui.theme.ExpenseTrackerTheme
import kotlinx.coroutines.launch

object CategoryEntryDestination : NavigationDestination {
    override val route = "EnterCategory"
    override val titleRes = R.string.app_name
    override val routeId = 15
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryEntryScreen(
    modifier: Modifier = Modifier,
    navigateBack: () -> Unit = {},
    onNavigateUp: () -> Unit = {},
    canNavigateBack: Boolean = true,
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
                        text = "Create Category",
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
                                navigateBack()
                            }
                        },
                        modifier = modifier.padding(0.dp, 0.dp, 8.dp, 0.dp),
                    ) {
                        Text(text = "Create")
                    }
                }
            )

        }

    ) { padding ->
        CategoryEntryBody(
            modifier = modifier.padding(padding)
        )
    }

}

@Composable
fun CategoryEntryBody(
    categoryUiState: CategoryUiState = CategoryUiState(),
    onCategoryValueChange: (CategoryDetails) -> Unit = {},
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        item {
            CategoryEntryForm(
                categoryDetails = categoryUiState.categoryDetails,
                onValueChange = onCategoryValueChange,
                modifier = Modifier
            )
        }
    }
}

@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryEntryForm(
    categoryDetails: CategoryDetails,
    onValueChange: (CategoryDetails) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val focusManager = LocalFocusManager.current

    Column(
        modifier = modifier
            .focusGroup()
            .padding(0.dp, 8.dp)
    )
    {
        OutlinedTextField(
            modifier = Modifier.padding(0.dp, 8.dp),
            value = categoryDetails.categName,
            onValueChange = { onValueChange(categoryDetails.copy(categName = it)) },
            label = { Text("Category Name *") },
            singleLine = true,
            keyboardActions = KeyboardActions(onDone = { focusManager.moveFocus(FocusDirection.Next) })
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CategoryEntryFormPreview() {
    ExpenseTrackerTheme {
        CategoryEntryScreen()
    }
}