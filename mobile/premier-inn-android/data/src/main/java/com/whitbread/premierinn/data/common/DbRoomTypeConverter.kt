package com.whitbread.premierinn.data.common

import androidx.room.TypeConverter
import com.whitbread.premierinn.domain.common.RoomType

class DbRoomTypeConverter {

    @TypeConverter
    fun fromStringToRoomType(value: String?): RoomType? {
        return value?.toRoomType()
    }

    @TypeConverter
    fun fromRoomTypeToString(type: RoomType?): String? {
        return type?.toRoomString()
    }
}