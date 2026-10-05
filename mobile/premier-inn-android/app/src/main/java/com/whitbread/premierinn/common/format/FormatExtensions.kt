package com.whitbread.premierinn.common.format

import com.prolificinteractive.materialcalendarview.CalendarDay
import com.whitbread.premierinn.calendar.toLocalDate
import com.whitbread.premierinn.data.common.devicelocal.DeviceLocaleProvider
import com.whitbread.premierinn.domain.common.PriceDomain
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter

/**
 *
 */
fun PriceDomain?.formatted(deviceLocaleProvider: DeviceLocaleProvider): String {
    return if (this != null) {
        PriceFormat.format(amount, currency, deviceLocaleProvider)
    } else "N/A"
}

fun LocalDate.format(format: String): String {
    return format(DateTimeFormatter.ofPattern(format))
}

fun CalendarDay.format(format: String): String {
    return toLocalDate().format(format)
}

fun String.toLocalDate(): LocalDate {
    val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    return LocalDate.parse(this, dateTimeFormatter)
}
