package com.seyone22.expensetracker.workers

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.seyone22.expensetracker.ExpenseApplication
import com.seyone22.expensetracker.R
import com.seyone22.expensetracker.receivers.NotificationActionReceiver
import kotlinx.coroutines.flow.firstOrNull
import java.time.LocalDate

class NotificationWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        Log.d("NotificationWorker", "Checking database for scheduled transaction reminders...")
        try {
            val container = (applicationContext as ExpenseApplication).container
            val billsDepositsRepository = container.billsDepositsRepository

            val allReminders = billsDepositsRepository.getAllTransactionsStream().firstOrNull() ?: emptyList()
            val today = LocalDate.now()

            // 1. Filter for items that are past due and not auto-executed
            val pastDueItems = allReminders.filter { item ->
                val nextDateStr = item.NEXTOCCURRENCEDATE
                if (!nextDateStr.isNullOrBlank()) {
                    val nextDate = LocalDate.parse(nextDateStr)
                    nextDate.isBefore(today) && (item.NUMOCCURRENCES ?: 0) > 0
                } else false
            }

            // 2. Filter for items due today or in the next 3 days
            val dueSoonItems = allReminders.filter { item ->
                val nextDateStr = item.NEXTOCCURRENCEDATE
                if (!nextDateStr.isNullOrBlank()) {
                    val nextDate = LocalDate.parse(nextDateStr)
                    (nextDate.isEqual(today) || (nextDate.isAfter(today) && nextDate.isBefore(today.plusDays(4)))) && (item.NUMOCCURRENCES ?: 0) > 0
                } else false
            }

            val notificationManager =
                applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            // Post notifications for past due items
            pastDueItems.forEach { item ->
                val title = "Bill Past Due Alert"
                val description = "${item.payeeName ?: "Transfer"} of amount ${item.TRANSAMOUNT} was due on ${item.NEXTOCCURRENCEDATE}."

                // Action intents
                val payIntent = Intent(applicationContext, NotificationActionReceiver::class.java).apply {
                    action = "com.seyone22.expensetracker.ACTION_PAY_NOW"
                    putExtra("EXTRA_BD_ID", item.BDID)
                }
                val payPendingIntent = PendingIntent.getBroadcast(
                    applicationContext,
                    item.BDID,
                    payIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

                val skipIntent = Intent(applicationContext, NotificationActionReceiver::class.java).apply {
                    action = "com.seyone22.expensetracker.ACTION_SKIP"
                    putExtra("EXTRA_BD_ID", item.BDID)
                }
                val skipPendingIntent = PendingIntent.getBroadcast(
                    applicationContext,
                    item.BDID + 5000,
                    skipIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

                val notification = NotificationCompat.Builder(applicationContext, "channel_id")
                    .setContentTitle(title)
                    .setContentText(description)
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setCategory(NotificationCompat.CATEGORY_REMINDER)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .addAction(R.drawable.ic_launcher_foreground, "Pay Now", payPendingIntent)
                    .addAction(R.drawable.ic_launcher_foreground, "Skip", skipPendingIntent)
                    .setAutoCancel(true)
                    .build()
                notificationManager.notify(item.BDID, notification)
            }

            // Post notifications for upcoming items
            dueSoonItems.forEach { item ->
                // Skip if already notified under past due
                if (pastDueItems.any { it.BDID == item.BDID }) return@forEach

                val title = "Upcoming Bill Reminder"
                val description = "${item.payeeName ?: "Transfer"} of amount ${item.TRANSAMOUNT} is due on ${item.NEXTOCCURRENCEDATE}."

                val payIntent = Intent(applicationContext, NotificationActionReceiver::class.java).apply {
                    action = "com.seyone22.expensetracker.ACTION_PAY_NOW"
                    putExtra("EXTRA_BD_ID", item.BDID)
                }
                val payPendingIntent = PendingIntent.getBroadcast(
                    applicationContext,
                    item.BDID + 10000,
                    payIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

                val skipIntent = Intent(applicationContext, NotificationActionReceiver::class.java).apply {
                    action = "com.seyone22.expensetracker.ACTION_SKIP"
                    putExtra("EXTRA_BD_ID", item.BDID)
                }
                val skipPendingIntent = PendingIntent.getBroadcast(
                    applicationContext,
                    item.BDID + 15000,
                    skipIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

                val notification = NotificationCompat.Builder(applicationContext, "channel_id")
                    .setContentTitle(title)
                    .setContentText(description)
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setCategory(NotificationCompat.CATEGORY_REMINDER)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .addAction(R.drawable.ic_launcher_foreground, "Pay Now", payPendingIntent)
                    .addAction(R.drawable.ic_launcher_foreground, "Skip", skipPendingIntent)
                    .setAutoCancel(true)
                    .build()
                notificationManager.notify(item.BDID + 10000, notification)
            }

        } catch (e: Exception) {
            Log.e("NotificationWorker", "Error running notification checker", e)
            return Result.failure()
        }

        return Result.success()
    }
}
