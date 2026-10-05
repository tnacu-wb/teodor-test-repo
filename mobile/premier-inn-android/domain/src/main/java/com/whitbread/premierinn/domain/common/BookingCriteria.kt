package com.whitbread.premierinn.domain.common

import com.whitbread.premierinn.domain.search.entity.SearchSuggetionItem
import org.threeten.bp.LocalDate

data class BookingCriteria(val searchCriteria: SearchSuggetionItem,
                           val arrivalDate: LocalDate,
                           val numberOfNights: Int,
                           val roomsCriteria: List<RoomCriteria>) {

    val numberOfRooms = roomsCriteria.size
    val numberOfGuests = roomsCriteria.asSequence().sumBy { it.numberOfAdults + it.numberOfChildren }
    val numberOfAdults = roomsCriteria.asSequence().sumBy { it.numberOfAdults }
    val numberOfChildren = roomsCriteria.asSequence().sumBy { it.numberOfChildren }
    val departureDate: LocalDate
        get() {
            return arrivalDate.plusDays(numberOfNights.toLong())
        }
}