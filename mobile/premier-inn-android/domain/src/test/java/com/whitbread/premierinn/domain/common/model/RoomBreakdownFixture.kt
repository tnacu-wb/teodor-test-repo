package com.whitbread.premierinn.domain.common.model

import com.whitbread.premierinn.domain.common.GBP
import com.whitbread.premierinn.domain.common.PriceDomain
import com.whitbread.premierinn.domain.common.RoomBreakdown

object RoomBreakdownFixture {

    fun aRoomBreakdown(
            roomId: String = "",
            totalRoomCost: PriceDomain = PriceDomain(200.00f, GBP)
    ): RoomBreakdown {
        return RoomBreakdown(
                totalRoomCost = totalRoomCost,
                roomId = roomId
        )
    }

}