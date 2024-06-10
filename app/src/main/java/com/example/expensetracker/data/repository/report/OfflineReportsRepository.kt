package com.example.expensetracker.data.repository.report

import com.example.expensetracker.data.model.Report
import kotlinx.coroutines.flow.Flow

class OfflineReportsRepository(private val reportDao: ReportDao) : ReportsRepository {
    override suspend fun insertReport(report: Report) = reportDao.insert(report)
    override suspend fun deleteReport(report: Report) = reportDao.delete(report)
    override suspend fun updateReport(report: Report) = reportDao.update(report)

    override fun getAllReportsStream(): Flow<List<Report>> = reportDao.getAllReports()
    override fun getReportStream(reportId: Int): Flow<Report?> = reportDao.getReportById(reportId)
}
