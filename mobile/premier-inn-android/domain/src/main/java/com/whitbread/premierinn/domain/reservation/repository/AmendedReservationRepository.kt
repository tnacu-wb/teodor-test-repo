package com.whitbread.premierinn.domain.reservation.repository

import com.whitbread.premierinn.domain.common.PaymentDetails
import com.whitbread.premierinn.domain.common.UpsellAvailable
import com.whitbread.premierinn.domain.graphql.amend.entity.PackagesAndAncillaryCloseoutDomain
import com.whitbread.premierinn.domain.graphql.requestBodyModels.BookingChannelDetails
import com.whitbread.premierinn.domain.graphql.requestBodyModels.CancelInformationRequestBody
import com.whitbread.premierinn.domain.payment.AmendReservationDomainDetails
import com.whitbread.premierinn.domain.reservation.entity.Reservation
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import org.threeten.bp.LocalDate


interface AmendedReservationRepository {
//    TODO: Dashboard Not migrated to opera
//    fun trigger(reservationReference: String, surname: String, arrivalDate: LocalDate): Completable
    fun bookingConfirmationAndSaveBookingDetailsInDbAndSP(
        tempBasketReference: String, bookingRef: String, country: String,
        language: String, hotelName: String?,
        cancelInformationRequestBody: CancelInformationRequestBody,
        arrivalDate: String, departureDate: String,
        isAddRoom: Boolean, isRemoveRoom: Boolean, isBusinessBooking: Boolean, authToken: String?
    ): Single<PackagesAndAncillaryCloseoutDomain>
    fun bookingConfirmationAndManageBookingAmendFlowAndSaveBookingDetailsInDbAndSP(
        originalBasketReference: String, tempBasketReference: String,
        bookingRef: String,
        token: String, bookingChannel: BookingChannelDetails,
        country: String, language: String, hotelName: String?,
        cancelInformationRequestBody: CancelInformationRequestBody,
        arrivalDate: String, departureDate: String, authToken: String?
    ): Single<PackagesAndAncillaryCloseoutDomain>
    fun submit(reservationId: String, arrivalDate: LocalDate, surname: String, updatedReservation: Reservation,
               paymentDetails: PaymentDetails, cardSecurityCode: String, availableUpsells: List<UpsellAvailable>): Single<AmendReservationDomainDetails?>

    fun updateBooking(reservationId: String, arrival: LocalDate, departure: LocalDate, leadGuestSurname: String): Completable

    fun getOriginalReservation(): Reservation?
    fun getUpsellsAvailable(): Observable<List<UpsellAvailable>>
    fun clearReservation()
    fun getAmendedReservation(reservationId: String): Observable<Reservation>
    fun updateReservationDatesAndUpsells(reservationId: String, dates: Pair<LocalDate, LocalDate>, updatedReservation: Reservation): Completable
    fun updateReservation(reservationId: String, updatedReservation: Reservation): Completable
    fun updateReservationAddRoom(reservationId: String, updatedReservation: Reservation): Completable
    fun removeRoom(reservationId: String, bookingRef: String): Completable
    fun completePendingAmend(pares: String): Single<AmendReservationDomainDetails?>
    fun clearDaoLinkedWithAmend()
    fun clearAmendReservationWithResId(reservationId: String): Completable
}
