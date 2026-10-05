package com.whitbread.premierinn.domain.booking.usecase

import com.whitbread.premierinn.domain.booking.entity.Booking
import com.whitbread.premierinn.domain.booking.repository.BookingRepository
import io.reactivex.Flowable
import javax.inject.Inject

class GetBookingUpdates @Inject constructor(private val repository: BookingRepository) {

    fun execute(bookingReference: String): Flowable<Booking> {
        return repository.getBookingUpdates(bookingReference)
    }
}