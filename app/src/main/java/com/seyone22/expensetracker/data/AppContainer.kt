package com.seyone22.expensetracker.data

import android.content.Context
import com.seyone22.expensetracker.data.repository.account.AccountsRepository
import com.seyone22.expensetracker.data.repository.account.OfflineAccountsRepository
import com.seyone22.expensetracker.data.repository.billsDeposit.BillsDepositsRepository
import com.seyone22.expensetracker.data.repository.billsDeposit.OfflineBillsDepositsRepository
import com.seyone22.expensetracker.data.repository.budgetEntry.BudgetEntryRepository
import com.seyone22.expensetracker.data.repository.budgetEntry.OfflineBudgetEntryRepository
import com.seyone22.expensetracker.data.repository.budgetYear.BudgetYearRepository
import com.seyone22.expensetracker.data.repository.budgetYear.OfflineBudgetYearRepository
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
import com.seyone22.expensetracker.data.repository.tag.OfflineTagsRepository
import com.seyone22.expensetracker.data.repository.tag.TagsRepository
import com.seyone22.expensetracker.data.repository.transaction.OfflineTransactionsRepository
import com.seyone22.expensetracker.data.repository.transaction.TransactionsRepository
import com.seyone22.expensetracker.data.repository.splitTransaction.SplitTransactionsRepository
import com.seyone22.expensetracker.data.repository.splitTransaction.OfflineSplitTransactionsRepository
import com.seyone22.expensetracker.data.repository.attachment.AttachmentsRepository
import com.seyone22.expensetracker.data.repository.attachment.OfflineAttachmentsRepository
import com.seyone22.expensetracker.data.repository.stock.StockRepository
import com.seyone22.expensetracker.data.repository.stock.OfflineStockRepository
import com.seyone22.expensetracker.data.repository.stockHistory.StockHistoryRepository
import com.seyone22.expensetracker.data.repository.stockHistory.OfflineStockHistoryRepository
import com.seyone22.expensetracker.data.repository.transLink.TransLinkRepository
import com.seyone22.expensetracker.data.repository.transLink.OfflineTransLinkRepository
import com.seyone22.expensetracker.data.repository.shareInfo.ShareInfoRepository
import com.seyone22.expensetracker.data.repository.shareInfo.OfflineShareInfoRepository

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
    val budgetEntryRepository: BudgetEntryRepository
    val budgetYearRepository: BudgetYearRepository
    val tagsRepository: TagsRepository
    val splitTransactionsRepository: SplitTransactionsRepository
    val attachmentsRepository: AttachmentsRepository
    val stockRepository: StockRepository
    val stockHistoryRepository: StockHistoryRepository
    val transLinkRepository: TransLinkRepository
    val shareInfoRepository: ShareInfoRepository
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
    override val budgetEntryRepository: BudgetEntryRepository by lazy {
        OfflineBudgetEntryRepository(MMEXDatabase.getDatabase(context).budgetEntryDao())
    }
    override val budgetYearRepository: BudgetYearRepository by lazy {
        OfflineBudgetYearRepository(MMEXDatabase.getDatabase(context).budgetYearDao())
    }
    override val tagsRepository: TagsRepository by lazy {
        OfflineTagsRepository(MMEXDatabase.getDatabase(context).tagDao())
    }
    override val splitTransactionsRepository: SplitTransactionsRepository by lazy {
        OfflineSplitTransactionsRepository(MMEXDatabase.getDatabase(context).splitTransactionDao())
    }
    override val attachmentsRepository: AttachmentsRepository by lazy {
        OfflineAttachmentsRepository(MMEXDatabase.getDatabase(context).attachmentDao())
    }
    override val stockRepository: StockRepository by lazy {
        OfflineStockRepository(MMEXDatabase.getDatabase(context).stockDao())
    }
    override val stockHistoryRepository: StockHistoryRepository by lazy {
        OfflineStockHistoryRepository(MMEXDatabase.getDatabase(context).stockHistoryDao())
    }
    override val transLinkRepository: TransLinkRepository by lazy {
        OfflineTransLinkRepository(MMEXDatabase.getDatabase(context).transLinkDao())
    }
    override val shareInfoRepository: ShareInfoRepository by lazy {
        OfflineShareInfoRepository(MMEXDatabase.getDatabase(context).shareInfoDao())
    }
}