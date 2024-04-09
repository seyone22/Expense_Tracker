package com.example.expensetracker.data.report

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.expensetracker.model.CurrencyFormat
import com.example.expensetracker.model.Report
import kotlinx.coroutines.flow.Flow

@Dao
interface ReportDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(report: Report)
    @Update
    suspend fun update(report: Report)
    @Delete
    suspend fun delete(report: Report)


    @Query("SELECT * FROM REPORT_V1")
    fun getAllReports(): Flow<List<Report>>
    @Query("SELECT * FROM REPORT_V1 WHERE REPORTID = :reportId")
    fun getReportById(reportId: Int): Flow<Report?>
}