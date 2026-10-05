package com.whitbread.premierinn.domain.alternativeroom.entity

import com.whitbread.premierinn.domain.common.PriceDomain

data class TwinRoomChoices(
    val roomId: Int,
    val type: String,
    val numberOfAdults: Int,
    val numberOfChildren: Int,
    val lettingType: String,
    val roomInfo: RoomInfo,
    val alternativeLettingType: List<RoomInfo>?,
    val isTwinRoomOption: Boolean,
    val totalCost: PriceDomain
)

data class RoomInfo(val lettingType: String, val price: PriceDomain)