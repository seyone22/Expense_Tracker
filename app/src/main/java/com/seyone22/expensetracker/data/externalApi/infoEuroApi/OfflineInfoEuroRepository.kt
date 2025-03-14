package com.seyone22.expensetracker.data.externalApi.infoEuroApi

import com.seyone22.expensetracker.data.model.InfoEuroCurrencyListResponse

class OfflineInfoEuroRepository(private val infoEuroRepository: InfoEuroRepository) :
    InfoEuroRepository {
    override fun getMonthlyRates(): List<InfoEuroCurrencyListResponse> =
        infoEuroRepository.getMonthlyRates()

    override fun getCurrencyHistory(currencyCode: String): List<InfoEuroCurrencyListResponse> =
        infoEuroRepository.getCurrencyHistory(currencyCode)

}