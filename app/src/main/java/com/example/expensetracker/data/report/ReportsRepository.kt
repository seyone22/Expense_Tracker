package com.example.expensetracker.data.report

import com.example.expensetracker.model.Report
import kotlinx.coroutines.flow.Flow

interface ReportsRepository{
    fun getAllReportsStream() : Flow<List<Report>>
    fun getReportStream(reportId: Int): Flow<Report?>

    suspend fun insertReport(report: Report)
    suspend fun deleteReport(report: Report)
    suspend fun updateReport(report: Report)
}
