package com.whitbread.premierinn.domain.reservation.usecase

import com.whitbread.premierinn.domain.reservation.repository.AmendedReservationRepository
import javax.inject.Inject

class ClearReservationUseCase @Inject constructor(
        private val repository: AmendedReservationRepository) {

    operator fun invoke() {
        return repository.clearReservation()
    }
}