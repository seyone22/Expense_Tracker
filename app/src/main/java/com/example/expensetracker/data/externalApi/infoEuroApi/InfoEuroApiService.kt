package com.example.expensetracker.data.externalApi.infoEuroApi

import com.example.expensetracker.model.InfoEuro
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.http.GET

private const val BASE_URL =
    "https://ec.europa.eu"

private val retrofit = Retrofit.Builder()
    .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
    .baseUrl(BASE_URL)
    .build()

interface InfoEuroApiService {
    @GET("/budg/inforeuro/api/public/monthly-rates")
    suspend fun getMonthlyRates() : List<InfoEuro>
}

object InfoEuroApi {
    val retrofitService : InfoEuroApiService by lazy {
        retrofit.create(InfoEuroApiService::class.java)
    }
}