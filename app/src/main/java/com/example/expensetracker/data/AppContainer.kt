package com.example.expensetracker.data

import android.content.Context
import com.example.expensetracker.data.account.AccountsRepository
import com.example.expensetracker.data.account.OfflineAccountsRepository
import com.example.expensetracker.data.payee.OfflinePayeesRepository
import com.example.expensetracker.data.payee.PayeesRepository
import com.example.expensetracker.data.transaction.OfflineTransactionsRepository
import com.example.expensetracker.data.transaction.TransactionsRepository
import com.example.expensetracker.model.Payee

interface AppContainer {
    val accountsRepository: AccountsRepository
    val transactionsRepository: TransactionsRepository
    val payeesRepository: PayeesRepository
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
    override val payeesRepository: PayeesRepository by lazy {
        OfflinePayeesRepository(MMEXDatabase.getDatabase(context).payeeDao())
    }
}