package com.seyone22.expensetracker.ui.common.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
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
import com.seyone22.expensetracker.data.model.CurrencyFormat


class AddEditCurrencyDialogAction(
    private val onAdd: (CurrencyFormat) -> Unit,
    private val onEdit: (CurrencyFormat) -> Unit,
    initialEntry: CurrencyFormat? = null,
) : DialogAction {
    private val existingEntry = initialEntry

    override val title = if (initialEntry == null) "Add Currency Format" else "Edit Currency Format"
    override val message = null

    private var currencyFormatSelected by mutableStateOf(
        initialEntry ?: CurrencyFormat(
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
                value = currencyFormatSelected.currencyName,
                onValueChange = {
                    currencyFormatSelected = currencyFormatSelected.copy(currencyName = it)
                },
                label = { Text("Currency Name *") },
                singleLine = true,
                keyboardActions = KeyboardActions(onDone = {
                    focusManager.moveFocus(
                        FocusDirection.Next
                    )
                })
            )
            // We're obviously not including last used category -_-
            OutlinedTextField(
                modifier = Modifier.padding(0.dp, 8.dp),
                value = currencyFormatSelected.currency_symbol,
                onValueChange = {
                    currencyFormatSelected = currencyFormatSelected.copy(currency_symbol = it)
                },
                label = { Text("Currency Symbol") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                keyboardActions = KeyboardActions(onDone = {
                    focusManager.moveFocus(
                        FocusDirection.Next
                    )
                })
            )
            OutlinedTextField(
                modifier = Modifier.padding(0.dp, 8.dp),
                value = currencyFormatSelected.currency_type,
                onValueChange = {
                    currencyFormatSelected = currencyFormatSelected.copy(currency_type = it)
                },
                label = { Text("Currency Type") },
                singleLine = true,
                keyboardActions = KeyboardActions(onDone = {
                    focusManager.moveFocus(
                        FocusDirection.Next
                    )
                })
            )
            OutlinedTextField(
                modifier = Modifier.padding(0.dp, 8.dp),
                value = currencyFormatSelected.pfx_symbol,
                onValueChange = {
                    currencyFormatSelected = currencyFormatSelected.copy(pfx_symbol = it)
                },
                label = { Text("Prefix Symbol") },
                singleLine = true,
                keyboardActions = KeyboardActions(onDone = {
                    focusManager.moveFocus(
                        FocusDirection.Next
                    )
                })
            )
            OutlinedTextField(
                modifier = Modifier.padding(0.dp, 8.dp),
                value = currencyFormatSelected.sfx_symbol,
                onValueChange = {
                    currencyFormatSelected = currencyFormatSelected.copy(sfx_symbol = it)
                },
                label = { Text("Suffix Symbol") },
                singleLine = true,
                keyboardActions = KeyboardActions(onDone = {
                    focusManager.moveFocus(
                        FocusDirection.Next
                    )
                })
            )
        }
    }

    override fun onConfirm() {
        if (currencyFormatSelected.currencyName.isBlank()) return // Prevent empty currencyFormats
        if (existingEntry == null) {
            onAdd(currencyFormatSelected)
        } else {
            onEdit(currencyFormatSelected)
        }
    }

    override fun onCancel() {
        // No-op
    }
}