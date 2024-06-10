package com.example.expensetracker.data.repository.payee

import com.example.expensetracker.data.model.Payee
import kotlinx.coroutines.flow.Flow

class OfflinePayeesRepository(private val payeeDao: PayeeDao) : PayeesRepository {
    override fun getAllPayeesStream(): Flow<List<Payee>> = payeeDao.getAllPayees()
    override fun getAllActivePayeesStream(): Flow<List<Payee>> = payeeDao.getAllActivePayees()
    override fun getPayeeStream(payeeId: Int): Flow<Payee?> = payeeDao.getPayee(payeeId)
    override fun getPayeesFromTypeStream(categId: Int): Flow<List<Payee>> =
        payeeDao.getAllPayeesByCategory(categId)

    override suspend fun insertPayee(payee: Payee) = payeeDao.insert(payee)
    override suspend fun deletePayee(payee: Payee) = payeeDao.delete(payee)
    override suspend fun updatePayee(payee: Payee) = payeeDao.update(payee)
}