package com.seyone22.expensetracker.ui.common.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.seyone22.expensetracker.SharedViewModel
import com.seyone22.expensetracker.data.model.Account
import com.seyone22.expensetracker.ui.AppViewModelProvider

class MoveTransactionDialogAction(
    private val onAdd: (Int) -> Unit,
) : DialogAction {
    override val title: String = "Move Transaction"
    override val message: String = "Where do you want to move this transaction?"

    private var accountSelected by mutableStateOf(Account())
    private var accountExpanded by mutableStateOf(false)


    @OptIn(ExperimentalMaterial3Api::class)
    override val content: @Composable () -> Unit = {
        Column(
            modifier = Modifier.padding(16.dp, 0.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            val sharedViewModel: SharedViewModel = viewModel(factory = AppViewModelProvider.Factory)
            val accounts by sharedViewModel.accountsFlow.collectAsState(initial = listOf())

            val focusManager = LocalFocusManager.current

            // New Account select dropdown
            ExposedDropdownMenuBox(
                expanded = accountExpanded, onExpandedChange = { accountExpanded = it }) {
                OutlinedTextField(
                    modifier = Modifier
                        .padding(0.dp, 8.dp)
                        .menuAnchor(MenuAnchorType.PrimaryEditable, true),
                    value = accountSelected.accountName,
                    readOnly = true,
                    onValueChange = {},
                    label = { Text("Move to") },
                    singleLine = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = accountExpanded) },
                )

                ExposedDropdownMenu(
                    expanded = accountExpanded,
                    onDismissRequest = { accountExpanded = false },
                ) {
                    accounts.forEach { account ->
                        DropdownMenuItem(text = { Text(account.accountName) }, onClick = {
                            accountSelected = account
                            accountExpanded = false
                        })
                    }
                }
            }
        }
    }

    override fun onConfirm() {
        onAdd(accountSelected.accountId)
    }

    override fun onCancel() {}
}