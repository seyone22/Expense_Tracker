package com.example.expensetracker.ui.screen.operations.report

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expensetracker.data.model.Report
import com.example.expensetracker.data.repository.currencyFormat.CurrencyFormatsRepository
import com.example.expensetracker.data.repository.report.ReportsRepository
import com.example.expensetracker.ui.screen.onboarding.CurrencyList
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class AddReportViewModel(
    private val reportsRepository: ReportsRepository,
    private val currencyFormatsRepository: CurrencyFormatsRepository
) : ViewModel() {
    val currencyList: StateFlow<CurrencyList> =
        currencyFormatsRepository.getAllCurrencyFormatsStream()
            .map { currencies ->
                CurrencyList(
                    currenciesList = currencies
                )
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = CurrencyList()
            )


    var reportUiState by mutableStateOf(ReportUiState())
        private set

    fun updateUiState(reportDetails: ReportDetails) {
        reportUiState =
            ReportUiState(
                reportDetails = reportDetails,
                isEntryValid = validateInput(reportDetails)
            )
    }

    suspend fun saveReport() {
        Log.d("DEBUG", "saveReport: Called!")
        if (validateInput()) {
            Log.d("DEBUG", "saveReport: Input Valid!")
            reportsRepository.insertReport(reportUiState.reportDetails.toReport())
        }
    }

    private fun validateInput(uiState: ReportDetails = reportUiState.reportDetails): Boolean {
        Log.d("DEBUG", "validateInput: Validation Begins!")
        Log.d("DEBUG", uiState.reportName)
        return with(uiState) {
            reportName.isNotBlank() && (sqlContent.isNotBlank())
        }
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}

//Data class for ReportUiState
data class ReportUiState(
    val reportDetails: ReportDetails = ReportDetails(),
    val isEntryValid: Boolean = false
)

//Data class for ReportDetails
data class ReportDetails(
    val reportId: Int = 0,
    val reportName: String = "",
    val groupName: String = "",
    val active: Boolean = false,
    val sqlContent: String = "",
    val luaContent: String = "",
    val templateContent: String = "",
    val description: String = ""
)


// Extension functions to convert between [Report], [ReportUiState], and [ReportDetails]
fun ReportDetails.toReport(): Report = Report(
    REPORTNAME = reportName,
    GROUPNAME = groupName,
    ACTIVE = if (active) 1 else 0,
    SQLCONTENT = sqlContent,
    LUACONTENT = luaContent,
    TEMPLATECONTENT = templateContent,
    DESCRIPTION = description
)

fun Report.toReportUiState(isEntryValid: Boolean = false): ReportUiState = ReportUiState(
    reportDetails = this.toReportDetails(),
    isEntryValid = isEntryValid
)

fun Report.toReportDetails(): ReportDetails = ReportDetails(
    reportId = REPORTID,
    reportName = REPORTNAME,
    groupName = GROUPNAME ?: "",
    active = ACTIVE == 1,
    sqlContent = SQLCONTENT ?: "",
    luaContent = LUACONTENT ?: "",
    description = DESCRIPTION ?: "",
)