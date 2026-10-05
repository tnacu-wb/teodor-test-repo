package com.whitbread.premierinn.common.model

import com.whitbread.premierinn.common.utils.StringUtils
import com.whitbread.premierinn.domain.common.GBP
import com.whitbread.premierinn.domain.common.PriceDomain
import com.whitbread.premierinn.domain.common.RatePlan
import com.whitbread.premierinn.domain.common.Room

object RatePlanFixture {

    fun aRatePlan(
            code: String = StringUtils.EMPTY_STRING,
            rateType: String = "Flex",
            totalCost : PriceDomain = PriceDomain(200.00f, GBP),
            cityTax: PriceDomain? = null,
            roomList: List<Room> = listOf(RoomFixture.aRoom())
    ) : RatePlan {
        return RatePlan(
                code = code,
                rateType = rateType,
                totalCost = totalCost,
                cityTax = cityTax,
                roomList = roomList
        )
    }

}