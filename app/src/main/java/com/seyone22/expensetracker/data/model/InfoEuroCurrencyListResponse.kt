package com.seyone22.expensetracker.data.model

import kotlinx.serialization.Serializable

@Serializable
data class InfoEuroCurrencyListResponse(
    val country: String,
    val currency: String,
    val isoA3Code: String,
    val isoA2Code: String,
    val value: Double,
    val comment: String?
)

@Serializable
data class InfoEuroCurrencyHistoryResponse(
    val currencyIso: String,
    val refCurrencyIso: String,
    val amount: Double,
    val dateStart: String,
    val dateEnd: String,
)