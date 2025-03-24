package com.seyone22.expensetracker.ui.common.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.seyone22.expensetracker.data.model.Payee


class AddEditPayeeDialogAction(
    private val onAdd: (Payee) -> Unit,
    private val onEdit: (Payee) -> Unit,
    initialEntry: Payee? = null,
) : DialogAction {
    private val existingEntry = initialEntry

    override val title = if (initialEntry == null) "Add Payee" else "Edit Payee"
    override val message = null

    private var payeeSelected by mutableStateOf(
        initialEntry ?: Payee(
        )
    )

    override val content: @Composable () -> Unit = {
        val focusManager = LocalFocusManager.current

        Column(
            modifier = Modifier.padding(8.dp, 0.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            OutlinedTextField(
                modifier = Modifier,
                value = payeeSelected.payeeName,
                onValueChange = {
                    payeeSelected = payeeSelected.copy(payeeName = it)
                },
                label = { Text("Payee Name *") },
                singleLine = true,
                keyboardActions = KeyboardActions(onDone = {
                    focusManager.moveFocus(
                        FocusDirection.Next
                    )
                })
            )
            // We're obviously not including last used category -_-
            OutlinedTextField(
                modifier = Modifier,
                value = payeeSelected.number,
                onValueChange = {
                    payeeSelected = payeeSelected.copy(number = it)
                },
                label = { Text("Reference Number") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                keyboardActions = KeyboardActions(onDone = {
                    focusManager.moveFocus(
                        FocusDirection.Next
                    )
                })
            )
            OutlinedTextField(
                modifier = Modifier,
                value = payeeSelected.website,
                onValueChange = {
                    payeeSelected = payeeSelected.copy(website = it)
                },
                label = { Text("Website") },
                singleLine = true,
                keyboardActions = KeyboardActions(onDone = {
                    focusManager.moveFocus(
                        FocusDirection.Next
                    )
                })
            )
            OutlinedTextField(
                modifier = Modifier,
                value = payeeSelected.notes,
                onValueChange = {
                    payeeSelected = payeeSelected.copy(notes = it)
                },
                label = { Text("Notes") },
                singleLine = true,
                keyboardActions = KeyboardActions(onDone = {
                    focusManager.moveFocus(
                        FocusDirection.Next
                    )
                })
            )
            Row(
                modifier = Modifier
            ) {
                Checkbox(
                    checked = payeeSelected.active == 1,
                    onCheckedChange = {
                        payeeSelected = payeeSelected.copy(active = if (it) 1 else 0)
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
        if (payeeSelected.payeeName.isBlank()) return // Prevent empty payees
        if (existingEntry == null) {
            onAdd(payeeSelected)
        } else {
            onEdit(payeeSelected)
        }
    }

    override fun onCancel() {
        // No-op
    }
}