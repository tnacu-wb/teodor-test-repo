package com.whitbread.premierinn.domain.common

data class UpsellAvailable(
        val description: String,
        val foodUpsell: Boolean,
        val freeBreakfastTrigger: Boolean,
        val availableForChildren: Boolean,
        val code: String,
        val freeBreakfastCode: String,
        val legend: String,
        val unitCost: PriceDomain,
        val freeBreakfastOption: Boolean,
        val attachments: List<UpsellAttachment?>)

const val FREE_CHILD_BREAKFAST = "Free Child Breakfast"
const val FREE_CHILD_BREAKFAST_OPERA_LEGEND1 = "Free Childrens Breakfast"
const val FREE_CHILD_BREAKFAST_OPERA_LEGEND2 = "Free breakfast for kids"

