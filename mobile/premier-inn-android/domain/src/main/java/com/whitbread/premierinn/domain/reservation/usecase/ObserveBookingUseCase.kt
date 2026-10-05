package com.whitbread.premierinn.domain.reservation.usecase

import com.whitbread.premierinn.domain.booking.entity.Booking
import com.whitbread.premierinn.domain.booking.repository.BookingRepository
import io.reactivex.Observable
import javax.inject.Inject

class ObserveBookingUseCase @Inject constructor(
        private val bookingRepository: BookingRepository) {

    operator fun invoke(reservationId: String): Observable<Booking> {
        return bookingRepository.getBookingUpdates(reservationId).take(1).toObservable()
    }
}