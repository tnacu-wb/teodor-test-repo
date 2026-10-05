package com.whitbread.premierinn.calendar

import com.prolificinteractive.materialcalendarview.CalendarDay
import org.threeten.bp.LocalDate
import java.text.SimpleDateFormat
import java.util.Locale


fun LocalDate.toCalendarDay(): CalendarDay = CalendarDay.from(year, monthValue - 1, dayOfMonth)

fun CalendarDay.toLocalDate(): LocalDate = LocalDate.of(year, month + 1, day)

fun convertDateFormat(dateString: String, currentFormat: String, newFormat: String): String {
    if (dateString.isEmpty()) return dateString

    val currentDateFormat = SimpleDateFormat(currentFormat, Locale.getDefault())
    val newDateFormat = SimpleDateFormat(newFormat, Locale.getDefault())
    val date = currentDateFormat.parse(dateString)

    return newDateFormat.format(date)
}
