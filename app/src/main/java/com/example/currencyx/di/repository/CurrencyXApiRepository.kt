package com.example.currencyx.di.repository

import com.example.currencyx.model.ExchangeRate
import com.example.currencyx.network.CurrencyXApi
import com.example.currencyx.utils.Constant
import javax.inject.Inject

/**
 * The CurrencyXApiRepository class provides a repository
 * for fetching exchange rate data and currency
 * information from an external API.
 */
class CurrencyXApiRepository @Inject constructor(private val api: CurrencyXApi) {

    /**
     * Fetch the latest exchange rates from the external API.
     * This function is marked as 'suspend' since it's a coroutine
     * operation and can be called from a coroutine scope.
     * @return ExchangeRate object representing the latest exchange rates.
     */
    suspend fun getLatestExchangeRates(): ExchangeRate = api.getLatestExchangeRates(
        appID = Constant.OPEN_EXCHANGE_APP_ID, // The API key required for authentication.
        base = null, // The base currency for conversion (null indicates using the API's default base).
        symbols = null, // A specific set of currency symbols to limit the result (null for all available currencies).
        prettyPrint = null, // Optional parameter for pretty-printing the response (null for default behavior).
        showAlternative = null // Optional parameter to show alternative cryptocurrencies (null for default behavior).
    )

    /**
     * Fetch a map of available currencies from the external API.
     * @return A Map containing currency codes as keys and currency names as values.
     */
    suspend fun getCurrencies(): Map<String, String> = api.getCurrencies(
        prettyprint = null, // Optional parameter for pretty-printing the response (null for default behavior).
        showAlternative = null, // Optional parameter to show alternative cryptocurrencies (null for default behavior).
        showInactive = null // Optional parameter to show inactive currencies (null for default behavior).
    )
}