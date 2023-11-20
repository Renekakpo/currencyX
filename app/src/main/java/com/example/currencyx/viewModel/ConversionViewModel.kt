package com.example.currencyx.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.currencyx.model.ConversionResult
import com.example.currencyx.model.Currency
import com.example.currencyx.model.ExchangeRate
import com.example.currencyx.di.repository.AppDaoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConversionViewModel @Inject constructor(
    appDaoRepository: AppDaoRepository
) : ViewModel() {

    // Flow to observe the latest exchange rates
    private val latestExchangeRates: Flow<ExchangeRate> = appDaoRepository.getExchangeRate()

    // Flow to observe the list of available currencies
    val currencies: Flow<List<Currency>> = appDaoRepository.getCurrencies()

    // Selected currency, observed as a StateFlow
    private val _selectedCurrency = MutableStateFlow<Currency?>(null)
    val selectedCurrency: StateFlow<Currency?> = _selectedCurrency

    // Conversion results, observed as a StateFlow
    private val _conversionResults = MutableStateFlow<List<ConversionResult>>(emptyList())
    val conversionResults: StateFlow<List<ConversionResult>> = _conversionResults

    // Store the latest exchange rates locally for quick access
    var latestExchangeRateData: ExchangeRate? = null

    // Store the currencies locally for quick access
    var currenciesData: List<Currency>? = null

    // Initialization block
    fun initDataForQuickAccess() {
        // Collect the latest exchange rate data from the repository
        viewModelScope.launch {
            latestExchangeRates.collect { exchangeRateData ->
                latestExchangeRateData = exchangeRateData
            }
        }

        // Collect the list of available currencies from the repository
        viewModelScope.launch {
            currencies.collect { data ->
                currenciesData = data
            }
        }
    }

    /**
     * Initialize the conversion results list with selected currencies
     */
    fun initConversionResults(list: List<Currency>) {
        viewModelScope.launch {
            val updatedList = list.map { currency ->
                ConversionResult(
                    targetCurrency = currency,
                    convertedAmount = 0.0
                )
            }
            _conversionResults.update { updatedList }
        }
    }

    /**
     * Set the selected base currency
     */
    fun setSelectedCurrency(currency: Currency) {
        _selectedCurrency.update { currency }
    }

    /**
     * Adjust exchange rates for the selected base currency
     */
    private fun adjustExchangeRatesForSelectedCurrency(
        baseCurrency: Currency,
        exchangeRateData: ExchangeRate
    ): Map<Currency, Double> {
        val selectedCurrencyRate = exchangeRateData.rates[baseCurrency.code] ?: 1.0
        val adjustedExchangeRates = mutableMapOf<Currency, Double>()

        currenciesData?.forEach { targetCurrency ->
            // Get the rate of the targeted currency. Otherwise use a default value: 1.0
            val rate = exchangeRateData.rates[targetCurrency.code] ?: 1.0
            val adjustedRate = rate / selectedCurrencyRate
            adjustedExchangeRates[targetCurrency] = adjustedRate
        }
        // Adjust exchange rates for each target currency
//        for ((code, rate) in exchangeRateData.rates) {
//            val adjustedRate = rate / selectedCurrencyRate
//            val mCurrency = currenciesData?.find { it.code == code }
//            if (mCurrency != null) {
//                adjustedExchangeRates[mCurrency] = adjustedRate
//            }
//        }

        return adjustedExchangeRates
    }

    /**
     * Convert the given amount from the base currency to target currencies
     */
    fun convertCurrency(
        amount: Double,
        baseCurrency: Currency,
        targetCurrencies: List<Currency>
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val exchangeRateData = latestExchangeRateData

                if (exchangeRateData != null) {
                    val adjustedExchangeRates =
                        adjustExchangeRatesForSelectedCurrency(baseCurrency, exchangeRateData)

                    // Calculate conversion results for each target currency
                    val conversionResults = targetCurrencies.map { targetCurrency ->
                        val targetRate = adjustedExchangeRates[targetCurrency]
                        val convertedAmount = amount * (targetRate ?: 0.0)
                        ConversionResult(targetCurrency, convertedAmount)
                    }

                    _conversionResults.value = conversionResults
                }
            } catch (e: Exception) {
                // Handle any exceptions and log the error
                Log.e("convertCurrency", "${e.message}")
            }
        }
    }
}