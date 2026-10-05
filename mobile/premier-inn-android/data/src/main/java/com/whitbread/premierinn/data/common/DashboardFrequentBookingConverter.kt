package com.whitbread.premierinn.data.common

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.whitbread.premierinn.data.dashboard.entity.FrequentBookingEntity

class DashboardFrequentBookingConverter {
    @TypeConverter
    fun fromFrequentBookingList(frequentBooking: List<FrequentBookingEntity>): String {
        val gson = Gson()
        val type = object : TypeToken<List<FrequentBookingEntity>>() {}.type
        return gson.toJson(frequentBooking, type)
    }

    @TypeConverter
    fun toFrequentBookingList(frequentBooking: String?): List<FrequentBookingEntity> {
        return if (frequentBooking.isNullOrEmpty()) {
            emptyList()
        } else {
            val gson = Gson()
            val type = object : TypeToken<List<FrequentBookingEntity>>() {}.type
            gson.fromJson(frequentBooking, type)
        }
    }
}