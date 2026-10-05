package com.whitbread.premierinn.domain.reservation.usecase

import com.whitbread.premierinn.domain.authentication.repository.AuthenticationRepository
import com.whitbread.premierinn.domain.authentication.usecase.GetFreshIdTokenAndRetryOnce
import com.whitbread.premierinn.domain.booking.entity.Booking
import com.whitbread.premierinn.domain.common.DOUBLE_ROOM_CODE
import com.whitbread.premierinn.domain.common.EMPLOYEE_CODE
import com.whitbread.premierinn.domain.common.EMPTY_STRING_DOMAIN
import com.whitbread.premierinn.domain.common.FAMILY_ROOM_CODE
import com.whitbread.premierinn.domain.common.Guest
import com.whitbread.premierinn.domain.common.RatePlanOpera
import com.whitbread.premierinn.domain.common.RoomCriteria
import com.whitbread.premierinn.domain.common.RoomType
import com.whitbread.premierinn.domain.common.SUB_CHANNEL
import com.whitbread.premierinn.domain.common.convertToRatePlansOperaForAmend
import com.whitbread.premierinn.domain.common.findSelectedRatePlan
import com.whitbread.premierinn.domain.common.hoteldetails.repository.GraphQLHotelDetailsRepository
import com.whitbread.premierinn.domain.common.returnSelectedRatePlan
import com.whitbread.premierinn.domain.common.toListOfRoomSearch
import com.whitbread.premierinn.domain.graphql.amend.usecase.GraphQLAmendUseCase
import com.whitbread.premierinn.domain.graphql.hdp.entity.HotelAvailabilityDomain
import com.whitbread.premierinn.domain.graphql.requestBodyModels.AmendEditRoomRequestBody
import com.whitbread.premierinn.domain.graphql.requestBodyModels.BookingChannelDetails
import com.whitbread.premierinn.domain.graphql.requestBodyModels.Channel
import com.whitbread.premierinn.domain.graphql.requestBodyModels.HotelAvailabilityRequestBody
import com.whitbread.premierinn.domain.graphql.requestBodyModels.HotelInfoDetails
import com.whitbread.premierinn.domain.graphql.requestBodyModels.LeadGuestAmend
import com.whitbread.premierinn.domain.graphql.requestBodyModels.RoomOccupancyAmend
import com.whitbread.premierinn.domain.reservation.entity.Reservation
import com.whitbread.premierinn.domain.reservation.repository.AmendedReservationRepository
import io.reactivex.Observable
import io.reactivex.Single
import java.util.Locale
import javax.inject.Inject

class StoreUpdatedAmendedReservation @Inject constructor(private val repository: AmendedReservationRepository,
                                                         private val graphQLHotelDetailsRepository: GraphQLHotelDetailsRepository,
                                                         private val graphQLAmendUseCase: GraphQLAmendUseCase,
                                                         private val authenticationRepository: AuthenticationRepository,
                                                         private val getFreshIdTokenAndRetryOnce: GetFreshIdTokenAndRetryOnce
) {

    operator fun invoke(
        originalReservation: Reservation,
        amendedReservation: Reservation,
        deviceLocale: Locale,
        bookingChannel: String,
        hotelBrand: String,
        selectedRatePlan: String,
        listOfBookingRooms: List<Booking.Room>,
        roomId: String,
        tempBasketRef: String,
        token: String,
        updatedLeadGuest: Guest,
        updatedRoomCriteria: RoomCriteria,
        isEmployeeBooking: Boolean,
        isBusinessBooking: Boolean
    ): Observable<UpdateState> {
        val listOfRatePlanCodes = if (isEmployeeBooking) listOf(EMPLOYEE_CODE) else emptyList()

        return when {
            requiresAvailabilityCheck(originalReservation, amendedReservation) -> {
                    updateWithAvailabilityCheck(originalReservation,
                        amendedReservation,
                        deviceLocale,
                        hotelBrand,
                        selectedRatePlan,
                        listOfBookingRooms,
                        updatedLeadGuest,
                        listOfRatePlanCodes,
                        bookingChannel,
                        isBusinessBooking)
            }
            else -> {
                    performUpdateForOpera(originalReservation ,originalReservation.bookingReference,
                        amendedReservation, tempBasketRef, token, roomId, updatedLeadGuest,
                        updatedRoomCriteria, deviceLocale, isBusinessBooking)
            }
        }.startWith(UpdateState.Loading)
    }

    fun performUpdate(reservationId: String, updatedReservation: Reservation): Observable<UpdateState> {
        return repository.updateReservation(reservationId, updatedReservation)
            .andThen(Single.just<UpdateState>(UpdateState.Updated))
            .toObservable()
            .onErrorReturn { e -> UpdateState.Error(exception = e) }
    }

    private fun performUpdateForOpera(
        originalReservation: Reservation,
        reservationId: String,
        updatedReservation: Reservation,
        tempBasketRef: String,
        token: String,
        roomId: String,
        updatedLeadGuest: Guest,
        updatedRoomCriteria: RoomCriteria,
        deviceLocale: Locale,
        isBusinessBooking: Boolean
    ): Observable<UpdateState> {
        val leadGuestOrig =
            originalReservation.roomsLeadGuest.find { it.roomId == updatedLeadGuest.roomId }

        val mapToDoubleIfFamilyRoomDoesntContainChild = when {
            updatedRoomCriteria.roomType.code == FAMILY_ROOM_CODE && updatedRoomCriteria.numberOfChildren == 0 -> DOUBLE_ROOM_CODE
            else -> updatedRoomCriteria.roomType.code
        }

        if (updatedLeadGuest != leadGuestOrig) {
            return graphQLAmendUseCase.amendEditRoom(
                AmendEditRoomRequestBody(
                    bookingChannel = BookingChannelDetails(
                        if (isBusinessBooking) Channel.BB.name else Channel.PI.name,
                        SUB_CHANNEL,
                        deviceLocale.language
                    ),
                    tempBookingRef = tempBasketRef,
                    reservationId = roomId,
                    roomOccupancy = RoomOccupancyAmend(
                        adultsNumber = updatedRoomCriteria.numberOfAdults,
                        childrenNumber = updatedRoomCriteria.numberOfChildren,
                        cotRequired = false
                    ),
                    leadGuest = LeadGuestAmend(
                        title = updatedLeadGuest.title,
                        firstName = updatedLeadGuest.firstName,
                        lastName = updatedLeadGuest.lastName,
                        emailAddress = updatedLeadGuest.emailAddress
                    ),
                    roomType = mapToDoubleIfFamilyRoomDoesntContainChild,
                    token = token
                ),
                isBusinessBooking
            ).flatMapObservable { tempBookRefRes ->
                if (tempBookRefRes.tempBookingRef.isNotEmpty()) {
                    repository.updateReservation(reservationId, updatedReservation)
                        .andThen(Single.just<UpdateState>(UpdateState.Updated))
                        .toObservable()
                        .onErrorReturn { e -> UpdateState.Error(exception = e) }
                } else {
                    Observable.just<UpdateState>(UpdateState.Error(null, "Unable to amend now"))
                }
            }
        } else {
            return repository.updateReservation(reservationId, updatedReservation)
                .andThen(Single.just<UpdateState>(UpdateState.Updated))
                .toObservable()
                .onErrorReturn { e -> UpdateState.Error(exception = e) }
        }
    }

    private fun performUpdateWithPriceUpdateOpera(
        bookingReference: String,
        updatedReservation: Reservation,
        ratePlanOpera: RatePlanOpera,
        updatedLeadGuest: Guest,
        originalReservation: Reservation
    ): Observable<UpdateState> {
        val leadGuestFromUpdRes = originalReservation.roomsLeadGuest.find { it.roomId == updatedLeadGuest.roomId }
        return if (updatedLeadGuest == leadGuestFromUpdRes) {
            Observable.just(UpdateState.AvailabilityUpdated(ratePlanOpera = ratePlanOpera,
                updatedReservation = updatedReservation))
        } else {
            repository.updateReservation(bookingReference, updatedReservation)
                .andThen(Single.just<UpdateState>(UpdateState.AvailabilityUpdated(ratePlanOpera = ratePlanOpera,
                    updatedReservation = updatedReservation)))
                .toObservable()
                .onErrorReturn { e -> UpdateState.Error(exception = e) }
        }

    }

    private fun requiresAvailabilityCheck(originalReservation: Reservation, updatedReservation: Reservation): Boolean {
        val updatedRoomCriteria = updatedReservation.roomsCriteria
        for (index in updatedRoomCriteria.indices) {
                if (requiresAvailabilityCheck(originalReservation.roomsCriteria[index], updatedRoomCriteria[index]) ||
                    isNumberOfGuestCriteriaChanged(originalReservation.roomsCriteria[index], updatedRoomCriteria[index])) {
                    return true
                }
        }
        return false
    }

    private fun requiresAvailabilityCheck(previousRoomCriteria: RoomCriteria, updatedRoomCriteria: RoomCriteria): Boolean {
        return previousRoomCriteria.roomType != updatedRoomCriteria.roomType || previousRoomCriteria.includeCot != updatedRoomCriteria.includeCot
    }

    private fun isNumberOfGuestCriteriaChanged(previousRoomCriteria: RoomCriteria, updatedRoomCriteria: RoomCriteria): Boolean {
        return previousRoomCriteria.numberOfAdults != updatedRoomCriteria.numberOfAdults || previousRoomCriteria.numberOfChildren != updatedRoomCriteria.numberOfChildren
    }

    private fun updateWithAvailabilityCheck(
        originalReservation: Reservation,
        updatedReservation: Reservation,
        deviceLocale: Locale,
        hotelBrand: String,
        selectedRatePlan: String,
        listOfBookingRooms: List<Booking.Room>,
        updatedLeadGuest: Guest,
        listOfRatePlanCodes: List<String>,
        bookingChannel: String,
        isBusinessBooking: Boolean
    ): Observable<UpdateState> {
        val availabilityObservable: Observable<HotelAvailabilityDomain> = if (isBusinessBooking) {
            authenticationRepository.getIdToken()
                .toFlowable()
                .retryWhen(getFreshIdTokenAndRetryOnce())
                .flatMapSingle { freshToken ->
                    graphQLHotelDetailsRepository.getHotelAvailability(
                        HotelAvailabilityRequestBody(
                            updatedReservation.arrival.toString(),
                            updatedReservation.departure.toString(),
                            HotelInfoDetails(updatedReservation.hotelCode),
                            updatedReservation.roomsCriteria.toListOfRoomSearch(),
                            BookingChannelDetails(
                                bookingChannel,
                                SUB_CHANNEL,
                                deviceLocale.language
                            ),
                            hotelBrand,
                            listOfRatePlanCodes,
                            null
                        ),
                        freshToken
                    )
                }
                .toObservable()
        } else {
            graphQLHotelDetailsRepository.getHotelAvailability(
                HotelAvailabilityRequestBody(
                    updatedReservation.arrival.toString(),
                    updatedReservation.departure.toString(),
                    HotelInfoDetails(updatedReservation.hotelCode),
                    updatedReservation.roomsCriteria.toListOfRoomSearch(),
                    BookingChannelDetails(bookingChannel, SUB_CHANNEL, deviceLocale.language),
                    hotelBrand,
                    listOfRatePlanCodes,
                    null
                ),
                null
            ).toObservable()
        }

        return availabilityObservable
            .flatMap { hotelAvailabilityDomain ->
                if (!hotelAvailabilityDomain.error.isNullOrEmpty()) {
                    createUnAvailableError(originalReservation, updatedReservation)
                } else if (hotelAvailabilityDomain.available) {
                    if (hotelAvailabilityDomain.findSelectedRatePlan(selectedRatePlan) != null) {
                        val availabilityReturned =
                            hotelAvailabilityDomain.returnSelectedRatePlan(selectedRatePlan)
                        if (availabilityReturned != null) {
                            performUpdateWithPriceUpdateOpera(
                                originalReservation.bookingReference,
                                updatedReservation,
                                availabilityReturned.convertToRatePlansOperaForAmend(
                                    listOfBookingRooms
                                ),
                                updatedLeadGuest,
                                originalReservation
                            )
                        } else {
                            createUnAvailableError(originalReservation, updatedReservation)
                        }
                    } else {
                        createUnAvailableError(originalReservation, updatedReservation)
                    }
                } else {
                    createUnAvailableError(originalReservation, updatedReservation)
                }
            }
            .onErrorReturn { UpdateState.Error(exception = it) }
    }

    private fun createUnAvailableError(originalReservation: Reservation, updatedReservation: Reservation): Observable<UpdateState> {
        val updatedRoomType = getUpdatedRoomType(originalReservation, updatedReservation)
        return updatedRoomType?.let {
            Observable.just(UpdateState.NoAvailability(it))
        } ?: run {
            Observable.just(UpdateState.Error())
        }
    }

    private fun getUpdatedRoomType(originalReservation: Reservation, updatedReservation: Reservation): RoomType? {
        val updatedRoomTypes = updatedReservation.roomsCriteria.sortedBy { it.roomId }
        val originalRoomTypes = originalReservation.roomsCriteria.sortedBy { it.roomId }
        for (i in updatedRoomTypes.indices) {
            if (requiresAvailabilityCheck(originalRoomTypes[i], updatedRoomTypes[i])) {
                return updatedRoomTypes[i].roomType
            }
        }
        return null
    }

    sealed class UpdateState {
        object Loading : UpdateState()
        object Updated : UpdateState()
        data class AvailabilityUpdated(val ratePlanOpera: RatePlanOpera, val updatedReservation: Reservation? = null) : UpdateState()
        data class NoAvailability(val roomType: RoomType) : UpdateState()
        data class Error(val exception: Throwable? = null, val msg: String? = null) : UpdateState()
    }
}