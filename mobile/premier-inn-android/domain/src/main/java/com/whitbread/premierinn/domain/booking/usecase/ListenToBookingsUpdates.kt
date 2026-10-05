package com.whitbread.premierinn.domain.booking.usecase

import com.whitbread.premierinn.domain.booking.entity.Booking
import com.whitbread.premierinn.domain.booking.repository.BookingRepository
import io.reactivex.Observable
import javax.inject.Inject

/**
 *
 */
class ListenToBookingsUpdates @Inject constructor(private val repository: BookingRepository) {

    fun execute(): Observable<List<Booking>> {
        return repository.getSortedNonPastBookingsUpdates()
    }
}