package com.whitbread.premierinn.domain.reservation.usecase

import com.whitbread.premierinn.domain.reservation.repository.AmendedReservationRepository
import io.reactivex.Observable
import io.reactivex.Single
import javax.inject.Inject

class RemoveStoredRoomUseCase @Inject constructor(
    private val repository: AmendedReservationRepository
) {
    fun invoke(reservationId: String, bookingRef: String): Observable<RemoveRoomStorageState> {
        return removeRoomLocally(reservationId, bookingRef)
            .startWith(RemoveRoomStorageState.Loading)
    }

    private fun removeRoomLocally(
        reservationId: String,
        bookingRef: String
    ): Observable<RemoveRoomStorageState> {
        return repository.removeRoom(reservationId, bookingRef)
            .andThen(Single.just<RemoveRoomStorageState>(RemoveRoomStorageState.Removed))
            .toObservable()
            .onErrorReturn { e -> RemoveRoomStorageState.Error(exception = e) }
    }

    sealed class RemoveRoomStorageState {
        object Loading : RemoveRoomStorageState()
        object Removed : RemoveRoomStorageState()
        data class Error(val exception: Throwable? = null, val msg: String? = null) :
            RemoveRoomStorageState()
    }
}