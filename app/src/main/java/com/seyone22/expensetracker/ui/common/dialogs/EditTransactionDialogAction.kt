package com.seyone22.expensetracker.ui.common.dialogs

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.seyone22.expensetracker.ui.screen.operations.transaction.TransactionDetails
import com.seyone22.expensetracker.ui.screen.operations.transaction.TransactionEntryViewModel
import com.seyone22.expensetracker.ui.screen.operations.transaction.TransactionUiState
import com.seyone22.expensetracker.ui.screen.operations.transaction.composables.TransactionEntryForm
import kotlinx.coroutines.CoroutineScope

class EditTransactionDialogAction(
    private val onEdit: (TransactionDetails) -> Unit = {},
    private val initialTransaction: TransactionDetails,
    private val viewModel: TransactionEntryViewModel,
    private val coroutineScope: CoroutineScope,
    private val transactionUiState: TransactionUiState
) : DialogAction {
    override val title = "Edit Transaction"
    override val message = null

    init {
        Log.d("TAG", "init: ")
        viewModel.updateUiState(
            initialTransaction, null, 0.0
        )
    }

    override fun onConfirm() {
        onEdit(viewModel.transactionUiState.value.transactionDetails)
    }

    override fun onCancel() {
        // Handle cancel action if needed
    }

    override val content: @Composable () -> Unit = {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp, 16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            item {
                TransactionEntryForm(
                    viewModel = viewModel,
                    coroutineScope = coroutineScope,
                    edit = true,
                )
            }
        }
    }

}