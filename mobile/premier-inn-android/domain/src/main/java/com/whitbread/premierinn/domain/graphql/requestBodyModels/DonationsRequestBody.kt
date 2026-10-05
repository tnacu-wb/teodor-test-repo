package com.whitbread.premierinn.domain.graphql.requestBodyModels

data class DonationsRequestBody(
    val hotelId: String,
    val country: String,
    val language: String,
    val rateCode: String
)