package com.whitbread.premierinn.domain.common

import com.whitbread.premierinn.domain.hotel.entity.Hotel

/**
 *
 */
data class HotelAvailability(val hotel: Hotel,
                             val distance: Distance,
                             val ratePlans: List<RatePlanSRP>?,
                             val availability: Availability) {

    val isFullyBooked = availability == Availability.SOLD_OUT

    val cheapestPlan: RatePlanSRP?
        get() {
            return ratePlans?.sortedBy { it.totalCost.amount }?.firstOrNull()
        }
}