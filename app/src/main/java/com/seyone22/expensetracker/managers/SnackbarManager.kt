package com.seyone22.expensetracker.managers

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class ToastType {
    INFO, SUCCESS, WARNING, ERROR
}

data class ExpressiveToast(
    val id: Long = System.currentTimeMillis(),
    val title: String,
    val message: String,
    val type: ToastType = ToastType.INFO,
    val actionLabel: String? = null,
    val onAction: (() -> Unit)? = null,
    val durationMs: Long = 4000L
)

object SnackbarManager {
    private val snackbarHostState = SnackbarHostState()
    val hostState: SnackbarHostState get() = snackbarHostState

    private val _activeToast = MutableStateFlow<ExpressiveToast?>(null)
    val activeToast: StateFlow<ExpressiveToast?> = _activeToast.asStateFlow()

    private var dismissJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Main + Job())

    fun showToast(
        title: String,
        message: String,
        type: ToastType = ToastType.INFO,
        actionLabel: String? = null,
        durationMs: Long = 4000L,
        onAction: (() -> Unit)? = null
    ) {
        dismissJob?.cancel()
        val toast = ExpressiveToast(
            title = title,
            message = message,
            type = type,
            actionLabel = actionLabel,
            durationMs = durationMs,
            onAction = onAction
        )
        _activeToast.value = toast

        dismissJob = scope.launch {
            delay(durationMs)
            if (_activeToast.value?.id == toast.id) {
                _activeToast.value = null
            }
        }
    }

    fun dismissToast() {
        dismissJob?.cancel()
        _activeToast.value = null
    }

    // Keep compatibility with showMessage
    suspend fun showMessage(message: String, duration: SnackbarDuration = SnackbarDuration.Short) {
        val durationMs = when (duration) {
            SnackbarDuration.Short -> 3000L
            SnackbarDuration.Long -> 6000L
            SnackbarDuration.Indefinite -> 10000L
        }
        showToast(
            title = "Info",
            message = message,
            type = ToastType.INFO,
            durationMs = durationMs
        )
        snackbarHostState.showSnackbar(message = message, duration = duration)
    }

    suspend fun showMessageWithAction(
        message: String, actionLabel: String, duration: SnackbarDuration = SnackbarDuration.Short
    ) {
        val durationMs = when (duration) {
            SnackbarDuration.Short -> 4000L
            SnackbarDuration.Long -> 7000L
            SnackbarDuration.Indefinite -> 15000L
        }
        showToast(
            title = "Notice",
            message = message,
            type = ToastType.SUCCESS,
            actionLabel = actionLabel,
            durationMs = durationMs,
            onAction = {
                // Callers can handle action
            }
        )
        snackbarHostState.showSnackbar(message, actionLabel, duration = duration)
    }

    suspend fun showError(message: String) {
        showToast(
            title = "Error",
            message = message,
            type = ToastType.ERROR,
            durationMs = 5000L
        )
        snackbarHostState.showSnackbar(message, actionLabel = "Dismiss")
    }
}
