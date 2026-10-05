package com.whitbread.premierinn.domain.graphql.ciol.entity

data class AuthorizeCardDomain(
        val paymentRedirect: String,
        val template: String,
        val sessionId: String,
        val providerUrl: String
)
