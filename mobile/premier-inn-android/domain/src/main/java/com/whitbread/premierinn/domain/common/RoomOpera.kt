package com.whitbread.premierinn.domain.common

/**
 *
 */
data class RoomOpera(val number: Int,
                     val type: RoomType,
                     val pmsRoomType: String,
                     val cost: PriceDomain,
                     val cityTax: PriceDomain? = null,
                     val lettingType: String,
                     val cot: Boolean,
                     val adults: Int,
                     val children: Int,
                     val infants: Int,
                     val dailyRates: List<DailyRate> = emptyList(),
                     val baseRateAmount: Float?,
                     val specialRequests: List<String>?)