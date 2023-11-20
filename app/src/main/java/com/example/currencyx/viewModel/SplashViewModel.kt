package com.example.currencyx.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.currencyx.di.repository.AppDaoRepository
import com.example.currencyx.di.repository.CurrencyXApiRepository
import com.example.currencyx.model.Currency
import com.example.currencyx.model.ExchangeRate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class SplashUiState {LOADING, ERROR, DONE}

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val apiRepos: CurrencyXApiRepository, // Repository for making API calls
    private val appDaoRepository: AppDaoRepository // Local data storage repository
) : ViewModel() {

    // Flow of the latest exchange rate data from local storage
    val latestExchangeRates: Flow<ExchangeRate?> = appDaoRepository.getExchangeRate()

    // Flow of the list of currencies from local storage
    val currencies: Flow<List<Currency>> = appDaoRepository.getCurrencies()

    private val _uiState = MutableStateFlow(SplashUiState.LOADING)
    val uiState: StateFlow<SplashUiState> = _uiState

    fun initUiState() {
        // Observe both currencies and latestExchangeRates
        viewModelScope.launch {
            combine(currencies, latestExchangeRates) { currencyList, exchangeRate ->
                try {
                    if (currencyList.isNotEmpty() && exchangeRate != null) {
                        _uiState.value = SplashUiState.DONE
                    } else {
                        // Handle other cases if needed
                    }
                } catch (e: Exception) {
                    // Handle exceptions here
                    _uiState.value = SplashUiState.ERROR
                }
            }.collect()
        }
    }

    /**
     * Function to fetch currencies from the API and store them locally
     */
    fun fetchCurrencies() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val mCurrencies = mutableListOf<Currency>()

                // Fetch currency data from the API and transform it into the Currency model
                apiRepos.getCurrencies().forEach { (code, name) ->
                    mCurrencies.add(Currency(0, code, name))
                }

                if (mCurrencies.isNotEmpty()) {
                    // If currencies were successfully fetched, insert them into the local database
                    appDaoRepository.insertCurrencies(mCurrencies)
                }
            } catch (e: Exception) {
                // Handle exceptions and log error messages
                Log.e("Currencies", "${e.message}")
            }
        }
    }

    /**
     * Function to fetch the latest exchange rates from the API and store them locally
     */
    fun fetchLatestExchangeRates() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Fetch the latest exchange rate data from the API
                val mExchangeRate = apiRepos.getLatestExchangeRates()
                // Insert the latest exchange rate data into the local database
                appDaoRepository.insertExchangeRate(exchangeRate = mExchangeRate)
            } catch (e: Exception) {
                // Handle exceptions and log error messages
                Log.e("LatestExchangeRates", "${e.message}")
            }
        }
    }
}