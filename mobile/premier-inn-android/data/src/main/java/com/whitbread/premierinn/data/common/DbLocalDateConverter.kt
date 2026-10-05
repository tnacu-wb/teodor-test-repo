package com.whitbread.premierinn.data.common

import androidx.room.TypeConverter
import org.threeten.bp.LocalDate

class DbLocalDateConverter {

    @TypeConverter
    fun fromTimestamp(value: Long?): LocalDate? {
        return value?.let {
            LocalDate.ofEpochDay(it)
        }
    }

    @TypeConverter
    fun longTimestampFrom(date: LocalDate?): Long? {
        return date?.toEpochDay()
    }
}