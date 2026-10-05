package com.whitbread.premierinn.common.model

import com.whitbread.premierinn.domain.common.DailyRate
import com.whitbread.premierinn.domain.common.GBP
import com.whitbread.premierinn.domain.common.PriceDomain
import com.whitbread.premierinn.domain.common.Room
import com.whitbread.premierinn.domain.common.RoomOpera
import com.whitbread.premierinn.domain.common.RoomType

object RoomOperaFixture {

    fun aRoomOpera(
        number: Int = 1,
        type: RoomType = RoomType.DOUBLE,
        pmsRoomType: String = "PPLDBL",
        cost: PriceDomain = PriceDomain(200.00f, GBP),
        cityTax: PriceDomain? = null,
        lettingType: String = "DB",
        cot: Boolean = false,
        adults: Int = 1,
        children: Int = 1,
        infants: Int = 1,
        dailyRates: List<DailyRate> = emptyList()
    ): RoomOpera {
        return RoomOpera(
            number = number,
            type = type,
            pmsRoomType = pmsRoomType,
            cost = cost,
            cityTax = cityTax,
            lettingType = lettingType,
            cot = cot,
            adults = adults,
            children = children,
            infants = infants,
            dailyRates = dailyRates,
            specialRequests = emptyList(),
            baseRateAmount = null
        )
    }
}