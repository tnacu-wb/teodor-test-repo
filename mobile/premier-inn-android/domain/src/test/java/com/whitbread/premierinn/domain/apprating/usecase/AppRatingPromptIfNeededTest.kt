package com.whitbread.premierinn.domain.apprating.usecase

import com.whitbread.premierinn.domain.apprating.repository.AppRatingRepository
import com.whitbread.premierinn.domain.booking.repository.BookingRepository
import junitparams.JUnitParamsRunner
import junitparams.Parameters
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock

@RunWith(JUnitParamsRunner::class)
class AppRatingPromptIfNeededTest {

    @Test
    @Parameters(method = "bookingAttemptInputs")
    fun `Should return TRUE WHEN booking has just been made AND App is not rated AND numberOfBookings since appRating is 3, 5 or 7 `(bookingAttempt: Int) {
        val repository = mock<AppRatingRepository> {
            on { isAppRated() } doReturn false
            on { numberOfBookingsSinceAppRating() } doReturn bookingAttempt
        }
        val bookingRepository = mock<BookingRepository> {
            on { countActiveBookings() } doReturn 0
        }
        val promptIfNeeded = AppRatingPromptIfNeeded(repository = repository, bookingRepository = bookingRepository)

        promptIfNeeded.execute(newBooking = true).test()
                .assertValue(true)
                .assertComplete()
    }

    fun bookingAttemptInputs(): Any {
        return arrayOf(3, 5, 7)
    }


    @Test
    fun `Should return TRUE WHEN booking has just been made AND App is not rated AND customer has already 3 or more active bookings on first attempt since AppRating prompt`() {
        val repository = mock<AppRatingRepository> {
            on { isAppRated() } doReturn false
            on { numberOfBookingsSinceAppRating() } doReturn 1
        }
        val bookingRepository = mock<BookingRepository> {
            on { countActiveBookings() } doReturn 3
        }
        val promptIfNeeded = AppRatingPromptIfNeeded(repository = repository, bookingRepository = bookingRepository)

        promptIfNeeded.execute(newBooking = true).test()
                .assertValue(true)
                .assertComplete()
    }

    @Test
    fun `Should return FALSE WHEN App is already rated`() {
        val repository = mock<AppRatingRepository> {
            on { isAppRated() } doReturn true
            on { numberOfBookingsSinceAppRating() } doReturn 3
        }
        val bookingRepository = mock<BookingRepository> {
            on { countActiveBookings() } doReturn 0
        }
        val promptIfNeeded = AppRatingPromptIfNeeded(repository = repository, bookingRepository = bookingRepository)

        promptIfNeeded.execute(newBooking = true).test()
                .assertValue(false)
                .assertComplete()
    }

    @Test
    @Parameters(method = "bookingFailedAttemptInputs")
    fun `Should return FALSE WHEN numberOfBookingsSince is any number other than 3, 5 or 7`(bookingAttempt: Int) {
        val repository = mock<AppRatingRepository> {
            on { isAppRated() } doReturn false
            on { numberOfBookingsSinceAppRating() } doReturn bookingAttempt
        }
        val bookingRepository = mock<BookingRepository> {
            on { countActiveBookings() } doReturn 0
        }
        val promptIfNeeded = AppRatingPromptIfNeeded(repository = repository, bookingRepository = bookingRepository)

        promptIfNeeded.execute(newBooking = true).test()
                .assertValue(false)
                .assertComplete()
    }

    fun bookingFailedAttemptInputs(): Any {
        return arrayOf(1, 2, 4, 6, 8, 9, 10)
    }


    @Test
    fun `Should return FALSE When there is no new Booking`() {
        val repository = mock<AppRatingRepository> {
            on { isAppRated() } doReturn false
        }
        val bookingRepository = mock<BookingRepository> {
            on { countActiveBookings() } doReturn 0
        }
        val promptIfNeeded = AppRatingPromptIfNeeded(repository = repository, bookingRepository = bookingRepository)

        promptIfNeeded.execute(newBooking = false).test()
                .assertValue(false)
                .assertComplete()
    }
}