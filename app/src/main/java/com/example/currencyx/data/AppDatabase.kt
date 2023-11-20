package com.example.currencyx.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.currencyx.model.Currency
import com.example.currencyx.model.ExchangeRate
import com.example.currencyx.model.typeConverters.RateConverters

@Database(entities = [Currency::class, ExchangeRate::class], version = 1, exportSchema = false)
@TypeConverters(RateConverters::class)
abstract class AppDatabase: RoomDatabase() {
    abstract fun currencyDao(): CurrencyDao
    abstract fun exchangeRateDao(): ExchangeRateDao
}