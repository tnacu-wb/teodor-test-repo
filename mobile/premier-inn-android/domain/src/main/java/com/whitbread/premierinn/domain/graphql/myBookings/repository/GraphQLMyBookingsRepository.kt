package com.whitbread.premierinn.domain.graphql.myBookings.repository

import com.whitbread.premierinn.domain.booking.entity.Booking
import com.whitbread.premierinn.domain.graphql.requestBodyModels.BookingHistoryRequestBody
import io.reactivex.Single

interface GraphQLMyBookingsRepository {

    fun getBookingHistory(input: BookingHistoryRequestBody, token: String): Single<List<Booking>>
}