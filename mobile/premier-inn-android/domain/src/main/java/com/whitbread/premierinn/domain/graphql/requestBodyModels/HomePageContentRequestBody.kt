package com.whitbread.premierinn.domain.graphql.requestBodyModels

data class HomePageContentRequestBody(
    val channel: String,
    val subChannel: String,
    val language: String,
    val country: String
)