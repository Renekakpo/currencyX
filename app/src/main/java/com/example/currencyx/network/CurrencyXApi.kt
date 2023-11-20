package com.example.currencyx.network

import com.example.currencyx.model.ExchangeRate
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface CurrencyXApi {
    @Headers("Content-Type: application/json")
    @GET("latest.json")
    suspend fun getLatestExchangeRates(
        @Query("app_id") appID: String,
        @Query("base") base: String?,
        @Query("symbols") symbols: String?,
        @Query("prettyprint") prettyPrint: Boolean?,
        @Query("show_alternative") showAlternative: Boolean?
    ): ExchangeRate

    @Headers("Content-Type: application/json")
    @GET("currencies.json")
    suspend fun getCurrencies(
        @Query("prettyprint") prettyprint: Boolean?,
        @Query("show_alternative") showAlternative: Boolean?,
        @Query("show_inactive") showInactive: Boolean?
    ): Map<String, String>
}