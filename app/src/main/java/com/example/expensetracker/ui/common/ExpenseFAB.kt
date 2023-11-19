package com.example.expensetracker.ui.common

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.expensetracker.activitiesAndIcons
import com.example.expensetracker.ui.transaction.TransactionEntryScreen

@Composable
fun ExpenseFAB(
    navigateToScreen: (screen: String) -> Unit,
) {
    var showDialog by remember { mutableStateOf(false) }

    FloatingActionButton(onClick = {
        showDialog = showDialog.not()
    }) {
        Icon(Icons.Outlined.Edit, "Add")
    }

    if (showDialog) {
        TransactionEntryScreen(
            onDismissRequest = { showDialog = !showDialog },
            onConfirmation = {
                showDialog = !showDialog
                navigateToScreen(activitiesAndIcons[0].activity)
            }
        )
    }
}