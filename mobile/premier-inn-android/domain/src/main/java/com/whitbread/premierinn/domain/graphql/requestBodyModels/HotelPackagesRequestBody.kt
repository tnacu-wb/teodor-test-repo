package com.whitbread.premierinn.domain.graphql.requestBodyModels

import com.whitbread.premierinn.domain.common.EMPTY_STRING_DOMAIN

data class HotelPackagesRequestBody(
    val hotelId: String,
    val startDate: String,
    val endDate: String,
    val adultsNumber: Int,
    val childrenNumber: Int,
    val nightsNumber: Int,
    val language: String,
    val country: String,
    val bookingFlowId: String,
    val showMealInclusiveRate: Boolean = false,
    val basketReferenceId: String = EMPTY_STRING_DOMAIN,
    val channel: Channel
)