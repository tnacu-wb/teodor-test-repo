package com.whitbread.premierinn.domain.common.model

import com.whitbread.premierinn.domain.common.GBP
import com.whitbread.premierinn.domain.common.PriceDomain
import com.whitbread.premierinn.domain.common.Upsell
import org.threeten.bp.LocalDate

object UpsellFixture {

    fun aUpsell(
            quantity : Int = 1,
            category : Upsell.Category = Upsell.Category.BREAKFAST,
            postingDate: LocalDate = LocalDate.of(2020, 8, 9),
            unitCost: PriceDomain = PriceDomain(8.99f, GBP),
            code: String = "12",
            legend: String = "KFC",
            roomId: String = ""): Upsell {
        return Upsell(
                quantity,
                category,
                legend,
                postingDate,
                unitCost,
                code,
                roomId
        )
    }
}