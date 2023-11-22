package com.example.expensetracker.ui.entity.payee

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.expensetracker.data.payee.PayeesRepository
import com.example.expensetracker.model.Payee

class PayeeEntryViewModel(private val payeesRepository: PayeesRepository) : ViewModel() {
    var payeeUiState by mutableStateOf(PayeeUiState())
        private set

    fun updateUiState(payeeDetails: PayeeDetails) {
        payeeUiState =
            PayeeUiState(payeeDetails = payeeDetails, isEntryValid = validateInput(payeeDetails))
    }

    suspend fun savePayee() {
        Log.d("DEBUG", "savePayee: Called!")
        if (validateInput()) {
            Log.d("DEBUG", "savePayee: Input Valid!")
            payeesRepository.insertPayee(payeeUiState.payeeDetails.toPayee())
        }
    }

    private fun validateInput(uiState: PayeeDetails = payeeUiState.payeeDetails): Boolean {
        Log.d("DEBUG", "validateInput: Validation Begins!")
        Log.d("DEBUG", uiState.payeeName)
        return with(uiState) {
            payeeName.isNotBlank()
        }
    }
}

//Data class for PayeeUiState
data class PayeeUiState(
    val payeeDetails: PayeeDetails = PayeeDetails(),
    val isEntryValid: Boolean = false
)

//Data class for PayeeDetails
data class PayeeDetails(
    val payeeId: Int = 0,
    val payeeName: String = "",
    val categId: String = "",
    val number: String = "",
    val website: String = "",
    val notes: String = "",
    val active: String = ""
)


// Extension functions to convert between [Payee], [PayeeUiState], and [PayeeDetails]
fun PayeeDetails.toPayee(): Payee = Payee(
    payeeId = payeeId,
    payeeName = payeeName,
    categId = categId.toInt(),
    number = number.toInt(),
    website = website,
    notes = notes,
    active = active.toInt()
)

fun Payee.toPayeeUiState(isEntryValid: Boolean = false): PayeeUiState = PayeeUiState(
    payeeDetails = this.toPayeeDetails(),
    isEntryValid = isEntryValid
)

fun Payee.toPayeeDetails(): PayeeDetails = PayeeDetails(
    payeeId = payeeId,
    payeeName = payeeName,
    categId = categId.toString(),
    number = number.toString(),
    website = website,
    notes = notes,
    active = active.toString()
)
