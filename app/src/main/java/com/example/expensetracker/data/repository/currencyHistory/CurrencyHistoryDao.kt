package com.example.expensetracker.data.repository.currencyHistory

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.expensetracker.data.model.CurrencyHistory
import kotlinx.coroutines.flow.Flow

@Dao
interface CurrencyHistoryDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(account: CurrencyHistory)

    @Update
    suspend fun update(account: CurrencyHistory)

    @Delete
    suspend fun delete(account: CurrencyHistory)

    @Query("SELECT * FROM CURRENCYHISTORY_V1 WHERE currencyId = :currencyId")
    fun getCurrencyHistory(currencyId: Int): Flow<CurrencyHistory>

    @Query("SELECT * FROM CURRENCYHISTORY_V1 ORDER BY currDate DESC")
    fun getAllCurrencyHistory(): Flow<List<CurrencyHistory>>

    @Query("SELECT * FROM CURRENCYHISTORY_V1 WHERE currencyId = :currencyId")
    fun getAllCurrencyHistoryByCurrencyHistory(currencyId: Int): Flow<List<CurrencyHistory>>
}