package com.whitbread.premierinn.ciol.entity.upsells

import kotlinx.parcelize.Parcelize

@Parcelize
data class BreakfastUiModel(
    val minPrice: Double?,
    val maxPrice: Double?,
    val currency: String,
    val imageUrl: String,
    val meals: List<MealUiModel>,
    var displayAsEnabled: Boolean
) : UpsellEntry() {

    fun isSelected() = meals.any { it.noOfSelections > 0 }

    fun isPreSelected() = meals.any { it.preselectedNoOfSelections > 0 }
}
