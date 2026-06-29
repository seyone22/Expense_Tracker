package com.seyone22.expensetracker.data.repository.report

import com.seyone22.expensetracker.data.model.Report
import kotlinx.coroutines.flow.Flow

import androidx.sqlite.db.SupportSQLiteQuery
import android.database.Cursor

interface ReportsRepository {
    fun getAllReportsStream(): Flow<List<Report>>
    fun getReportStream(reportId: Int): Flow<Report?>

    suspend fun insertReport(report: Report)
    suspend fun deleteReport(report: Report)
    suspend fun updateReport(report: Report)

    fun executeRawQuery(query: SupportSQLiteQuery): Cursor
}
