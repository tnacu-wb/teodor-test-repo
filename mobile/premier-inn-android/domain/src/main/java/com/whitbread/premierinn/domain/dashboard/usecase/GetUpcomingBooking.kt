package com.whitbread.premierinn.domain.dashboard.usecase

import com.whitbread.premierinn.domain.booking.entity.Booking
import com.whitbread.premierinn.domain.booking.repository.BookingRepository
import io.reactivex.Maybe
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class GetUpcomingBooking @Inject constructor(private val repository: BookingRepository) {

    fun execute(): Maybe<Booking> {
        return repository.getUpcomingBooking()
            .subscribeOn(Schedulers.io())
    }
}