package com.whitbread.premierinn.domain.graphql.requestBodyModels

import com.whitbread.premierinn.domain.common.EMPTY_STRING_DOMAIN

data class PreCheckInRequestBody(
    val arrivalTime: String = EMPTY_STRING_DOMAIN,
    val reservationId: String = EMPTY_STRING_DOMAIN,
    val hotelId: String = EMPTY_STRING_DOMAIN
)
