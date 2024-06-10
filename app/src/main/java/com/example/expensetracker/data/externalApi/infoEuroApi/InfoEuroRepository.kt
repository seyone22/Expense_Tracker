package com.example.expensetracker.data.externalApi.infoEuroApi

import com.example.expensetracker.data.model.InfoEuroCurrencyListResponse

interface InfoEuroRepository {
    fun getMonthlyRates(): List<InfoEuroCurrencyListResponse>
    fun getCurrencyHistory(currencyCode: String): List<InfoEuroCurrencyListResponse>
}