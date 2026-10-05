package com.whitbread.premierinn.domain.ciol.entity

import com.whitbread.premierinn.domain.common.PriceDomain
import com.whitbread.premierinn.domain.graphql.hdp.entity.UpsellDomainItem

data class PriceBreakdown(
    val outstandingBalance: PriceDomain,
    val roomSelections: List<PriceBreakdownRoomSelection>,
    val nights: Int,
)

data class PriceBreakdownRoomSelection(
    val reservationId: String? = null,
    val packagesSelection: List<UpsellDomainItem>
)