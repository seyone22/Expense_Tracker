package com.example.expensetracker.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.expensetracker.data.model.Account
import com.example.expensetracker.data.model.BillsDeposits
import com.example.expensetracker.data.model.Category
import com.example.expensetracker.data.model.CurrencyFormat
import com.example.expensetracker.data.model.Metadata
import com.example.expensetracker.data.model.Payee
import com.example.expensetracker.data.model.Report
import com.example.expensetracker.data.model.Transaction
import com.example.expensetracker.data.repository.account.AccountDao
import com.example.expensetracker.data.repository.billsDeposit.BillsDepositsDao
import com.example.expensetracker.data.repository.category.CategoryDao
import com.example.expensetracker.data.repository.currencyFormat.CurrencyFormatDao
import com.example.expensetracker.data.repository.metadata.MetadataDao
import com.example.expensetracker.data.repository.payee.PayeeDao
import com.example.expensetracker.data.repository.report.ReportDao
import com.example.expensetracker.data.repository.transaction.TransactionDao

@Database(
    entities = [Account::class, Transaction::class, Payee::class, Category::class, CurrencyFormat::class, Metadata::class, BillsDeposits::class, Report::class],
    version = 1,
    exportSchema = true
)
abstract class MMEXDatabase : RoomDatabase() {
    abstract fun accountDao(): AccountDao
    abstract fun transactionDao(): TransactionDao
    abstract fun payeeDao(): PayeeDao
    abstract fun categoryDao(): CategoryDao
    abstract fun currencyFormatDao(): CurrencyFormatDao
    abstract fun metadataDao(): MetadataDao
    abstract fun billsDepositsDao(): BillsDepositsDao
    abstract fun reportDao(): ReportDao

    companion object {
        @Volatile
        private var Instance: MMEXDatabase? = null

        fun getDatabase(context: Context): MMEXDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, MMEXDatabase::class.java, "mmex_database")
                    //.createFromAsset("database/prepopulate_v1_1.db")
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { Instance = it }
            }
        }
    }
}

// When Modifying the database, make sure to modify prepopulate.db as well.