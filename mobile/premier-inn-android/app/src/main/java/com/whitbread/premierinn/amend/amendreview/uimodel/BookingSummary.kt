package com.whitbread.premierinn.amend.amendreview.uimodel

import com.whitbread.premierinn.domain.common.PriceDomain
import com.whitbread.premierinn.domain.common.RoomCriteria
import com.whitbread.premierinn.domain.common.Upsell
import org.threeten.bp.LocalDate

data class BookingSummary(
        val roomCriteria: List<RoomCriteria>,
        val upsells: List<Upsell>,
        val amendedDates: Pair<LocalDate, LocalDate>,
        val amendedTotal: PriceDomain,
        val originalTotalCost: PriceDomain
)