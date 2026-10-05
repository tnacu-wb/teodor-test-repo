package com.whitbread.premierinn.ciol.uilogic

import com.whitbread.premierinn.ciol.entity.upsells.BreakfastUiModel
import com.whitbread.premierinn.ciol.entity.upsells.ExtrasItemUiModel
import com.whitbread.premierinn.ciol.entity.upsells.MealUiModel
import com.whitbread.premierinn.ciol.entity.upsells.UpsellsUnselectedHeader
import com.whitbread.premierinn.ciol.entity.upsells.UpsellItem
import com.whitbread.premierinn.ciol.entity.upsells.UpsellsSelectedHeader
import com.whitbread.premierinn.ciol.utils.isKidsMeal
import javax.inject.Inject

class FormatUpsellsList @Inject constructor() {

    operator fun invoke(upsells: List<UpsellItem>): List<UpsellItem> {
        val result = mutableListOf<UpsellItem>()

        val selectedUpsells = upsells.filter { upsell ->
            when(upsell) {
                is ExtrasItemUiModel -> upsell.isSelected() || upsell.isPreSelected()
                is MealUiModel -> upsell.isSelected() || upsell.isPreSelected()
                is BreakfastUiModel -> upsell.isSelected() || upsell.isPreSelected()
                else -> false
            }
        }

        val unselectedUpsells = upsells.filter { upsell ->
            when(upsell) {
                is ExtrasItemUiModel -> !upsell.isSelected() && !upsell.isPreSelected()
                is MealUiModel -> !upsell.isSelected() && !upsell.isPreSelected()
                is BreakfastUiModel -> !upsell.isSelected() && !upsell.isPreSelected()
                else -> false
            }
        }

        result.apply {
            if (selectedUpsells.isNotEmpty()) {
                add(
                    UpsellsSelectedHeader(
                        selectedUpsells.filter {
                            when(it) {
                                is BreakfastUiModel -> true
                                is ExtrasItemUiModel -> true
                                is MealUiModel -> !it.id.isKidsMeal()
                                is UpsellsSelectedHeader -> false
                                UpsellsUnselectedHeader -> false
                            }
                        }.size
                    )
                )
            }
            addAll(selectedUpsells)

            if (unselectedUpsells.size > 1 || unselectedUpsells.size == 1 && unselectedUpsells.firstOrNull()
                    ?.let { upsell -> upsell is MealUiModel && upsell.id.isKidsMeal() } == false
            ) {
                add(UpsellsUnselectedHeader)
            }
            addAll(unselectedUpsells)
        }

        return result
    }
}
