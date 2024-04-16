package com.example.expensetracker.data.externalApi.infoEuroApi

import com.example.expensetracker.model.InfoEuroCurrencyListResponse

class OfflineInfoEuroRepository(private val infoEuroRepository : InfoEuroRepository) : InfoEuroRepository {
    override fun getMonthlyRates(): List<InfoEuroCurrencyListResponse> = infoEuroRepository.getMonthlyRates()
    override fun getCurrencyHistory(currencyCode: String): List<InfoEuroCurrencyListResponse> = infoEuroRepository.getCurrencyHistory(currencyCode)

}