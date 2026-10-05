package com.whitbread.premierinn.domain.apprating.usecase

import com.whitbread.premierinn.domain.apprating.repository.AppRatingRepository
import com.whitbread.premierinn.domain.booking.repository.BookingRepository
import io.reactivex.Single
import javax.inject.Inject

/**
 * The app rating prompt should show on new booking creation when the app has not been rated yet
 * AND whether the customer has already 3 or more active bookings
 * OR when the customer has just made his 3rd, 5th or 7th booking since the introduction of appRating UseCase
 */
class AppRatingPromptIfNeeded @Inject constructor(
    private val bookingRepository: BookingRepository,
    private val repository: AppRatingRepository
) {
    fun execute(newBooking: Boolean): Single<Boolean> {
        return Single.fromCallable {
            (newBooking
                    && !repository.isAppRated()
                    && ((bookingRepository.countActiveBookings() >= 3 && repository.numberOfBookingsSinceAppRating() == 1)
                    || repository.numberOfBookingsSinceAppRating() == 3
                    || repository.numberOfBookingsSinceAppRating() == 5
                    || repository.numberOfBookingsSinceAppRating() == 7))
        }
    }
}