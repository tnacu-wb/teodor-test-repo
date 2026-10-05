package com.whitbread.premierinn.domain.booking.repository

import com.whitbread.premierinn.domain.booking.entity.Booking
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single

interface BookingRepository {
    fun isEmpty(): Single<Boolean>
    fun getLocalStaleBookings(staleSeconds: Int): Flowable<Booking>
    fun countActiveBookings(): Int
    fun bookingExist(bookingReference: String): Boolean
    fun clearAllBookings()
    fun clearAllRoomData()
    fun store(booking: Booking)
    fun store(bookings: List<Booking>): Completable
    fun storeUpdatedAmendedRoom(booking: Booking)

    fun updateLinkedAccountBookings(bookings: List<Booking>, isBusinessBooker: Boolean): Completable

    fun getBookingUpdates(bookingReference: String): Flowable<Booking>
    fun getSortedNonPastBookingsUpdates(): Observable<List<Booking>>
    fun markAsCheckedIn(bookingReference: String): Completable

    fun getUpcomingBooking(): Maybe<Booking>
    fun storeBookingStatus(bookings: List<Booking>)
    fun storeBookingHotelCountry(bookings: List<Booking>)
}