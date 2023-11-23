package com.example.expensetracker.data.payee

import com.example.expensetracker.model.Payee
import kotlinx.coroutines.flow.Flow

interface PayeesRepository {
    fun getAllPayeesStream(): Flow<List<Payee>>
    fun getAllActivePayeesStream() : Flow<List<Payee>>
    fun getPayeeStream(payeeId: Int): Flow<Payee?>
    fun getPayeesFromTypeStream(categId: Int): Flow<List<Payee>>

    suspend fun insertPayee(payee: Payee)
    suspend fun deletePayee(payee: Payee)
    suspend fun updatePayee(account: Payee)
}