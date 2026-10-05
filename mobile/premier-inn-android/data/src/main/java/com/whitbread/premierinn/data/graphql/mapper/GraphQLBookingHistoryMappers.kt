package com.whitbread.premierinn.data.graphql.mapper

import com.whitbread.premierinn.data.common.EMPTY_STRING
import com.whitbread.premierinn.data.common.toLocalDate
import com.whitbread.premierinn.data.remote.graphql.contracts.BookingHistoryGraphQLContract
import com.whitbread.premierinn.domain.booking.entity.Booking
import com.whitbread.premierinn.domain.common.AmendRestrictions
import com.whitbread.premierinn.domain.common.EMPLOYEE_RATE_PLAN_CODE
import com.whitbread.premierinn.domain.common.PriceDomain

fun BookingHistoryGraphQLContract.BookingHistoryData.mapToBookingDomainGQL(isBusinessBooking: Boolean): List<Booking> {
    this.data?.bookingHistory?.let {
        return  this.data.bookingHistory.bookings.asIterable()
            .map { it.toBooking(isBusinessBooking) }
            .toList()
    }
    return emptyList()
}

fun BookingHistoryGraphQLContract.BookingsFromBookingHistory.toBooking(isBusinessBooking: Boolean): Booking {
    return Booking(
        bookingReference = this.bookingReference,
        leadGuestSurname = this.leadGuestSurname,
        arrivalDate = this.arrivalDate.toLocalDate(),
        departureDate = this.departureDate.toLocalDate(),
        hotelCode = this.hotelCode,
        hotelName = this.hotelName,
        numberOfRooms = this.noOfRooms,
        leadGuestFullName = this.leadGuest,
        rateType = this.rateName,
        prepaidAmount = this.prePaidAmount?.toPriceDomain(),
        totalCost = this.totalCost.toPriceDomain(),
        amendable = this.amendable,
        cardFeeApplies = false,
        isCancelled = this.cancelled,
        amendRestrictions = AmendRestrictions.createWithDefaults(),
        isLinkedToAccount = true,
        isBusinessBooking = isBusinessBooking,
        bookingStatus =  this.bookingStatus ?: EMPTY_STRING,
        isCheckInOnlineAvailable = this.isCheckInOnlineAvailable,
        basketStatus = this.basketStatus,
        hotelCountry = this.hotelCountry,
        isEmployeeBooking = this.rateName == EMPLOYEE_RATE_PLAN_CODE
    )
}


private fun BookingHistoryGraphQLContract.Price.toPriceDomain(): PriceDomain {
    return PriceDomain(
        amount = this.amount,
        currency = this.currency
    )
}

