package com.whitbread.premierinn.data.common

import androidx.room.TypeConverter

private const val DELIMITER = ", "

class RecentSearchDataConverter {
    @TypeConverter
    fun fromIntList(listOfInt: List<Int>): String {
        return listOfInt.joinToString()
    }

    @TypeConverter
    fun toIntList(string: String): List<Int> {
        return string.split(DELIMITER)
                .map { it.toInt() }
    }

    @TypeConverter
    fun fromBooleanList(listOfBoolean: List<Boolean>): String {
        return listOfBoolean.joinToString()
    }

    @TypeConverter
    fun toBooleanList(string: String): List<Boolean> {
        return string.split(DELIMITER)
                .map { it.toBoolean() }
    }

    @TypeConverter
    fun fromStringList(listOfString: List<String>): String {
        return listOfString.joinToString()
    }

    @TypeConverter
    fun toStringList(string: String): List<String> {
        return string.split(DELIMITER)
    }
}