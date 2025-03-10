package com.seyone22.expensetracker.ui.common.dialogs

import androidx.compose.runtime.Composable

class DeleteItemDialogAction(
    private val itemName: String,
    private val onAdd: () -> Unit,
) : DialogAction {
    override val title: String = "Delete $itemName?"
    override val message: String =
        "Are you sure you want to delete $itemName? This action cannot be undone."

    override val content: @Composable () -> Unit = {
    }

    override fun onConfirm() {
        onAdd()
    }

    override fun onCancel() {}
}