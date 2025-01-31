package com.seyone22.expensetracker.data.externalApi.infoEuroApi

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.seyone22.expensetracker.data.model.InfoEuroCurrencyHistoryResponse
import com.seyone22.expensetracker.data.model.InfoEuroCurrencyListResponse
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Url


private const val BASE_URL =
    "https://ec.europa.eu"

// Create a logging interceptor
val loggingInterceptor = HttpLoggingInterceptor().apply {
    level =
        HttpLoggingInterceptor.Level.NONE // Set logging level to show request and response bodies
}

// Create an OkHttpClient with the logging interceptor
val okHttpClient = OkHttpClient.Builder()
    .addInterceptor(loggingInterceptor)
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
    .client(okHttpClient)
    .baseUrl(BASE_URL)
    .build()

interface InfoEuroApiService {
    @GET("/budg/inforeuro/api/public/monthly-rates")
    suspend fun getMonthlyRates(): List<InfoEuroCurrencyListResponse>

    @GET
    suspend fun getCurrencyHistory(@Url url: String?): Response<List<InfoEuroCurrencyHistoryResponse>>
}

object InfoEuroApi {
    val retrofitService: InfoEuroApiService by lazy {
        retrofit.create(InfoEuroApiService::class.java)
    }
}