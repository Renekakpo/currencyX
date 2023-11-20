package com.example.currencyx.model.typeConverters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class RateConverters {

    private val gson = Gson()

    @TypeConverter
    fun fromString(value: String?): Map<String, Double> {
        if (value == null) {
            return emptyMap()
        }

        val mapType = object : TypeToken<Map<String, Double>>() {}.type
        return gson.fromJson(value, mapType)
    }

    @TypeConverter
    fun toString(value: Map<String, Double>?): String {
        return gson.toJson(value)
    }
}