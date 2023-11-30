package com.example.expensetracker.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.expensetracker.data.account.AccountDao
import com.example.expensetracker.data.category.CategoryDao
import com.example.expensetracker.data.currencyFormat.CurrencyFormatDao
import com.example.expensetracker.data.metadata.MetadataDao
import com.example.expensetracker.data.payee.PayeeDao
import com.example.expensetracker.data.transaction.TransactionDao
import com.example.expensetracker.model.Account
import com.example.expensetracker.model.Asset
import com.example.expensetracker.model.Attachment
import com.example.expensetracker.model.BillsDeposit
import com.example.expensetracker.model.BudgetSplitTransaction
import com.example.expensetracker.model.BudgetTable
import com.example.expensetracker.model.BudgetYear
import com.example.expensetracker.model.Category
import com.example.expensetracker.model.CurrencyFormat
import com.example.expensetracker.model.CurrencyHistory
import com.example.expensetracker.model.CustomField
import com.example.expensetracker.model.CustomFieldData
import com.example.expensetracker.model.Payee
import com.example.expensetracker.model.Transaction
import com.example.expensetracker.model.Metadata
import com.example.expensetracker.model.Report
import com.example.expensetracker.model.ShareInfo
import com.example.expensetracker.model.SplitTransaction
import com.example.expensetracker.model.Stock
import com.example.expensetracker.model.StockHistory
import com.example.expensetracker.model.TransactionLink

@Database(
    entities = [Account::class, Asset::class, Attachment::class, BillsDeposit::class, BudgetSplitTransaction::class, BudgetTable::class, BudgetYear::class, Category::class, Transaction::class, CurrencyFormat::class, CurrencyHistory::class, CustomFieldData::class, CustomField::class, Metadata::class, Payee::class, Report::class, ShareInfo::class, SplitTransaction::class, StockHistory::class, Stock::class, TransactionLink::class],
    version = 19,
    exportSchema = false
)
abstract class MMEXDatabase : RoomDatabase() {
    abstract fun accountDao(): AccountDao
    abstract fun transactionDao(): TransactionDao
    abstract fun payeeDao(): PayeeDao
    abstract fun categoryDao(): CategoryDao
    abstract fun currencyFormatDao(): CurrencyFormatDao
    abstract fun metadataDao(): MetadataDao

    companion object {
        @Volatile
        private var Instance: MMEXDatabase? = null

        fun getDatabase(context: Context): MMEXDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, MMEXDatabase::class.java, "mmex_database_v19")
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { Instance = it }
            }
        }
    }
}

// When Modifying the database, make sure to modify prepopulate.db as well.