package com.whitbread.premierinn.data.common

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.whitbread.premierinn.data.dashboard.entity.RoomEntity
import java.lang.reflect.Type

class DashboardRoomConverter {

    @TypeConverter
    fun fromRoomList(room: List<RoomEntity>): String {
        val gson = Gson()
        val type: Type = object : TypeToken<List<RoomEntity>>() {}.type
        return gson.toJson(room, type)
    }

    @TypeConverter
    fun toRoomList(room: String): List<RoomEntity> {
        val gson = Gson()
        val type: Type = object : TypeToken<List<RoomEntity>>() {}.type
        return gson.fromJson(room, type)
    }
}