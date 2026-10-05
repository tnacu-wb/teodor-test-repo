package com.whitbread.premierinn.domain.reservation.usecase.meals

import com.whitbread.premierinn.domain.common.UpsellAvailable
import com.whitbread.premierinn.domain.reservation.createAmendedUpsells
import com.whitbread.premierinn.domain.reservation.createBreakfast
import com.whitbread.premierinn.domain.reservation.entity.RoomSelectedUpsells
import com.whitbread.premierinn.domain.reservation.entity.Reservation
import com.whitbread.premierinn.domain.reservation.repository.AmendedReservationRepository
import io.reactivex.Observable
import io.reactivex.Single
import javax.inject.Inject

class UpdateAllRoomUpsellUseCase @Inject constructor(private val repository: AmendedReservationRepository) {

    operator fun invoke(reservationId: String, selectedUpsell: UpsellAvailable, fromDashboard: Boolean): Observable<UpdateUpsellState> {
        return repository.getAmendedReservation(reservationId)
                .flatMap { reservation ->
                    updatedReservationAvailability(reservationId, reservation, selectedUpsell, fromDashboard)
                }
                .startWith(UpdateUpsellState.Loading)
    }

    operator fun invoke(reservationId: String, selectedUpsells: List<RoomSelectedUpsells>): Observable<UpdateUpsellState> {
        return repository.getAmendedReservation(reservationId)
            .take(1)
            .flatMap { reservation ->
                updatedReservationAvailability(reservationId, reservation, selectedUpsells)
            }
            .startWith(UpdateUpsellState.Loading)
    }

    private fun updatedReservationAvailability(reservationId: String, reservation: Reservation, selectedUpsells: List<RoomSelectedUpsells>): Observable<UpdateUpsellState> {
        val updatedReservation = reservation.copy(upsells = createAmendedUpsells(selectedUpsells, reservation.departure, reservation.roomsCriteria))
        return repository.updateReservation(reservationId, updatedReservation)
            .andThen(Single.just<UpdateUpsellState>(UpdateUpsellState.Updated) )
            .toObservable()
            .onErrorReturn { error -> UpdateUpsellState.Error(exception = error) }
    }

    private fun updatedReservationAvailability(reservationId: String, reservation: Reservation, selectedUpsell: UpsellAvailable, fromDashboard: Boolean): Observable<UpdateUpsellState> {
        val updatedReservation = reservation.copy(upsells = createBreakfast(selectedUpsell,
            reservation.departure, reservation.roomsCriteria, reservation.upsells))
        return repository.updateReservation(reservationId, updatedReservation)
                .andThen(
                        when { fromDashboard -> { Single.just<UpdateUpsellState>(UpdateUpsellState.NavigateToAmendReview) }
                            else -> { Single.just<UpdateUpsellState>(UpdateUpsellState.Updated) } })
                .toObservable()
                .onErrorReturn { error -> UpdateUpsellState.Error(exception = error) }
    }

    sealed class UpdateUpsellState {
        object Idle : UpdateUpsellState()
        object Loading : UpdateUpsellState()
        object Updated : UpdateUpsellState()
        object NavigateToAmendReview : UpdateUpsellState()
        data class Error(val exception: Throwable? = null, val msg: String? = null) : UpdateUpsellState()
    }
}