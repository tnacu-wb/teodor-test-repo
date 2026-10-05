package com.whitbread.premierinn.domain.graphql.requestBodyModels

data class UpdatePreStayInfoRequestBody(
    val basketReference: String,
    val hotelId: String,
    val reasonForStay: String,
    val addressLine1: String,
    val addressLine2: String,
    val addressLine3: String,
    val addressLine4: String,
    val postalCode: String,
    val countryCode: String,
    val title: String,
    val firstName: String,
    val lastName: String,
    val emailAddress: String,
    val mobile: String,
    val stayingGuests: List<StayingGuest>
)

data class StayingGuest(
    val sameAsBooker: Boolean,
    val stayingGuestDetails: StayingGuestDetails,
    val accompanyingGuestDetails: AccompanyingGuestDetails?
)

data class AccompanyingGuestDetails(
    val title: String,
    val firstName: String,
    val lastName: String,
    val additionalDetails: StayingGuestAdditionalDetails? = null
)
