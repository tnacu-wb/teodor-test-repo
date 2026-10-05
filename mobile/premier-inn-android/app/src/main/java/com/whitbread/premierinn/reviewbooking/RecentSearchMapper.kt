package com.whitbread.premierinn.reviewbooking

import com.whitbread.premierinn.common.utils.StringUtils
import com.whitbread.premierinn.domain.recentsearch.entity.RecentSearch
import com.whitbread.premierinn.paymentdetails.PaymentDetailsInput

fun PaymentDetailsInput.toRecentSearch(): RecentSearch {
    val booking = this.bookingFlowInput()
    return RecentSearch(
            searchTerm = StringUtils.EMPTY_STRING,
            hotelCode = booking.hotelCode(),
            latitude = 0f,
            longitude = 0f,
            hotelBrand = StringUtils.EMPTY_STRING,
            arrivalDate = booking.arrivalDate(),
            departureDate = booking.arrivalDate().plusDays(booking.numNights().toLong()),
            roomsCount = booking.roomBookings().size,
            adults = booking.roomBookings().map { it.adults },
            children = booking.roomBookings().map { it.children },
            infants = booking.roomBookings().map { it.infants },
            cots = booking.roomBookings().map { it.cot },
            roomTypeCodes = booking.roomBookings().map { it.type })
}