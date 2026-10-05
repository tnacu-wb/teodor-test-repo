package com.whitbread.premierinn.domain.booking.entity

import com.whitbread.premierinn.domain.common.*
import org.threeten.bp.LocalDate
import org.threeten.bp.temporal.ChronoUnit
import java.util.*

data class Booking @JvmOverloads constructor(
    val bookingReference: String,
    val leadGuestSurname: String,
    val arrivalDate: LocalDate,
    val departureDate: LocalDate,
    val hotelCode: String,
    val hotelName: String,
    val numberOfRooms: Int,
    val numberOfGuests: Int? = null,
    val leadGuestFullName: String,
    val rateType: String,
    val totalCost: PriceDomain? = null,
    val balanceOutstanding: PriceDomain? = null,
    val prepaidAmount: PriceDomain? = null,
    val amendable: Boolean = false,
    val cancellable: Boolean = false,
    val isCancelled: Boolean = false,
    val isLinkedToAccount: Boolean = false,
    val cardFeeApplies: Boolean = false,
    val details: Details? = null,
    val upsells: List<Upsell> = Collections.emptyList(),
    val amendRestrictions: AmendRestrictions,
    val booker: Guest? = null, // TODO Remove to different object as we only get this from reservation details
    val rooms: List<Room>? = Collections.emptyList(), // TODO Remove details, rooms, num Guests into different object
    val isBusinessBooking: Boolean = false,
    val preStayDetails: PreStayDetails? = null,
    val bookingStatus: String = EMPTY_STRING_DOMAIN,
    val isCheckInOnlineAvailable: Boolean = false,
    val isCheckOutOnlineAvailable: Boolean = false,
    val basketStatus: String? = null,
    val hotelCountry: String? = EMPTY_STRING_DOMAIN,
    val isEmployeeBooking: Boolean = false,
    val rateCode: String = EMPTY_STRING_DOMAIN,
    val rateDescription: String = EMPTY_STRING_DOMAIN,
    val isThirdPartyBooking: Boolean = false,
    val paymentOption: String = EMPTY_STRING_DOMAIN,
    val upsellsAddonsEnabled: Boolean = false
) {
    val numberOfNights: Long = ChronoUnit.DAYS.between(arrivalDate, departureDate)
    val prepaid: Boolean = prepaidAmount != null
    val numberOfAdults: Int = rooms?.sumOf { it.numberOfAdults } ?: 0
    val numberOfChildren: Int = rooms?.sumOf { it.numberOfChildren } ?: 0

    data class Details(val business: Boolean, val cityTax: PriceDomain?, val rateName: String)

    data class Room(
        val roomId: String,
        val roomType: RoomType,
        val lettingType: String? = EMPTY_STRING_DOMAIN,
        val numberOfAdults: Int,
        val numberOfChildren: Int,
        val leadGuestInfo: LeadGuest,
        val additionalAdult: Guest? = null
    )
}