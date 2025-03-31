package com.seyone22.expensetracker.managers

import android.content.Context
import androidx.compose.material3.SnackbarDuration
import androidx.work.WorkManager
import com.seyone22.expensetracker.data.model.BillsDepositWithDetails
import com.seyone22.expensetracker.data.model.BudgetEntry
import com.seyone22.expensetracker.data.repository.billsDeposit.BillsDepositsRepository
import com.seyone22.expensetracker.data.repository.budgetEntry.BudgetEntryRepository
import com.seyone22.expensetracker.data.repository.transaction.TransactionsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull

class NotificationStateManager(
    private val transactionRepository: TransactionsRepository,
    private val billsDepositsRepository: BillsDepositsRepository,
    private val budgetRepository: BudgetEntryRepository // Or your appropriate data source
) {
    // LiveState or StateFlow to keep track of notifications
    private val _notifications = MutableStateFlow(NotificationState())
    val notifications: StateFlow<NotificationState> get() = _notifications

    // Initialize on startup (check for overdue transactions, budget alerts, etc.)
    suspend fun checkInitialConditions() {
        // Check for overdue transactions
        val overdueTransactions = billsDepositsRepository.getPastDueBillsDeposits().firstOrNull()

        // Check for budget alerts
        val budgetAlerts = budgetRepository.getAllBudgetEntriesStream().firstOrNull()

        // Update state
        _notifications.value = NotificationState(
            overdueTransactions = overdueTransactions, budgetAlerts = budgetAlerts
        )

        if (!overdueTransactions.isNullOrEmpty()) {
            // Show success Snackbar **after** both operations are completed
            SnackbarManager.showMessageWithAction(
                message = "Bby, you have overdue transactions",
                actionLabel = "View",
                duration = SnackbarDuration.Indefinite,
            )
        }
    }

    // Update notifications (e.g., when a transaction is logged)
    suspend fun updateNotifications() {
        // Query the repository for the latest state of transactions and budgets
        val overdueTransactions = billsDepositsRepository.getPastDueBillsDeposits().firstOrNull()
        val budgetAlerts = budgetRepository.getAllBudgetEntriesStream().firstOrNull()

        // Update state
        _notifications.value = NotificationState(
            overdueTransactions = overdueTransactions, budgetAlerts = budgetAlerts
        )
    }

    // Manually remove a specific notification (e.g., after a transaction is logged)
    fun removeNotification(notification: NotificationType) {
        val currentState = _notifications.value
        val updatedState = when (notification) {
            NotificationType.OverdueTransaction -> currentState.copy(
                overdueTransactions = emptyList()
            )

            NotificationType.BudgetAlert -> currentState.copy(
                budgetAlerts = emptyList()
            )
        }
        _notifications.value = updatedState
    }

    fun nukeAllWorkManagers(context: Context) {
        val workManager = WorkManager.getInstance(context)
        workManager.cancelAllWork() // Cancels all scheduled, running, or enqueued work
        workManager.pruneWork() // Deletes completed work to ensure no old data lingers
    }
}


// Define NotificationState data structure
data class NotificationState(
    val overdueTransactions: List<BillsDepositWithDetails>? = emptyList(),
    val budgetAlerts: List<BudgetEntry>? = emptyList()
)

enum class NotificationType {
    OverdueTransaction, BudgetAlert
}
