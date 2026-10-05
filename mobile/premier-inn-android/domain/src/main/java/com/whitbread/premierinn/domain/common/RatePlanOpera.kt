package com.whitbread.premierinn.domain.common

/**
 *
 */
data class RatePlanOpera(val code: String,
                         val rateType: String,
                         val cellCode: String,
                         val totalCost: PriceDomain,
                         val cityTax: PriceDomain? = null,
                         val roomList: List<RoomOpera>,
                         val alternateRoomList: List<RoomOpera>,
                         val accessibleRoomList: List<RoomOpera>? = null,
                         val twinRoomList: List<RoomOpera>? = null
)