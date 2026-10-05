package com.whitbread.premierinn.common.utils

import android.content.Context
import com.whitbread.premierinn.R
import com.whitbread.premierinn.common.format.DateFormat
import com.whitbread.premierinn.common.format.format
import com.whitbread.premierinn.domain.common.Guest
import com.whitbread.premierinn.domain.common.RoomCriteria
import com.whitbread.premierinn.domain.common.RoomType
import com.whitbread.premierinn.domain.common.nightsCount
import com.whitbread.premierinn.roomcriteria.getStringResourceName
import org.threeten.bp.LocalDate

fun Guest.fullName(): String = "${this.title} ${this.firstName} ${this.lastName}"

fun RoomCriteria.roomCriteriaSummary(context: Context): String {

    val adults = context.resources.getQuantityString(R.plurals.number_of_adults_capitalised, this.numberOfAdults, this.numberOfAdults)
    val children = context.resources.getQuantityString(R.plurals.number_of_children_capitalised, this.numberOfChildren, this.numberOfChildren)
    val roomType = this.roomType.formatted(context)

    return if (this.numberOfChildren > 0) {
        "$adults, $children, $roomType"
    } else "$adults, $roomType"
}

fun RoomType.formatted(context: Context): String {
    val roomType = this.getStringResourceName(context)
    return context.getString(R.string.room_suffix, roomType)
}

fun Pair<LocalDate, LocalDate>.formattedNumberOfNights(context: Context): String {
    val (arrival, departure) = this
    return context.getString(R.string.arrival_departure_dates_with_nights,
            arrival.format(DateFormat.WEEKDAY_DAY_MONTH),
            departure.format(DateFormat.WEEKDAY_DAY_MONTH),
            context.resources.getQuantityString(R.plurals.nights, this.nightsCount(), this.nightsCount())
    )
}

fun Pair<LocalDate, LocalDate>.formattedDates(context: Context): String {
    val (arrival, departure) = this
    return context.getString(R.string.arrival_departure_dates,
            arrival.format(DateFormat.WEEKDAY_DAY_MONTH),
            departure.format(DateFormat.WEEKDAY_DAY_MONTH))
}

fun guestsAndNights(context: Context, guests: Int, nights: Int): String {
    return """${context.resources.getQuantityString(R.plurals.guests, guests, guests)} ${context.resources.getQuantityString(R.plurals.nights, nights, nights)}"""
}

fun guestsAndRooms(context: Context, guests: Int, rooms: Int) : String {
    return context.getString(R.string.guests_and_rooms,
            context.resources.getQuantityString(R.plurals.guests, guests, guests),
            context.resources.getQuantityString(R.plurals.rooms, rooms, rooms))
}
