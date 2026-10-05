package com.whitbread.premierinn.domain.reservation.usecase

import com.whitbread.premierinn.domain.common.Upsell
import com.whitbread.premierinn.domain.common.UpsellAvailable
import com.whitbread.premierinn.domain.reservation.createBreakfast
import com.whitbread.premierinn.domain.reservation.entity.Reservation
import com.whitbread.premierinn.domain.reservation.repository.AmendedReservationRepository
import io.reactivex.Observable
import io.reactivex.Single
import javax.inject.Inject

class AddRoomAndUpsellToStorageUseCase @Inject constructor(private val repository: AmendedReservationRepository) {

     fun invoke(reservationId: String,
                selectedUpsell: UpsellAvailable? = null): Observable<StoreUpdatedAmendedReservation.UpdateState> {
        return repository.getAmendedReservation(reservationId).take(1)
            .flatMap { reservation ->
                val upsells = if (selectedUpsell != null) {
                    createBreakfast(selectedUpsell,
                        reservation.departure, reservation.roomsCriteria, reservation.upsells)
                } else null
                createAmendedReservation(reservation, upsells)
            }
            .startWith(StoreUpdatedAmendedReservation.UpdateState.Loading)
    }

    private fun createAmendedReservation(originalReservation: Reservation,
                                         upsells: List<Upsell>? = null): Observable<StoreUpdatedAmendedReservation.UpdateState> {
        val updatedReservation = if (upsells != null) {
            originalReservation.copy(roomsCriteria = originalReservation.roomsCriteria,
                roomsLeadGuest = originalReservation.roomsLeadGuest,
                upsells = upsells,
                roomsBreakdown = originalReservation.roomsBreakdown)
        } else {
            originalReservation.copy(roomsCriteria = originalReservation.roomsCriteria,
                upsells = originalReservation.upsells.filter { it.category != Upsell.Category.BREAKFAST },
                roomsLeadGuest = originalReservation.roomsLeadGuest,
                roomsBreakdown = originalReservation.roomsBreakdown)
        }
        return performUpdateAddRoom(originalReservation.bookingReference, updatedReservation)
    }

    internal fun performUpdateAddRoom(reservationId: String, updatedReservation: Reservation):
    Observable<StoreUpdatedAmendedReservation.UpdateState> {
        return if (updatedReservation.roomsLeadGuest.isNotEmpty()) {
            repository.updateReservationAddRoom(reservationId, updatedReservation)
                    .andThen(Single.just<StoreUpdatedAmendedReservation.UpdateState>(StoreUpdatedAmendedReservation.UpdateState.Updated))
                    .toObservable().cache()
                    .onErrorReturn { e -> StoreUpdatedAmendedReservation.UpdateState.Error(exception = e) }
        } else {
            Observable.just(StoreUpdatedAmendedReservation.UpdateState.Loading)
        }
    }
}