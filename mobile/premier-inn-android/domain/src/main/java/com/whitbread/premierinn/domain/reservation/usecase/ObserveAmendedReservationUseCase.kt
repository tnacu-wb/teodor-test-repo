package com.whitbread.premierinn.domain.reservation.usecase

import com.whitbread.premierinn.domain.reservation.entity.Reservation
import com.whitbread.premierinn.domain.reservation.repository.AmendedReservationRepository
import io.reactivex.Observable
import javax.inject.Inject

class ObserveAmendedReservationUseCase @Inject constructor(
        private val repository: AmendedReservationRepository) {

    operator fun invoke(reservationId: String): Observable<Reservation> {
        return repository.getAmendedReservation(reservationId)
    }
}