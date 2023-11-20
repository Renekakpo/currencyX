package com.example.currencyx.di.repository

import com.example.currencyx.data.CurrencyDao
import com.example.currencyx.data.ExchangeRateDao
import com.example.currencyx.model.Currency
import com.example.currencyx.model.ExchangeRate
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AppDaoRepository @Inject constructor(
    private val currencyDao: CurrencyDao, // A data access object (DAO) for currency-related operations.
    private val exchangeRateDao: ExchangeRateDao // A DAO for exchange rate-related operations.
) {
    // Inserts a list of currencies into the database.
    fun insertCurrencies(currencies: List<Currency>) = currencyDao.insertAll(currencies)

    // Retrieves a list of currencies from the database as a Flow.
    fun getCurrencies(): Flow<List<Currency>> = currencyDao.getAllItems()

    // Inserts an exchange rate into the database.
    fun insertExchangeRate(exchangeRate: ExchangeRate) = exchangeRateDao.insert(exchangeRate)

    // Retrieves the current exchange rate as a Flow from the database.
    fun getExchangeRate(): Flow<ExchangeRate> = exchangeRateDao.getItem()
}