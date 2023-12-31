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
import com.example.expensetracker.model.Category
import com.example.expensetracker.model.CurrencyFormat
import com.example.expensetracker.model.Payee
import com.example.expensetracker.model.Transaction
import com.example.expensetracker.model.Metadata

@Database(entities = [Account::class, Transaction::class, Payee::class, Category::class, CurrencyFormat::class, Metadata::class], version = 1, exportSchema = false)
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
                Room.databaseBuilder(context, MMEXDatabase::class.java, "mmex_database")
                    .createFromAsset("database/prepopulate_v1.db")
                    //.fallbackToDestructiveMigration()
                    .build()
                    .also { Instance = it }
            }
        }
    }
}

// When Modifying the database, make sure to modify prepopulate.db as well.