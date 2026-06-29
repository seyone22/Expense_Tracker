package com.seyone22.expensetracker

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.seyone22.expensetracker.data.AppContainer
import com.seyone22.expensetracker.data.AppDataContainer
import com.seyone22.expensetracker.workers.ExchangeRateWorker
import com.seyone22.expensetracker.workers.NotificationWorker
import java.util.concurrent.TimeUnit

class ExpenseApplication : Application() {
    lateinit var container: AppContainer
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        container = AppDataContainer(this)

        scheduleBackgroundWorkers()
    }

    private fun createNotificationChannel() {
        val name = "Channel Name"
        val descriptionText = "Channel Description"
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel("channel_id", name, importance).apply {
            description = descriptionText
        }
        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun scheduleBackgroundWorkers() {
        val workManager = WorkManager.getInstance(this)

        // 1. Schedule NotificationWorker to check daily
        val notificationRequest = PeriodicWorkRequestBuilder<NotificationWorker>(
            24, TimeUnit.HOURS
        ).build()

        workManager.enqueueUniquePeriodicWork(
            "NotificationPeriodicCheck",
            ExistingPeriodicWorkPolicy.KEEP,
            notificationRequest
        )

        // 2. Schedule ExchangeRateWorker to run weekly
        val exchangeRateRequest = PeriodicWorkRequestBuilder<ExchangeRateWorker>(
            7, TimeUnit.DAYS
        ).build()

        workManager.enqueueUniquePeriodicWork(
            "ExchangeRatePeriodicUpdate",
            ExistingPeriodicWorkPolicy.KEEP,
            exchangeRateRequest
        )
    }
}