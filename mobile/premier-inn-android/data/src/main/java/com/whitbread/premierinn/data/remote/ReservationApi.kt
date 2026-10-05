package com.whitbread.premierinn.data.remote

import io.reactivex.Single
import org.threeten.bp.LocalDate
import retrofit2.http.*


// TODO: Bart can remove?
interface ReservationApi {
    @GET("/reservation/hotels/{reservation-id}")
    fun getReservation(@Path("reservation-id") reservationId: String,
                       @Query("surname") surname: String,
                       @Query("arrival") arrivalDate: LocalDate,
                       @Header("bookingChannel") bookingChannel: String,
                       @Header("country") country: String,
                       @Header("language") language: String): Single<ReservationApiContract.ReservationResponse>

    @PUT("/reservation/hotels/{reservation-id}")
    fun updateAmendReservation(@Body amendReservationRequest: AmendReservationRequest.ReservationRequest,
                               @Path("reservation-id") reservationId: String,
                               @Query("arrival") arrivalDate: LocalDate,
                               @Query("brand") brand: String = "PI",
                               @Query("sessionId") sessionId: String,
                               @Header("bookingChannel") bookingChannel: String,
                               @Header("country") country: String,
                               @Header("language") language: String):
            Single<AmendReservationApiResponse.AmendReservationResponse>

    @PUT("/reservation/hotels/pending-amend/{pending-amend-id}")
    fun completePendingAmend(@Body completePendingAmendRequest: AmendReservationRequest.CompletePendingAmendRequest,
                             @Path("pending-amend-id") pendingAmendId: String):
            Single<AmendReservationApiResponse.AmendReservationResponse>
}