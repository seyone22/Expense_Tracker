package com.example.expensetracker.ui.common

import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable

@Composable
fun ExpenseFAB(
    navigateToScreen: (screen: String) -> Unit,
) {
    FloatingActionButton(onClick = {
        navigateToScreen("TransactionEntry")
    }) {
        Icon(Icons.Outlined.Edit, "Add")
    }
}