package com.whitbread.premierinn.domain.reservation.usecase

import com.whitbread.premierinn.domain.common.UpsellAvailable
import com.whitbread.premierinn.domain.reservation.repository.AmendedReservationRepository
import io.reactivex.Observable
import javax.inject.Inject

class UpsellsAvailableUseCase @Inject constructor(private val repository: AmendedReservationRepository) {
    operator fun invoke(): Observable<List<UpsellAvailable>> {
        return repository.getUpsellsAvailable()
    }
}