package com.example.expensetracker.workers

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.expensetracker.R

class RecurringTransactionWorker(private val context: Context, workerParams: WorkerParameters): Worker(context, workerParams) {
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun doWork(): Result {
        sendNotification()
        return Result.success()
    }

    @SuppressLint("MissingPermission")
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun sendNotification() {
        val builder = NotificationCompat.Builder(applicationContext, "channel_id")
            .setContentTitle("Expense Notification!")
            .setContentText("This is a notification from Work Manager!")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationCompat.PRIORITY_MAX)

        val notificationManager = NotificationManagerCompat.from(applicationContext)

        // Permission granted, send the notification
        notificationManager.notify(123, builder.build())
    }

}