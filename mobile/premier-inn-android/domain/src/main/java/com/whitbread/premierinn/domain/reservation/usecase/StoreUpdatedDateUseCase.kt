package com.whitbread.premierinn.domain.reservation.usecase

import com.whitbread.premierinn.domain.common.RatePlan
import com.whitbread.premierinn.domain.reservation.createRoomsBreakdown
import com.whitbread.premierinn.domain.reservation.entity.Reservation
import com.whitbread.premierinn.domain.reservation.repository.AmendedReservationRepository
import io.reactivex.Observable
import io.reactivex.Single
import org.threeten.bp.LocalDate
import javax.inject.Inject

class StoreUpdatedDateUseCase @Inject constructor(
        private val repository: AmendedReservationRepository
) {
    operator fun invoke(reservationId: String, updatedDates: Pair<LocalDate, LocalDate>, ratePlan: RatePlan): Observable<UpdateDateState> {
        return repository.getAmendedReservation(reservationId)
                .flatMap { reservation ->
                    updatedReservationAvailability(reservationId, reservation, updatedDates, ratePlan)
                }
                .startWith(UpdateDateState.Loading)
    }


    private fun updatedReservationAvailability(reservationId: String, reservation: Reservation,
                                               updatedDates: Pair<LocalDate, LocalDate>, ratePlan: RatePlan): Observable<UpdateDateState> {
        val updatedReservation = reservation.copy(roomsBreakdown = createRoomsBreakdown(ratePlan.roomList, reservation.roomsBreakdown),
            arrival = updatedDates.first, departure = updatedDates.second)

        return repository.updateReservationDatesAndUpsells(reservationId, updatedDates, updatedReservation)
                .andThen(Single.just<UpdateDateState>(UpdateDateState.Updated))
                .toObservable()
                .onErrorReturn { e -> UpdateDateState.Error(exception = e) }
    }

    sealed class UpdateDateState {
        object Loading : UpdateDateState()
        object Updated : UpdateDateState()
        data class Error(val exception: Throwable? = null, val msg: String? = null) : UpdateDateState()
    }
}
