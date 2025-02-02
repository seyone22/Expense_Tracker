package com.seyone22.expensetracker.ui.common.dialogs

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun GenericDialog(
    dialogAction: DialogAction,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = dialogAction.title) },
        text = {
            Column {
                Text(text = dialogAction.message)
                dialogAction.content() // Render the dynamic content here
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    dialogAction.onConfirm()
                    onDismiss()
                }
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            Button(
                onClick = {
                    dialogAction.onCancel()
                    onDismiss()
                }
            ) {
                Text("Cancel")
            }
        }
    )
}
