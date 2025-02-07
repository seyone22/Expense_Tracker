package com.seyone22.expensetracker.utils

import androidx.compose.material3.SnackbarHostState

object SnackbarManager {
    private val snackbarHostState = SnackbarHostState()

    val hostState: SnackbarHostState
        get() = snackbarHostState

    suspend fun showMessage(message: String) {
        snackbarHostState.showSnackbar(message)
    }

    suspend fun showError(message: String) {
        snackbarHostState.showSnackbar(message, actionLabel = "Dismiss")
    }
}
