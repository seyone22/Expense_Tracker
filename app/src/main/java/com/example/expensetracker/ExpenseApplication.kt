package com.example.expensetracker

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.expensetracker.data.AppContainer
import com.example.expensetracker.data.AppDataContainer
import com.example.expensetracker.data.MMEXDatabase

class ExpenseApplication : Application() {
    lateinit var container: AppContainer
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()

        container = AppDataContainer(this)
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
}