package com.example.currencyx.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.currencyx.model.Currency
import com.example.currencyx.utils.Constant
import kotlinx.coroutines.flow.Flow

@Dao
interface CurrencyDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(currencies: List<Currency>)

    @Query("SELECT * FROM ${Constant.CURRENCY_TABLE_NAME} ORDER BY code ASC")
    fun getAllItems(): Flow<List<Currency>>
}