package com.whitbread.premierinn.domain.common.model

import com.whitbread.premierinn.domain.common.GBP
import com.whitbread.premierinn.domain.common.PriceDomain
import com.whitbread.premierinn.domain.common.Room
import com.whitbread.premierinn.domain.common.RoomType

object RoomFixture {

    fun aRoom(
            number: Int = 1,
            type: RoomType = RoomType.DOUBLE,
            cost : PriceDomain = PriceDomain(200.00f, GBP),
            cityTax: PriceDomain? = null,
            lettingType: String = "DB"
    ) : Room {
        return Room(
                number = number,
                type = type,
                cost = cost,
                cityTax = cityTax,
                lettingType = lettingType
        )
    }

}