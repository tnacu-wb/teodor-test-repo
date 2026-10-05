package com.whitbread.premierinn.common.model

import com.whitbread.premierinn.common.utils.StringUtils
import com.whitbread.premierinn.domain.common.EMPTY_STRING_DOMAIN
import com.whitbread.premierinn.domain.common.GBP
import com.whitbread.premierinn.domain.common.PriceDomain
import com.whitbread.premierinn.domain.common.RatePlanOpera
import com.whitbread.premierinn.domain.common.RoomOpera

object RatePlanFixtureOpera {

    fun aRatePlanOpera(
        code: String = StringUtils.EMPTY_STRING,
        rateType: String = "Flex",
        totalCost: PriceDomain = PriceDomain(200.00f, GBP),
        cityTax: PriceDomain? = null,
        roomList: List<RoomOpera> = listOf(RoomOperaFixture.aRoomOpera()),
        alternateRoomList: List<RoomOpera> = listOf(RoomOperaFixture.aRoomOpera()),
        accessibleRoomList: List<RoomOpera> = listOf(RoomOperaFixture.aRoomOpera())
    ): RatePlanOpera {
        return RatePlanOpera(
            code = code,
            rateType = rateType,
            cellCode = EMPTY_STRING_DOMAIN,
            totalCost = totalCost,
            cityTax = cityTax,
            roomList = roomList,
            alternateRoomList = alternateRoomList,
            accessibleRoomList = accessibleRoomList
        )
    }
}