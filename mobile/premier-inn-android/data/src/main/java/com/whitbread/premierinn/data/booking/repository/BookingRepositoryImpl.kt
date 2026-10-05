package com.whitbread.premierinn.data.booking.repository

import com.whitbread.premierinn.businessbooker.data.remote.BusinessAccountApi
import com.whitbread.premierinn.data.booking.dao.BookingDao
import com.whitbread.premierinn.data.booking.mapToBooking
import com.whitbread.premierinn.data.booking.mapToBookingEntity
import com.whitbread.premierinn.data.booking.mapToBookingWithRooms
import com.whitbread.premierinn.data.booking.toBooking
import com.whitbread.premierinn.data.common.ErrorLogger
import com.whitbread.premierinn.data.common.persistence.SimplePersistenceManager
import com.whitbread.premierinn.domain.booking.entity.Booking
import com.whitbread.premierinn.domain.booking.entity.BookingHotelCountry
import com.whitbread.premierinn.domain.booking.entity.BookingStatus
import com.whitbread.premierinn.domain.booking.repository.BookingRepository
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import java.lang.Boolean.FALSE
import java.lang.Boolean.TRUE
import javax.inject.Inject

class BookingRepositoryImpl @Inject constructor(private val businessApi: BusinessAccountApi,
                                                val dao: BookingDao,
                                                private val simplePersistenceManager: SimplePersistenceManager,
                                                private val logger: ErrorLogger) : BookingRepository {


    override fun getLocalStaleBookings(staleSeconds: Int): Flowable<Booking> {
        return dao.getStaleBookings(staleSeconds)
                .flattenAsFlowable { it }
                .filter { !it.isLinkedToAccount }
                .map { it.mapToBooking(emptyList()) }
    }

    override fun bookingExist(bookingReference: String): Boolean {
        return if (dao.getBookingById(bookingReference) != null) TRUE else FALSE
    }


    override fun getBookingUpdates(bookingReference: String): Flowable<Booking> {
        return dao.getBookingWithRoomsUpdatesById(bookingReference)
                .map { list -> if (list.isEmpty()) throw NoSuchElementException() else list[0] } //This Rooms workaround see BookingDao method for Details
                .map { bookingEntity ->
                    bookingEntity.mapToBooking()
                }
    }

    override fun store(booking: Booking) {
        dao.insertOrReplaceBookingWithRooms(booking.mapToBookingWithRooms(), logger)
    }

    override fun storeUpdatedAmendedRoom(booking: Booking) {
        dao.deleteAllRooms(booking.bookingReference)

        dao.insertRooms(booking.mapToBookingWithRooms().rooms)
    }

    override fun clearAllBookings() {
        dao.deleteAll()
    }

    override fun clearAllRoomData() {
        dao.deleteAllRooms()
    }
    override fun getSortedNonPastBookingsUpdates(): Observable<List<Booking>> {
        return dao.getSortedBookings().map {
            it.toBooking()
        }
    }

    override fun store(bookings: List<Booking>): Completable {
        return Observable.fromIterable(bookings)
                .map { it.mapToBookingEntity() }
                .doOnNext { dao.insertOrReplace(it) }
                .ignoreElements()
    }

    override fun updateLinkedAccountBookings(bookings: List<Booking>, isBusinessBooker: Boolean): Completable {
        return Observable.fromIterable(bookings)
                .map { it.mapToBookingEntity() }
                .toList().toObservable()
                .doOnNext { dao.updateLinkedAccountBookings(it) }
                .ignoreElements()
    }

    override fun markAsCheckedIn(bookingReference: String): Completable {
        return Completable.fromAction { dao.markAsCheckedIn(bookingReference) }
    }

    override fun getUpcomingBooking(): Maybe<Booking> {
        return dao.getUpcomingBooking()
                .map { it.mapToBooking(emptyList()) }
    }

    override fun isEmpty(): Single<Boolean> {
        return Single.fromCallable {
            val count = dao.count()
            if (count > 0) {
                FALSE
            } else {
                TRUE
            }
        }
    }

    override fun countActiveBookings(): Int {
        return dao.countActiveBookings()
    }

    override fun storeBookingStatus(bookings: List<Booking>) {
        val bookingStatusList = bookings.map {
            if (it.isCancelled) BookingStatus(it.bookingReference, "CANCELLED")
            else BookingStatus(it.bookingReference, it.bookingStatus)
        }

        simplePersistenceManager.storeBookingStatus(bookingStatusList)
    }

    override fun storeBookingHotelCountry(bookings: List<Booking>) {
        val bookingHotelCountryList = bookings.map {
            it.hotelCountry?.let { hotelCountry ->
                BookingHotelCountry(it.bookingReference, hotelCountry)
            }
        }

        simplePersistenceManager.storeBookingHotelCountry(bookingHotelCountryList)
    }
}