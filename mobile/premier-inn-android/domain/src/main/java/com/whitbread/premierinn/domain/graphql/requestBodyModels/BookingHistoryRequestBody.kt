package com.whitbread.premierinn.domain.graphql.requestBodyModels

data class BookingHistoryRequestBody(
        val business: Boolean,
        val includeCheckInBookings: Boolean,
        val sortOrder: String,
        val continuationToken: String?,
        val pageSize: Int,
        val pageIndex: Int,
        val bookingChannel: BookingChannelDetails,
)