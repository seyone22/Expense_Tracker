package com.seyone22.expensetracker.ui.common.dialogs

import androidx.compose.runtime.Composable

interface DialogAction {
    val title: String
    val message: String
    val content: @Composable () -> Unit // Content for the body of the dialog
    fun onConfirm() // Action when user confirms
    fun onCancel() // Action when user cancels
}
