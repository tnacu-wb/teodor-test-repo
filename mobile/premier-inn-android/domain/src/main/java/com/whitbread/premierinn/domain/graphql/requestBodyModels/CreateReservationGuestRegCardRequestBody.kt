package com.whitbread.premierinn.domain.graphql.requestBodyModels

data class CreateReservationGuestRegCardRequestBody(
    val basketReference: String,
    val hotelId: String,
    val reasonForStay: String,
    val preCheckIn: Boolean? = true,
    val title: String,
    val acceptFutureMailing: Boolean?,
    val emailAddress: String,
    val firstName: String,
    val lastName: String,
    val addressType: String?,
    val addressLine1: String,
    val addressLine2: String?,
    val addressLine3: String?,
    val addressLine4: String?,
    val postalCode: String,
    val mobile: String,
    val language: String,
    val countryCode: String?,
    val stayingGuests: List<StayingGuestDetailsRegCard>,
)

data class StayingGuestDetailsRegCard(
    val title: String? = null,
    val firstName: String,
    val lastName: String,
    val profileId: String?,
    val address: StayingGuestAddress?,
    val additionalDetails: StayingGuestAdditionalDetails,
    val sameAsBooker: Boolean,
    val reservationId: String,
    val isAccompanyingGuest: Boolean,
)

data class StayingGuestAddress(
    val addressLine1: String?,
    val addressLine2: String?,
    val addressLine3: String?,
    val cityName: String,
    val countryCode: String,
    val postalCode: String,
    val addressType: String,
)
