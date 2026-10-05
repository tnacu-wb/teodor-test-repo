package com.whitbread.premierinn.domain.payment.entity

import com.whitbread.premierinn.domain.common.EMPTY_STRING_DOMAIN
import com.whitbread.premierinn.domain.graphql.guestDetails.entity.CardDomain

data class ThreeCpPaymentDetailsEntity(
        val booking: ThreeCpBooking,
        val booker: ThreeCPBooker,
        val bookingPayment: ThreeCpBookingPayment,
        val sessionId: String
)

data class ThreeCpBooking(
        val hotelCode: String,
        val hotelName: String,
        val hotelLocation: String,
        val arrivalDate: String,
        val departureDate: String,
        val roomBookings: List<ThreeCpPaymentRoom>,
        val currency: String,
        val minorUnits: Int,
        val billingAddress: ThreeCpBillingAddress,
        val type: String
)

data class ThreeCPBooker(
        val title: String,
        val firstName: String,
        val lastName: String,
        val email: String,
        val telephone: String,
        val guestHistoryCreation: String,
        val totalStays: Int,
        val fullName: String = "$title $firstName $lastName"
)

data class ThreeCpPaymentRoom(
        val type: String,
        val rate: String,
        val adults: Long
)

data class ThreeCpBillingAddress(
        val line1: String,
        val line2: String,
        val line3: String = EMPTY_STRING_DOMAIN,
        val line4: String = EMPTY_STRING_DOMAIN,
        val postalCode: String,
        val countryCode: String,
        val state: String?
)

data class ThreeCpBookingPayment(
        val isBusinessCard: Boolean,
        var storedCard: CardDomain?
)