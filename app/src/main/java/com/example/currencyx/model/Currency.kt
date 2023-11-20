package com.example.currencyx.model

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.currencyx.utils.Constant.CURRENCY_TABLE_NAME
import kotlinx.serialization.Serializable

@Entity(tableName = CURRENCY_TABLE_NAME, indices = [Index(value = ["code", "name"], unique = true)])
@Serializable
data class Currency(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val code: String,
    val name: String,
)

