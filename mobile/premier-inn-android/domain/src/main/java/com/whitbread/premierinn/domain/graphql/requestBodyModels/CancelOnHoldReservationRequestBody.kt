package com.whitbread.premierinn.domain.graphql.requestBodyModels

data class CancelOnHoldReservationRequestBody(
    val basketReference: String,
    val hotelId: String
)