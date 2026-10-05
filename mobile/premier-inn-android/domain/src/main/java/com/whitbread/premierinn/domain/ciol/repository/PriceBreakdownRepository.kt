package com.whitbread.premierinn.domain.ciol.repository

import com.whitbread.premierinn.domain.ciol.entity.PriceBreakdown
import kotlinx.coroutines.flow.Flow

interface PriceBreakdownRepository {
    fun update(priceBreakdown: PriceBreakdown)
    suspend fun retrieve(): Flow<PriceBreakdown>
}
