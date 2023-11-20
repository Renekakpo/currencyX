package com.example.currencyx.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.currencyx.model.ExchangeRate
import com.example.currencyx.utils.Constant
import kotlinx.coroutines.flow.Flow

@Dao
interface ExchangeRateDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(exchangeRate: ExchangeRate)

    @Query("SELECT * FROM ${Constant.EXCHANGE_RATE_TABLE_NAME} LIMIT 1")
    fun getItem(): Flow<ExchangeRate>
}