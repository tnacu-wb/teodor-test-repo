package com.whitbread.premierinn.domain.graphql.requestBodyModels

import com.whitbread.premierinn.domain.common.EMPTY_STRING_DOMAIN

data class CreateReservationGuestRequestBody(
        val basketReference: String,
        val hotelId: String,
        val reasonForStay: String,
        val booker: Booker,
        val stayingGuests: List<StayingGuests>
)

data class Booker(
        val title: String,
        val firstName: String,
        val lastName: String,
        val emailAddress: String,
        val mobile: String,
        val acceptFutureMailing: Boolean,
        val address: BookerAddress
)

data class BookerAddress(
        val addressLine1: String,
        val addressLine2: String? = EMPTY_STRING_DOMAIN,
        val addressLine3: String? = EMPTY_STRING_DOMAIN,
        val addressLine4: String? = EMPTY_STRING_DOMAIN,
        val addressType: String,
        val countryCode: String,
        val postalCode: String
)

data class StayingGuests(
        val sameAsBooker: Boolean,
        val stayingGuestDetails: StayingGuestDetails
)

data class StayingGuestDetails(
        val title: String,
        val firstName: String,
        val lastName: String,
        val additionalDetails: StayingGuestAdditionalDetails? = null
)

data class StayingGuestAdditionalDetails(
        val dob: String? = null,
        val passportNumber: String? = null,
        val nationality: String? = null
)