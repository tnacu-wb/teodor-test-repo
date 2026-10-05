package com.whitbread.premierinn.domain.reservation.entity

import com.whitbread.premierinn.domain.graphql.hdp.entity.UpsellDomainItem

data class RoomSelectedUpsells(
    val roomId: String,
    val selectedUpsells: List<UpsellDomainItem>
)