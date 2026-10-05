package com.whitbread.premierinn.domain.graphql.requestBodyModels

data class LeadGuestAmend(
    val title: String,
    val firstName: String,
    val lastName: String,
    val emailAddress: String?
)