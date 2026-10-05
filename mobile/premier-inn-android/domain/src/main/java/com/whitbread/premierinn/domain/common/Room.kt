package com.whitbread.premierinn.domain.common

/**
 *
 */
data class Room(val number: Int,
                val type: RoomType,
                val cost: PriceDomain,
                val cityTax: PriceDomain? = null,
                val lettingType: String,
                val dailyRates: List<DailyRate>? = emptyList())