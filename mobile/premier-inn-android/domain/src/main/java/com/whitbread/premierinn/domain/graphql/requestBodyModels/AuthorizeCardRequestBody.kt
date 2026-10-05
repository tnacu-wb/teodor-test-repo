package com.whitbread.premierinn.domain.graphql.requestBodyModels

import com.whitbread.premierinn.domain.common.EMPTY_STRING_DOMAIN

data class AuthorizeCardRequestBody(
        val requestId: String = EMPTY_STRING_DOMAIN,
        val environment: String = EMPTY_STRING_DOMAIN,
        val language: String = EMPTY_STRING_DOMAIN,
        val country: String = EMPTY_STRING_DOMAIN
)
