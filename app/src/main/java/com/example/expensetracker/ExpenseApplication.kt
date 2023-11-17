package com.example.expensetracker

import android.app.Application
import com.example.expensetracker.data.AppContainer
import com.example.expensetracker.data.AppDataContainer

class ExpenseApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }
}