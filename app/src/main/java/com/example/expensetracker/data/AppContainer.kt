package com.example.expensetracker.data

import android.content.Context
import com.example.expensetracker.data.account.AccountsRepository
import com.example.expensetracker.data.account.OfflineAccountsRepository
import com.example.expensetracker.data.transaction.OfflineTransactionsRepository
import com.example.expensetracker.data.transaction.TransactionsRepository

interface AppContainer {
    val accountsRepository: AccountsRepository
    val transactionsRepository: TransactionsRepository
}

/**
 * [AppContainer] implementation that provides instance of [OfflineItemsRepository]
 */
class AppDataContainer(private val context: Context) : AppContainer {
    /**
     * Implementation for [ItemsRepository]
     */
    override val accountsRepository: AccountsRepository by lazy {
        OfflineAccountsRepository(MMEXDatabase.getDatabase(context).accountDao())
    }
    override val transactionsRepository: TransactionsRepository by lazy {
        OfflineTransactionsRepository(MMEXDatabase.getDatabase(context).transactionDao())
    }
}