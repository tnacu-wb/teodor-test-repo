package com.whitbread.premierinn.ciol.utils

import android.content.Context
import com.whitbread.premierinn.ciol.entity.upsells.details.RoomSelection
import com.whitbread.premierinn.common.utils.StringUtils
import com.whitbread.premierinn.domain.ciol.entity.UpsellItemId

fun RoomSelection.getAllUpsellsNames(context: Context): String {
    val upsellsWithoutFreeBreakfast = this.selectedUpsells
        .filterNot { upsell -> upsell.getId().isKidsMeal() }
        .filter { upsell -> upsell.getNumberOfSelections() > 0 }

    val allUpsellsNames = upsellsWithoutFreeBreakfast
        .joinToString(StringUtils.LINE_BREAK) { it.getUpsellName(context) }

    val freeBfSelections = upsellsWithoutFreeBreakfast.sumOf { upsell -> upsell.getFreeBreakfastSelections() }
    val freeBfUpsellName = freeBfSelections
        .let {
            UpsellItemId.FREE_CHILD_BREAKFAST.id.getFoodUpsellNameFromCode(
                context, freeBfSelections
            )
        }

    return buildString {
        append(allUpsellsNames)
        if (freeBfSelections > 0) {
            append(StringUtils.LINE_BREAK)
            append(freeBfUpsellName)
        }
    }
}
