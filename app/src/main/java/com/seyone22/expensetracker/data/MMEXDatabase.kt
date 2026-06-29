package com.seyone22.expensetracker.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.seyone22.expensetracker.data.model.Account
import com.seyone22.expensetracker.data.model.BillsDeposits
import com.seyone22.expensetracker.data.model.BudgetEntry
import com.seyone22.expensetracker.data.model.BudgetYear
import com.seyone22.expensetracker.data.model.Category
import com.seyone22.expensetracker.data.model.CurrencyFormat
import com.seyone22.expensetracker.data.model.CurrencyHistory
import com.seyone22.expensetracker.data.model.Metadata
import com.seyone22.expensetracker.data.model.Payee
import com.seyone22.expensetracker.data.model.Report
import com.seyone22.expensetracker.data.model.Tag
import com.seyone22.expensetracker.data.model.TagLink
import com.seyone22.expensetracker.data.model.Transaction
import com.seyone22.expensetracker.data.model.SplitTransaction
import com.seyone22.expensetracker.data.model.Attachment
import com.seyone22.expensetracker.data.repository.splitTransaction.SplitTransactionDao
import com.seyone22.expensetracker.data.repository.attachment.AttachmentDao
import com.seyone22.expensetracker.data.repository.account.AccountDao
import com.seyone22.expensetracker.data.repository.billsDeposit.BillsDepositsDao
import com.seyone22.expensetracker.data.repository.budgetEntry.BudgetEntryDao
import com.seyone22.expensetracker.data.repository.budgetYear.BudgetYearDao
import com.seyone22.expensetracker.data.repository.category.CategoryDao
import com.seyone22.expensetracker.data.repository.currencyFormat.CurrencyFormatDao
import com.seyone22.expensetracker.data.repository.currencyHistory.CurrencyHistoryDao
import com.seyone22.expensetracker.data.repository.metadata.MetadataDao
import com.seyone22.expensetracker.data.repository.payee.PayeeDao
import com.seyone22.expensetracker.data.repository.report.ReportDao
import com.seyone22.expensetracker.data.repository.tag.TagDao
import com.seyone22.expensetracker.data.repository.tagLink.TagLinkDao
import com.seyone22.expensetracker.data.repository.transaction.TransactionDao

@Database(
    entities = [Account::class, Transaction::class, Payee::class, Category::class, CurrencyFormat::class, Metadata::class, BillsDeposits::class, Report::class, CurrencyHistory::class, Tag::class, TagLink::class, BudgetEntry::class, BudgetYear::class, SplitTransaction::class, Attachment::class],
    version = 6,
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
    abstract fun splitTransactionDao(): SplitTransactionDao
    abstract fun attachmentDao(): AttachmentDao

    companion object {
        @Volatile
        private var Instance: MMEXDatabase? = null

        fun getDatabase(context: Context): MMEXDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, MMEXDatabase::class.java, "mmex_database")
                    //.createFromAsset("database/prepopulate_v1_1.db")
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_4, MIGRATION_4_5, MIGRATION_5_6)
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

        private val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS SPLITTRANSACTIONS_V1 (
                        SPLITTRANSID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        TRANSID INTEGER NOT NULL,
                        CATEGID INTEGER,
                        SPLITTRANSAMOUNT REAL,
                        NOTES TEXT
                    )
                    """.trimIndent()
                )
                db.execSQL(
                    """
                    CREATE INDEX IF NOT EXISTS IDX_SPLITTRANSACTIONS_TRANSID ON SPLITTRANSACTIONS_V1(TRANSID)
                    """.trimIndent()
                )
            }
        }

        private val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS ATTACHMENT_V1 (
                        ATTACHMENTID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        REFTYPE TEXT NOT NULL,
                        REFID INTEGER NOT NULL,
                        DESCRIPTION TEXT,
                        FILENAME TEXT NOT NULL
                    )
                    """.trimIndent()
                )
                db.execSQL(
                    """
                    CREATE INDEX IF NOT EXISTS IDX_ATTACHMENT_REF ON ATTACHMENT_V1 (REFTYPE, REFID)
                    """.trimIndent()
                )
            }
        }
    }
}