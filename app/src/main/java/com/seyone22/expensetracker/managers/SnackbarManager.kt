package com.seyone22.expensetracker.managers

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState

object SnackbarManager {
    private val snackbarHostState = SnackbarHostState()

    val hostState: SnackbarHostState
        get() = snackbarHostState

    suspend fun showMessage(message: String, duration: SnackbarDuration = SnackbarDuration.Short) {
        snackbarHostState.showSnackbar(message = message, duration = duration)
    }

    suspend fun showMessageWithAction(
        message: String, actionLabel: String, duration: SnackbarDuration = SnackbarDuration.Short
    ) {
        snackbarHostState.showSnackbar(message, actionLabel, duration = duration)
    }

    suspend fun showError(message: String) {
        snackbarHostState.showSnackbar(message, actionLabel = "Dismiss")
    }
}
