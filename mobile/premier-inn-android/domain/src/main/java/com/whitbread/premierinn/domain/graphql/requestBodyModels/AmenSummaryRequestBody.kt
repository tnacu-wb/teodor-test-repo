package com.whitbread.premierinn.domain.graphql.requestBodyModels

data class AmendSummaryRequestBody(
    val tempBasketReference: String,
    val originalBasketReference: String,
    val token: String,
    val bookingChannel: BookingChannelDetails,
    val country: String
)