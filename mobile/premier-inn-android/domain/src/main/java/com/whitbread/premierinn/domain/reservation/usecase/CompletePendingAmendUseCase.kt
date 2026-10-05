package com.whitbread.premierinn.domain.reservation.usecase

import com.whitbread.premierinn.domain.payment.AmendReservationDomainDetails
import com.whitbread.premierinn.domain.reservation.repository.AmendedReservationRepository
import io.reactivex.Single
import javax.inject.Inject

class CompletePendingAmendUseCase @Inject constructor(private val repository: AmendedReservationRepository) {

    operator fun invoke(pares: String): Single<AmendReservationDomainDetails?> {
        return repository.completePendingAmend(pares)
    }
}