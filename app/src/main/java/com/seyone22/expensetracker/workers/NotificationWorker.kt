package com.seyone22.expensetracker.workers

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.seyone22.expensetracker.R

class NotificationWorker(
    context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    override fun doWork(): Result {
        val notificationType = inputData.getString("notification_type") ?: return Result.failure()

        when (notificationType) {
            "SCHEDULED_TRANSACTION" -> {
                val transactionId = inputData.getLong("transaction_id", -1L)
                // Get the scheduled transaction details and send a reminder
                sendScheduledTransactionReminder(transactionId)
            }

            "BUDGET_ALERT" -> {
                val categoryId = inputData.getLong("category_id", -1L)
                // Check if the user exceeded the budget for the category and send an alert
                sendBudgetExceedAlert(categoryId)
            }

            "PAST_DUE_TRANSACTION" -> {
                val transactionId = inputData.getLong("transaction_id", -1L)
                // Send a notification for past-due transaction
                sendPastDueTransactionAlert(transactionId)
            }

            else -> {
                return Result.failure()
            }
        }

        return Result.success()
    }

    private fun sendScheduledTransactionReminder(transactionId: Long) {
        // Logic to get the scheduled transaction details and show a reminder
        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notification = NotificationCompat.Builder(applicationContext, "channel_id")
            .setContentTitle("Scheduled Transaction Reminder")
            .setContentText("You have a scheduled transaction due soon.")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(transactionId.toInt(), notification)
    }

    private fun sendBudgetExceedAlert(categoryId: Long) {
        // Logic to check if the budget for the category has been exceeded
        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notification = NotificationCompat.Builder(applicationContext, "channel_id")
            .setContentTitle("Budget Exceeded Alert")
            .setContentText("You have exceeded your budget in this category.")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(categoryId.toInt(), notification)
    }

    private fun sendPastDueTransactionAlert(transactionId: Long) {
        // Logic to notify the user about a past-due scheduled transaction
        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notification = NotificationCompat.Builder(applicationContext, "channel_id")
            .setContentTitle("Past Due Transaction Alert")
            .setContentText("You have a past-due transaction that needs your attention.")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(transactionId.toInt(), notification)
    }
}
