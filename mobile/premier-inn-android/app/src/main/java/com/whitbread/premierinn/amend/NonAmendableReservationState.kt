package com.whitbread.premierinn.amend

import com.whitbread.premierinn.common.AsyncResult
import com.whitbread.premierinn.domain.booking.entity.Booking

data class NonAmendableReservationState (
        val reservation: AsyncResult<Booking>? = null,
        val cancelBookingInProgress: Boolean = false) {

    val isLoading: Boolean
        get() = reservation is AsyncResult.Loading

    val cancellable: Boolean?
        get() = if (reservation is AsyncResult.Success && reservation.data != null) {
            reservation.data.cancellable
        } else null
}