package com.whitbread.premierinn.common.model

import com.whitbread.premierinn.common.utils.StringUtils
import com.whitbread.premierinn.domain.common.GBP
import com.whitbread.premierinn.domain.common.NO_AMOUNT
import com.whitbread.premierinn.domain.common.PriceDomain
import com.whitbread.premierinn.domain.common.Upsell
import org.threeten.bp.LocalDate

object UpsellFixture {

    fun aUpsell(
            quantity : Int = 1,
            category : Upsell.Category = Upsell.Category.BREAKFAST,
            postingDate: LocalDate = LocalDate.now(),
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

    fun noUpsell(
            quantity : Int = 0,
            category : Upsell.Category = Upsell.Category.BREAKFAST,
            postingDate: LocalDate = LocalDate.now(),
            unitCost: PriceDomain = PriceDomain(NO_AMOUNT, GBP),
            code: String = "none",
            legend: String = StringUtils.EMPTY_STRING,
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