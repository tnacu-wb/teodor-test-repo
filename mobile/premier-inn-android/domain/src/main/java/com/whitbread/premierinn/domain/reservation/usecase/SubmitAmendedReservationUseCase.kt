package com.whitbread.premierinn.domain.reservation.usecase

import com.whitbread.premierinn.domain.reservation.repository.AmendedReservationRepository
import io.reactivex.Completable
import org.threeten.bp.LocalDate
import javax.inject.Inject

class SubmitAmendedReservationUseCase @Inject constructor(private val repository: AmendedReservationRepository) {

    fun updateBooking(reservationId: String, arrival: LocalDate, departure: LocalDate, leadGuestSurname: String): Completable {
        return repository.updateBooking(reservationId, arrival, departure, leadGuestSurname)
    }
}