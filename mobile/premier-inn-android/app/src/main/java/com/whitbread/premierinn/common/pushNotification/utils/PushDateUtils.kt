package com.whitbread.premierinn.common.pushNotification.utils

import java.time.LocalDate
import java.time.format.DateTimeFormatter


object PushDateUtils {
    fun formatDate(date: String, pattern: String): LocalDate {
        val now = LocalDate.now()

        if (date.isEmpty()) return now

        return try {
            val dateFormatter = DateTimeFormatter.ofPattern(pattern)
            val parsedDate = LocalDate.parse(date, dateFormatter)
            if (parsedDate.isBefore(now)) now else parsedDate
        } catch (e: Exception) {
            now
        }
    }
}
