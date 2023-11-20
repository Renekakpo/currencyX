package com.example.currencyx.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.currencyx.model.typeConverters.RateConverters
import com.example.currencyx.utils.Constant.EXCHANGE_RATE_TABLE_NAME
import kotlinx.serialization.Serializable

@Entity(tableName = EXCHANGE_RATE_TABLE_NAME, indices = [Index(value = ["license", "base", "timestamp"], unique = true)])
@Serializable
data class ExchangeRate(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val disclaimer: String,
    val license: String,
    val timestamp: Long,
    val base: String,
    @TypeConverters(RateConverters::class)
    val rates: Map<String, Double>,
)
