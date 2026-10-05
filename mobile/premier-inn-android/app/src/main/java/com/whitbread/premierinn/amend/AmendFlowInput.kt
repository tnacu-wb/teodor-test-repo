package com.whitbread.premierinn.amend

import com.whitbread.premierinn.common.utils.sumByFloat
import com.whitbread.premierinn.domain.common.NO_AMOUNT
import com.whitbread.premierinn.domain.common.PriceDomain
import com.whitbread.premierinn.domain.common.Upsell
import java.util.Locale

fun priceDifference(amendCost: Float, originalCost: PriceDomain): PriceDomain {
    val totalPrice = String.format(Locale.UK, "%.2f",  amendCost - originalCost.amount).toFloat()
    return when {
        totalPrice != NO_AMOUNT -> PriceDomain(totalPrice, originalCost.currency)
        else -> PriceDomain(NO_AMOUNT, originalCost.currency)
    }
}

fun totalDatesCost(newTotalCost: PriceDomain, originalRoomTotalCost: Float): PriceDomain {
    val totalPrice = when (newTotalCost.amount) {
        NO_AMOUNT -> newTotalCost.amount
        else -> newTotalCost.amount - originalRoomTotalCost
    }

    return when {
        totalPrice != NO_AMOUNT -> PriceDomain(totalPrice, newTotalCost.currency)
        else -> PriceDomain(NO_AMOUNT, newTotalCost.currency)
    }
}

fun calculateUpsellTotalCost(roomUpsells: List<Upsell>): Float {
    return roomUpsells.sumByFloat { it.quantity * it.unitCost.amount }
}