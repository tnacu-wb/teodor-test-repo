package com.whitbread.premierinn.amend.amendcalendar

import com.whitbread.premierinn.amend.AmendStringProvider
import com.whitbread.premierinn.common.AsyncResult
import com.whitbread.premierinn.domain.common.PriceDomain
import com.whitbread.premierinn.domain.reservation.entity.Reservation
import com.whitbread.premierinn.domain.reservation.usecase.StoreUpdatedDateUseCase

data class AmendCalendarState(
        private val amendedReservation: AsyncResult<Reservation>? = null,
        private val updateDatesState : StoreUpdatedDateUseCase.UpdateDateState? = null,
        private val amendedTotal: PriceDomain? = null,
        private val errorMessageStays : String? = null,
        private val stringResourceProvider: AmendStringProvider
) {

    val isLoading: Boolean
        get() = amendedReservation is AsyncResult.Loading

    val getErrorMessageChangeStays = errorMessageStays

}