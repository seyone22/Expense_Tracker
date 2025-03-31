package com.seyone22.expensetracker

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seyone22.expensetracker.managers.SnackbarManager
import com.seyone22.expensetracker.ui.common.dialogs.DialogAction
import kotlinx.coroutines.launch

abstract class BaseViewModel : ViewModel() {
    protected companion object {
        const val TIMEOUT_MILLIS = 5_000L
    }

    // This state holds the current dialog to be shown
    private val _currentDialog = mutableStateOf<DialogAction?>(null)
    val currentDialog = _currentDialog

    // Method to show the dialog
    fun showDialog(dialogAction: DialogAction) {
        _currentDialog.value = dialogAction
    }

    // Method to dismiss the dialog
    fun dismissDialog() {
        _currentDialog.value = null
    }

    fun showSnackbar(message: String) {
        viewModelScope.launch {
            SnackbarManager.showMessage(message)
        }
    }
}