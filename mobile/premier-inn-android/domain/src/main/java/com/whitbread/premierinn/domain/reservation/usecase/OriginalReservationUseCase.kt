package com.whitbread.premierinn.domain.reservation.usecase

import com.whitbread.premierinn.domain.reservation.entity.Reservation
import com.whitbread.premierinn.domain.reservation.repository.AmendedReservationRepository
import javax.inject.Inject

class OriginalReservationUseCase @Inject constructor(
        private val repository: AmendedReservationRepository) {

    operator fun invoke(): Reservation? {
        return repository.getOriginalReservation()
    }
}