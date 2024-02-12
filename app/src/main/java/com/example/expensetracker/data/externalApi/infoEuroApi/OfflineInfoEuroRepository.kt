package com.example.expensetracker.data.externalApi.infoEuroApi

import com.example.expensetracker.model.CurrencyFormat
import com.example.expensetracker.model.InfoEuro
import kotlinx.coroutines.flow.Flow

class OfflineInfoEuroRepository(private val infoEuroRepository : InfoEuroRepository) : InfoEuroRepository {
    override fun getMonthlyRates(): List<InfoEuro> = infoEuroRepository.getMonthlyRates()
}