package com.example.expensetracker.ui.common

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringArrayResource
import com.example.expensetracker.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseTopBar(
    selectedActivity: Int,
    navBarAction: () -> Unit,
    hasNavBarAction : Boolean = true,
    navigateToSettings: () -> Unit
    ) {
    //Title string for header elements
    val titleString : String
    if (selectedActivity in 0..4) {
        titleString = stringArrayResource(id = R.array.activities)[selectedActivity]
    } else {
        titleString = "Settings"
    }

    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
        ),
        title = {
            Text(titleString)
        },
        navigationIcon = {
            IconButton(onClick = {
                navigateToSettings()
            }) {
                Icon(
                    imageVector = Icons.Filled.Settings,
                    contentDescription = "Settings"
                )
            }
        },
        actions = {
            if (hasNavBarAction) {
                IconButton(onClick = { navBarAction() }) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Add Item"
                    )
                }
            }
        }
    )
}