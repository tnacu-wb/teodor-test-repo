package com.whitbread.premierinn.domain.reservation.usecase

import com.whitbread.premierinn.domain.booking.entity.Booking
import com.whitbread.premierinn.domain.common.EMPLOYEE_CODE
import com.whitbread.premierinn.domain.common.Guest
import com.whitbread.premierinn.domain.common.RatePlanOpera
import com.whitbread.premierinn.domain.common.RoomCriteria
import com.whitbread.premierinn.domain.common.SUB_CHANNEL
import com.whitbread.premierinn.domain.common.convertToRatePlansOperaForAmend
import com.whitbread.premierinn.domain.common.findSelectedRatePlan
import com.whitbread.premierinn.domain.common.hoteldetails.repository.GraphQLHotelDetailsRepository
import com.whitbread.premierinn.domain.common.returnSelectedRatePlan
import com.whitbread.premierinn.domain.common.toListOfRoomSearch
import com.whitbread.premierinn.domain.graphql.requestBodyModels.BookingChannelDetails
import com.whitbread.premierinn.domain.graphql.requestBodyModels.Channel
import com.whitbread.premierinn.domain.graphql.requestBodyModels.HotelAvailabilityRequestBody
import com.whitbread.premierinn.domain.graphql.requestBodyModels.HotelInfoDetails
import com.whitbread.premierinn.domain.reservation.entity.Reservation
import com.whitbread.premierinn.domain.reservation.repository.AmendedReservationRepository
import io.reactivex.Observable
import java.util.Collections
import java.util.Locale
import javax.inject.Inject

class AddRoomUseCase @Inject constructor(
        private val repository: AmendedReservationRepository,
        private val graphQLHotelDetailsRepository: GraphQLHotelDetailsRepository
) {
    fun invoke(reservationId: String,
               listOfBookingRooms: List<Booking.Room>,
               updatedCriteria: RoomCriteria,
               updatedLeadGuest: Guest,
               addedRoomNumber: Int,
               deviceLocale: Locale,
               hotelBrand: String,
               selectedRatePlan: String,
               isEmployeeBooking: Boolean): Observable<AddARoomState> {
        val listOfRatePlanCodes = if (isEmployeeBooking) listOf(EMPLOYEE_CODE) else emptyList()

        return repository.getAmendedReservation(reservationId).take(1)
            .flatMap { reservation ->
                createAmendedReservation(reservation,
                    listOfBookingRooms,
                    addedRoomNumber,
                    updatedLeadGuest,
                    updatedCriteria,
                    deviceLocale,
                    hotelBrand, selectedRatePlan, listOfRatePlanCodes)
            }
            .startWith(AddARoomState.Loading)
    }

    private fun createAmendedReservation(originalReservation: Reservation,
                                         listOfBookingRooms: List<Booking.Room>,
                                         addedRoomNumber: Int,
                                         updatedLeadGuest: Guest,
                                         updatedCriteria: RoomCriteria,
                                         deviceLocale: Locale,
                                         hotelBrand: String,
                                         selectedRatePlan: String,
                                         listOfRatePlanCodes: List<String>): Observable<AddARoomState> {
        val roomCriteriaNewList = mutableListOf<RoomCriteria>()
        val roomGuestNewList = mutableListOf<Guest>()

        if (originalReservation.roomsCriteria.size + 1 == addedRoomNumber) {
            originalReservation.roomsCriteria.mapIndexed { index, originalCriteria ->
                if (isEndOfList(index, addedRoomNumber)) {
                    roomCriteriaNewList.add(index, originalCriteria)
                    roomCriteriaNewList.add(index + 1, updatedCriteria)
                } else {
                    roomCriteriaNewList.add(index, originalCriteria)
                }
            }
        }

        if (originalReservation.roomsLeadGuest.size + 1 == addedRoomNumber) {
            originalReservation.roomsLeadGuest.mapIndexed { index, originalLeadGuest ->
                if (isEndOfList(index, addedRoomNumber)) {
                    roomGuestNewList.add(index, originalLeadGuest)
                    roomGuestNewList.add(index + 1, updatedLeadGuest)
                } else {
                    roomGuestNewList.add(index, originalLeadGuest)
                }
            }
        }

        val updatedReservation = originalReservation.copy(
            roomsCriteria = roomCriteriaNewList,
            roomsLeadGuest = roomGuestNewList
        )
        return addRoomUpdateWithAvailabilityCheck(
                updatedReservation,
                updatedCriteria,
                listOfBookingRooms,
                deviceLocale,
                hotelBrand,
                selectedRatePlan,
                listOfRatePlanCodes
            )
        }

fun addRoomUpdateWithAvailabilityCheck(
        updatedReservation: Reservation,
        updatedCriteria: RoomCriteria,
        listOfBookingRooms: List<Booking.Room>,
        deviceLocale: Locale,
        hotelBrand: String,
        selectedRatePlan: String,
        listOfRatePlanCodes: List<String>
    ): Observable<AddARoomState> {
        return when {
            updatedReservation.roomsLeadGuest.isNotEmpty() -> {
                graphQLHotelDetailsRepository.getHotelAvailability(
                    HotelAvailabilityRequestBody(
                        updatedReservation.arrival.toString(),
                        updatedReservation.departure.toString(),
                        HotelInfoDetails(updatedReservation.hotelCode),
                        Collections.singletonList(updatedCriteria).toListOfRoomSearch(),
                        BookingChannelDetails(Channel.PI.name, SUB_CHANNEL, deviceLocale.language),
                        hotelBrand, listOfRatePlanCodes, null), null)
                    .flatMapObservable { hotelAvailabilityDomain ->
                        if (!hotelAvailabilityDomain.error.isNullOrEmpty()) {
                            createUnAvailableError(false)
                        } else if (hotelAvailabilityDomain.available) {
                            if (hotelAvailabilityDomain.findSelectedRatePlan(selectedRatePlan) != null) {
                                val availabilityReturned =
                                    hotelAvailabilityDomain.returnSelectedRatePlan(selectedRatePlan)
                                if (availabilityReturned != null) {
                                    Observable.just<AddARoomState>(
                                        AddARoomState.AvailabilityUpdated
                                            (availabilityReturned.convertToRatePlansOperaForAmend(
                                            listOfBookingRooms
                                        ), updatedReservation)
                                    )
                                } else {
                                    createUnAvailableError(true)

                                }
                            } else {
                                createUnAvailableError(true)
                            }
                        } else {
                            createUnAvailableError(true)
                        }

                    }.onErrorReturn { e -> AddARoomState.Error(exception = e) }
            }
            else -> {
                createUnAvailableError(false)
            }
        }
    }

    private fun createUnAvailableError(soldOut: Boolean): Observable<AddARoomState> {
        return when (soldOut) {
            true -> Observable.just(AddARoomState.NoAvailability("NO Availability"))
            false -> Observable.just(AddARoomState.Error())
        }
    }

    companion object {
        /**
         * Checks if end of existing list is reached to add the new element
         * e.g for an existing list with indices 0,1,2 where room numbers are 1,2,3 the added room number should be 4
         */
        fun isEndOfList(index: Int, addedRoomNumber: Int) = index + 2 == addedRoomNumber
    }

    sealed class AddARoomState {
        object Loading : AddARoomState()
        data class AvailabilityUpdated(val ratePlanOpera: RatePlanOpera, val updatedReservation: Reservation? = null) : AddARoomState()
        data class NoAvailability(val message: String?) : AddARoomState()
        data class Error(val exception: Throwable? = null, val msg: String? = null) : AddARoomState()
    }
}