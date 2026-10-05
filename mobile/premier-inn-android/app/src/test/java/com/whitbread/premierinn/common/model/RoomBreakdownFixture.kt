package com.whitbread.premierinn.common.model

import com.whitbread.premierinn.common.utils.StringUtils
import com.whitbread.premierinn.domain.common.GBP
import com.whitbread.premierinn.domain.common.PriceDomain
import com.whitbread.premierinn.domain.common.RoomBreakdown

object RoomBreakdownFixture {

    fun aRoomBreakdown(
            roomId: String = StringUtils.EMPTY_STRING,
            totalRoomCost: PriceDomain = PriceDomain(200.00f, GBP)
    ): RoomBreakdown {
        return RoomBreakdown(
                totalRoomCost = totalRoomCost,
                roomId = roomId
        )
    }
}