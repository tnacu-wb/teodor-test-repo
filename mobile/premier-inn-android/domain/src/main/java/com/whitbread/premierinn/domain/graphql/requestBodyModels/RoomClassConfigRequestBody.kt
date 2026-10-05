package com.whitbread.premierinn.domain.graphql.requestBodyModels

data class RoomClassConfigRequestBody(
    val channel: String,
    val brand: String?,
    val country: String?,
    val language: String?
)
