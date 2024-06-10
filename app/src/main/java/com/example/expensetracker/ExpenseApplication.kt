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

private const val BASECURRENCYIDT_PREFERENCE_NAME = "baseCurrencyId"
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = BASECURRENCYIDT_PREFERENCE_NAME
)

class ExpenseApplication : Application() {
    lateinit var container: AppContainer
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()

        val metadataDao = MMEXDatabase.getDatabase(this).metadataDao()
        val baseCurrencyId =
            metadataDao.getMetadataByName("BASECURRENCYID") // Replace with your actual method

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