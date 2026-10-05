package com.whitbread.premierinn.data.payments

import com.whitbread.premierinn.data.common.EMPTY_STRING
import com.whitbread.premierinn.domain.graphql.guestDetails.entity.CardDomain
import com.whitbread.premierinn.domain.payment.entity.ThreeCPBooker
import com.whitbread.premierinn.domain.payment.entity.ThreeCpBillingAddress
import com.whitbread.premierinn.domain.payment.entity.ThreeCpBooking

fun CardDomain.toPaymentCard(): Card {
    return Card(
        token = this.token,
        expiryMonth = this.expiryMonth,
        expiryYear = this.expiryYear
    )
}

fun ThreeCpBooking.toPaymentRequestAmount(): Amount {
    return Amount(
        currency = this.currency, //get currency from rate
        minorUnits = this.minorUnits
    )
}

fun ThreeCpBooking.toPaymentBusinessSite(): BusinessSite {
    return BusinessSite(
        identifier = this.hotelCode,
        name = this.hotelName,
        type = BUSINESS_SITE_TYPE, // this will change if app start booking restaurants
        location = this.hotelLocation, // find out if this can be grabbed from availabilities
        additionalServices = emptyList()
    )
}

fun ThreeCPBooker.toPaymentRequestBilling(booking: ThreeCpBooking): Billing {
    return Billing(
        title = this.title,
        firstName = this.firstName,
        lastName = this.lastName,
        email = this.email,
        telephone = this.telephone,
        address = booking.billingAddress.toPaymentRequestBillingAddress()
    )
}

fun ThreeCPBooker.toPaymentLeadGuest(): LeadGuest {
    return LeadGuest(
        name = this.fullName,
        registered = false,
        registeredSince = this.guestHistoryCreation,
        previousBookings = this.totalStays
    )
}

fun ThreeCpBillingAddress.toPaymentRequestBillingAddress(): BillingAddress {
    return BillingAddress(
        line1 = this.line1,
        line2 = this.line2.ifEmpty { EMPTY_STRING },
        state = this.state,
        countryCode = this.countryCode,
        postalCode = this.postalCode
    )
}