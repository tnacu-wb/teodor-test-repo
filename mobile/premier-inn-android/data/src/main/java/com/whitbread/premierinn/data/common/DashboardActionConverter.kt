package com.whitbread.premierinn.data.common

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.whitbread.premierinn.data.dashboard.entity.ActionEntity

class DashboardActionConverter {
    @TypeConverter
    fun fromActionList(action: List<ActionEntity>): String {
        val gson = Gson()
        val type = object : TypeToken<List<ActionEntity>>() {}.type
        return gson.toJson(action, type)
    }

    @TypeConverter
    fun toActionList(action: String): List<ActionEntity> {
        val gson = Gson()
        val type = object : TypeToken<List<ActionEntity>>() {}.type
        return gson.fromJson(action, type)
    }
}