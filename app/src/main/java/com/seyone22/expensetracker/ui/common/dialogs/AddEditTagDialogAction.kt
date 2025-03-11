package com.seyone22.expensetracker.ui.common.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import com.seyone22.expensetracker.data.model.Tag


class AddEditTagDialogAction(
    private val onAdd: (Tag) -> Unit,
    private val onEdit: (Tag) -> Unit,
    initialEntry: Tag? = null,
) : DialogAction {
    private val existingEntry = initialEntry

    override val title = if (initialEntry == null) "Add Tag" else "Edit Tag"
    override val message = null

    private var tagSelected by mutableStateOf(
        initialEntry ?: Tag(
            tagName = "", active = true
        )
    )

    override val content: @Composable () -> Unit = {
        val focusManager = LocalFocusManager.current

        Column(
            modifier = Modifier.padding(16.dp, 0.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            OutlinedTextField(
                modifier = Modifier.padding(0.dp, 8.dp),
                value = tagSelected.tagName,
                onValueChange = {
                    tagSelected = tagSelected.copy(tagName = it)
                },
                label = { Text("Tag Name*") },
                singleLine = true,
                keyboardActions = KeyboardActions(onDone = {
                    focusManager.moveFocus(
                        FocusDirection.Next
                    )
                })
            )

            Row(
                modifier = Modifier.padding(0.dp, 8.dp)
            ) {
                Checkbox(
                    checked = tagSelected.active ?: true,
                    onCheckedChange = {
                        tagSelected = tagSelected.copy(active = it)
                    },
                )
                Text(
                    text = "Active",
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }
        }
    }

    override fun onConfirm() {
        if (tagSelected.tagName.isBlank()) return // Prevent empty tags
        if (existingEntry == null) {
            onAdd(tagSelected)
        } else {
            onEdit(tagSelected)
        }
    }

    override fun onCancel() {
        // No-op
    }
}