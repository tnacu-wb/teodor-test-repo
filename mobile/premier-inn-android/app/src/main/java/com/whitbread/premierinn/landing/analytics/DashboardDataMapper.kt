package com.whitbread.premierinn.landing.analytics

import com.whitbread.premierinn.common.format.DateFormat
import com.whitbread.premierinn.common.format.format
import com.whitbread.premierinn.domain.dashboard.entity.DashboardItem
import com.whitbread.premierinn.domain.dashboard.entity.FrequentBooking
import com.whitbread.premierinn.domain.recentsearch.entity.RecentSearch

private const val MIN_CHARACTERS = 0
private const val MAX_CHARACTERS = 25

fun DashboardItem.toUpcomingBookingTracking(): String {
    val actions = this.content?.actions?.joinToString {
        it.type.substring(MIN_CHARACTERS, formatMaxCharacters(it.type))
    }
    val confirmationNumber = this.content?.confirmationNumber
    val arrivalDate = this.content?.arrivalDate?.format(DateFormat.SLASHED_DAY_MONTH_YEAR)

    return formattedCodeAndArrivalDate(confirmationNumber, arrivalDate) + ", $actions"
}

fun List<RecentSearch>.toRecentSearchesTracking(): String {
    return this.joinToString {
        val code = when {
            it.hotelCode != null && it.hotelCode!!.isNotBlank() -> { it.hotelCode!! }
            else -> { it.searchTerm }
        }
        formattedCodeAndArrivalDate(code, it.arrivalDate.format(DateFormat.SLASHED_DAY_MONTH_YEAR))
    }
}

fun List<FrequentBooking>.toFrequentBookingTracking(): String {
    return this.joinToString {
        String.format("%s:%s", it.hotelCode, "undated")
    }
}

fun formatMaxCharacters(type: String): Int {
    return if (type.length > MAX_CHARACTERS) {
        MAX_CHARACTERS
    } else type.length
}

fun formattedCodeAndArrivalDate(code: String?, arrivalDate: String?): String {
    return String.format("%s:%s", code, arrivalDate)
}