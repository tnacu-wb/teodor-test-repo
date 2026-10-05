package com.whitbread.premierinn.amend

import com.whitbread.premierinn.domain.common.PriceDomain
import com.whitbread.premierinn.domain.reservation.entity.Reservation

data class RemoveRoomAvailability (
        val roomToRemove : Reservation,
        val roomsTotalCost : PriceDomain?
)