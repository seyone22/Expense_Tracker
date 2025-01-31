package com.seyone22.expensetracker.data.repository.currencyFormat

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.seyone22.expensetracker.data.model.CurrencyFormat
import kotlinx.coroutines.flow.Flow

@Dao
interface CurrencyFormatDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(account: CurrencyFormat)

    @Update
    suspend fun update(account: CurrencyFormat)

    @Delete
    suspend fun delete(account: CurrencyFormat)

    @Query("SELECT * FROM CURRENCYFORMATS_V1 WHERE currencyId = :currencyId")
    fun getCurrencyFormat(currencyId: Int): Flow<CurrencyFormat>

    @Query("SELECT * FROM CURRENCYFORMATS_V1 ORDER BY currencyName ASC")
    fun getAllCurrencyFormats(): Flow<List<CurrencyFormat>>

    @Query("SELECT * FROM CURRENCYFORMATS_V1 WHERE currencyId = :currencyId")
    fun getAllCurrencyFormatsByCurrencyFormat(currencyId: Int): Flow<List<CurrencyFormat>>

    @Query(
        """
    SELECT DISTINCT
        currencyId
    FROM ACCOUNTLIST_V1    
        """
    )
    fun getActiveCurrencies(): Flow<List<Int>>
}