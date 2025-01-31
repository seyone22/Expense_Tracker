package com.seyone22.expensetracker.data

import android.content.Context
import com.seyone22.expensetracker.data.repository.account.AccountsRepository
import com.seyone22.expensetracker.data.repository.account.OfflineAccountsRepository
import com.seyone22.expensetracker.data.repository.billsDeposit.BillsDepositsRepository
import com.seyone22.expensetracker.data.repository.billsDeposit.OfflineBillsDepositsRepository
import com.seyone22.expensetracker.data.repository.category.CategoriesRepository
import com.seyone22.expensetracker.data.repository.category.OfflineCategoriesRepository
import com.seyone22.expensetracker.data.repository.currencyFormat.CurrencyFormatsRepository
import com.seyone22.expensetracker.data.repository.currencyFormat.OfflineCurrencyFormatsRepository
import com.seyone22.expensetracker.data.repository.currencyHistory.CurrencyHistoryRepository
import com.seyone22.expensetracker.data.repository.currencyHistory.OfflineCurrencyHistoryRepository
import com.seyone22.expensetracker.data.repository.metadata.MetadataRepository
import com.seyone22.expensetracker.data.repository.metadata.OfflineMetadataRepository
import com.seyone22.expensetracker.data.repository.payee.OfflinePayeesRepository
import com.seyone22.expensetracker.data.repository.payee.PayeesRepository
import com.seyone22.expensetracker.data.repository.report.OfflineReportsRepository
import com.seyone22.expensetracker.data.repository.report.ReportsRepository
import com.seyone22.expensetracker.data.repository.transaction.OfflineTransactionsRepository
import com.seyone22.expensetracker.data.repository.transaction.TransactionsRepository

interface AppContainer {
    val accountsRepository: AccountsRepository
    val transactionsRepository: TransactionsRepository
    val payeesRepository: PayeesRepository
    val categoriesRepository: CategoriesRepository
    val currenciesRepository: CurrencyFormatsRepository
    val metadataRepository: MetadataRepository
    val billsDepositsRepository: BillsDepositsRepository
    val currencyHistoryRepository: CurrencyHistoryRepository
    val reportsRepository: ReportsRepository
}

/**
 * [AppContainer] implementation that provides instance of OfflineItemsRepository
 */
class AppDataContainer(private val context: Context) : AppContainer {
    /**
     * Implementation for ItemsRepository
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
    override val metadataRepository: MetadataRepository by lazy {
        OfflineMetadataRepository(MMEXDatabase.getDatabase(context).metadataDao())
    }
    override val billsDepositsRepository: BillsDepositsRepository by lazy {
        OfflineBillsDepositsRepository(MMEXDatabase.getDatabase(context).billsDepositsDao())
    }
    override val currencyHistoryRepository: CurrencyHistoryRepository by lazy {
        OfflineCurrencyHistoryRepository(MMEXDatabase.getDatabase(context).currencyHistoryDao())
    }
    override val reportsRepository: ReportsRepository by lazy {
        OfflineReportsRepository(MMEXDatabase.getDatabase(context).reportDao())
    }
}