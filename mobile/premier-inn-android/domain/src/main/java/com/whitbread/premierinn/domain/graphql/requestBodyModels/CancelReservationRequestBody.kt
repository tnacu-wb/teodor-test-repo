package com.whitbread.premierinn.domain.graphql.requestBodyModels

data class CancelReservationRequestBody(
    val basketReference: String,
    val hotelId: String,
    val token: String
)