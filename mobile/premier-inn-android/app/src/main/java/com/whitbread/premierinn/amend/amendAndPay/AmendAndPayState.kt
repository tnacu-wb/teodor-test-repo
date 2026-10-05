package com.whitbread.premierinn.amend.amendAndPay

import com.whitbread.premierinn.common.AsyncResult
import com.whitbread.premierinn.domain.booking.entity.Booking
import com.whitbread.premierinn.domain.common.EMPTY_STRING_DOMAIN
import com.whitbread.premierinn.domain.graphql.amend.entity.ConfirmAmendLogicDomain
import com.whitbread.premierinn.domain.reservation.entity.Reservation

data class AmendAndPayState(
    private val amendedReservation: AsyncResult<Reservation>? = null,
    private val originalReservation: Reservation? = null,
    val originalBooking: AsyncResult<Booking>? = null,
    val privacyPolicyText: String? = EMPTY_STRING_DOMAIN,
    val totalCost: String? = EMPTY_STRING_DOMAIN,
    val confirmAmendResponse: AsyncResult<ConfirmAmendLogicDomain>? = null) {
    val isLoading: Boolean
        get() = amendedReservation is AsyncResult.Loading || originalBooking is AsyncResult.Loading

    val confirmAmendLogicResponse : ConfirmAmendLogicDomain?
        get() = if (confirmAmendResponse is AsyncResult.Success) {
            confirmAmendResponse.data
        } else null
}