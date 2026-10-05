package com.whitbread.premierinn.data.checkinonline

import com.whitbread.premierinn.domain.ciol.entity.PriceBreakdown
import com.whitbread.premierinn.domain.ciol.repository.PriceBreakdownRepository
import com.whitbread.premierinn.domain.common.PriceDomain
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class PriceBreakdownRepositoryImpl : PriceBreakdownRepository {

    private val _priceBreakdownState =
        MutableStateFlow(PriceBreakdown(PriceDomain.createDefault(), emptyList(), 0))
    private val priceBreakdownState: StateFlow<PriceBreakdown> = _priceBreakdownState

    override fun update(priceBreakdown: PriceBreakdown) {
        _priceBreakdownState.update { priceBreakdown }
    }

    override suspend fun retrieve() = priceBreakdownState
}
