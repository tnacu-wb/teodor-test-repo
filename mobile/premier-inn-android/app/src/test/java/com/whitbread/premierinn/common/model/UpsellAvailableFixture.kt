package com.whitbread.premierinn.common.model

import com.whitbread.premierinn.domain.common.PriceDomain
import com.whitbread.premierinn.domain.common.UpsellAttachment
import com.whitbread.premierinn.domain.common.UpsellAvailable

object UpsellAvailableFixture {

    fun aUpsellAvailable(
        description: String = "A Breakfast",
        foodUpsell: Boolean = true,
        freeBreakfastTrigger: Boolean = true,
        availableForChildren: Boolean = true,
        code: String = "12",
        freeBreakfastCode: String = "15",
        legend: String = "KFC",
        unitCost: PriceDomain = PriceDomain(8.99f, "GBP"),
        freeBreakfastOption: Boolean = true,
        attachments: List<UpsellAttachment> = mutableListOf()): UpsellAvailable {
        return UpsellAvailable(
                description,
                foodUpsell,
                freeBreakfastTrigger,
                availableForChildren,
                code,
                freeBreakfastCode,
                legend,
                unitCost,
                freeBreakfastOption,
                attachments

        )
    }
}