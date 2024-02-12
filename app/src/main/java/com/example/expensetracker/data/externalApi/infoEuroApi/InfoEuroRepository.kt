package com.example.expensetracker.data.externalApi.infoEuroApi

import com.example.expensetracker.model.InfoEuro

interface InfoEuroRepository {
    fun getMonthlyRates(): List<InfoEuro>
}