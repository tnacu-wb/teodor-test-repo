package com.whitbread.premierinn.domain.booking.usecase

import com.whitbread.premierinn.domain.apprating.repository.AppRatingRepository
import com.whitbread.premierinn.domain.booking.entity.Booking
import com.whitbread.premierinn.domain.booking.repository.BookingRepository
import io.reactivex.Completable
import javax.inject.Inject

class StoreBookingMadeOnApp @Inject constructor(private val bookingRepository: BookingRepository,
                                                private val appRatingRepository: AppRatingRepository) {
    fun execute(booking: Booking): Completable {
        return Completable.fromCallable {
            bookingRepository.store(booking)
            appRatingRepository.incrementNumberOfBookings()
        }
    }
}