package com.example.expensetracker.ui.common

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

@Composable
fun DeleteConfirmationDialog(
    onDismissRequest: () -> Unit,
    confirmButtonAction: () -> Unit,
    bodyText: String,
) {
    AlertDialog(
        onDismissRequest = { onDismissRequest() },
        confirmButton = {
            TextButton(onClick = { confirmButtonAction(); onDismissRequest() }) {
                Text(text = "Delete")
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismissRequest() }) {
                Text("Cancel")
            }
        },
        title = { Text("Confirm Delete") },
        text = { Text(text = bodyText) }
    )
}

