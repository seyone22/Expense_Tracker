package com.example.expensetracker.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.expensetracker.data.model.Account
import com.example.expensetracker.data.model.BillsDeposits
import com.example.expensetracker.data.model.BudgetEntry
import com.example.expensetracker.data.model.BudgetYear
import com.example.expensetracker.data.model.Category
import com.example.expensetracker.data.model.CurrencyFormat
import com.example.expensetracker.data.model.CurrencyHistory
import com.example.expensetracker.data.model.Metadata
import com.example.expensetracker.data.model.Payee
import com.example.expensetracker.data.model.Report
import com.example.expensetracker.data.model.Tag
import com.example.expensetracker.data.model.TagLink
import com.example.expensetracker.data.model.Transaction
import com.example.expensetracker.data.repository.account.AccountDao
import com.example.expensetracker.data.repository.billsDeposit.BillsDepositsDao
import com.example.expensetracker.data.repository.budgetEntry.BudgetEntryDao
import com.example.expensetracker.data.repository.budgetYear.BudgetYearDao
import com.example.expensetracker.data.repository.category.CategoryDao
import com.example.expensetracker.data.repository.currencyFormat.CurrencyFormatDao
import com.example.expensetracker.data.repository.currencyHistory.CurrencyHistoryDao
import com.example.expensetracker.data.repository.metadata.MetadataDao
import com.example.expensetracker.data.repository.payee.PayeeDao
import com.example.expensetracker.data.repository.report.ReportDao
import com.example.expensetracker.data.repository.tag.TagDao
import com.example.expensetracker.data.repository.tagLink.TagLinkDao
import com.example.expensetracker.data.repository.transaction.TransactionDao

@Database(
    entities = [Account::class, Transaction::class, Payee::class, Category::class, CurrencyFormat::class, Metadata::class, BillsDeposits::class, Report::class, CurrencyHistory::class, Tag::class, TagLink::class, BudgetEntry::class, BudgetYear::class],
    version = 4,
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
    abstract fun currencyHistoryDao(): CurrencyHistoryDao
    abstract fun tagDao(): TagDao
    abstract fun tagLinkDao(): TagLinkDao
    abstract fun budgetEntryDao(): BudgetEntryDao
    abstract fun budgetYearDao(): BudgetYearDao

    companion object {
        @Volatile
        private var Instance: MMEXDatabase? = null

        fun getDatabase(context: Context): MMEXDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, MMEXDatabase::class.java, "mmex_database")
                    //.createFromAsset("database/prepopulate_v1_1.db")
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_4)
                    .build()
                    .also { Instance = it }
            }
        }

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS CURRENCYHISTORY_V1 (
                        currHistId INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        currencyId INTEGER NOT NULL,
                        currDate TEXT NOT NULL,
                        currValue REAL NOT NULL,
                        currUpdType INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
            }
        }

        // Migrate to Version 4 (adds tags and budgets)
        private val MIGRATION_2_4 = object : Migration(2, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Create TAGS_V1 table
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS TAGS_V1 (
                        tagId INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        tagName TEXT COLLATE NOCASE NOT NULL UNIQUE,
                        active INTEGER
                    )
                    """.trimIndent()
                )

                // Create TAGLINK_V1 table
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS TAGLINK_V1 (
                        tagLinkId INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        refType TEXT NOT NULL,
                        refId INTEGER NOT NULL,
                        tagId INTEGER NOT NULL,
                        FOREIGN KEY (tagId) REFERENCES TAGS_V1(tagId),
                        UNIQUE(refType, refId, tagId)
                    )
                    """.trimIndent()
                )

                // Create the index as expected by Room
                db.execSQL(
                    """
                        CREATE UNIQUE INDEX IF NOT EXISTS IDX_TAGLINK ON TAGLINK_V1 (refType, refId, tagId)
                    """.trimIndent()
                )

                // Create BUDGETTABLE_V1 table
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS BUDGETTABLE_V1 (
                        budgetEntryId INTEGER PRIMARY KEY NOT NULL,
                        budgetYearId INTEGER,
                        categId INTEGER,
                        period TEXT NOT NULL /* Options: None, Weekly, Bi-Weekly, Monthly, Bi-Monthly, Quarterly, Half-Yearly, Yearly, Daily */,
                        amount REAL NOT NULL,
                        notes TEXT,
                        active INTEGER
                    )
                    """.trimIndent()
                )

                // Create BUDGETYEAR_V1 table
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS BUDGETYEAR_V1 (
                        budgetYearId INTEGER PRIMARY KEY NOT NULL,
                        budgetYearName TEXT NOT NULL UNIQUE
                    )
                    """.trimIndent()
                )
            }
        }

        // Migrate to Version 5 (add assets, attachments, customfielddata, stocks & shares, and split transactions
    }
}

// When Modifying the database, make sure to modify prepopulate.db as well.