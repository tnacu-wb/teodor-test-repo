package com.whitbread.premierinn.domain.common

/**
 *
 */
data class RatePlan(val code: String,
                    val rateType: String,
                    val totalCost: PriceDomain,
                    val cityTax: PriceDomain? = null,
                    val roomList: List<Room>)