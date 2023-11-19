package com.example.expensetracker.data

import android.content.Context
import com.example.expensetracker.data.account.AccountsRepository
import com.example.expensetracker.data.account.OfflineAccountsRepository
import com.example.expensetracker.data.category.CategoriesRepository
import com.example.expensetracker.data.category.OfflineCategoriesRepository
import com.example.expensetracker.data.currencyFormat.CurrencyFormatsRepository
import com.example.expensetracker.data.currencyFormat.OfflineCurrencyFormatsRepository
import com.example.expensetracker.data.payee.OfflinePayeesRepository
import com.example.expensetracker.data.payee.PayeesRepository
import com.example.expensetracker.data.transaction.OfflineTransactionsRepository
import com.example.expensetracker.data.transaction.TransactionsRepository

interface AppContainer {
    val accountsRepository: AccountsRepository
    val transactionsRepository: TransactionsRepository
    val payeesRepository: PayeesRepository
    val categoriesRepository : CategoriesRepository
    val currenciesRepository : CurrencyFormatsRepository
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
    override val categoriesRepository: CategoriesRepository by lazy {
        OfflineCategoriesRepository(MMEXDatabase.getDatabase(context).categoryDao())
    }
    override val currenciesRepository: CurrencyFormatsRepository by lazy {
        OfflineCurrencyFormatsRepository(MMEXDatabase.getDatabase(context).currencyFormatDao())
    }
}