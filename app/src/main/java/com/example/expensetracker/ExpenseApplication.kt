package com.example.expensetracker

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.expensetracker.data.AppContainer
import com.example.expensetracker.data.AppDataContainer
import com.example.expensetracker.data.MMEXDatabase
import dagger.hilt.android.HiltAndroidApp

private const val BASECURRENCYIDT_PREFERENCE_NAME = "baseCurrencyId"
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = BASECURRENCYIDT_PREFERENCE_NAME
)

@HiltAndroidApp
class ExpenseApplication : Application() {

    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()

        val metadataDao = MMEXDatabase.getDatabase(this).metadataDao()
        val baseCurrencyId = metadataDao.getMetadataByName("BASECURRENCYID") // Replace with your actual method

        container = AppDataContainer(this)
    }
}