package com.seyone22.expensetracker.data.externalApi.infoEuroApi

import com.seyone22.expensetracker.data.model.InfoEuroCurrencyListResponse

interface InfoEuroRepository {
    fun getMonthlyRates(): List<InfoEuroCurrencyListResponse>
    fun getCurrencyHistory(currencyCode: String): List<InfoEuroCurrencyListResponse>
}