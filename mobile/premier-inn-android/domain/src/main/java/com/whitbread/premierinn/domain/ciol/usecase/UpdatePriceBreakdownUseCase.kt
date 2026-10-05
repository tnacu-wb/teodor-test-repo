package com.whitbread.premierinn.domain.ciol.usecase

import com.whitbread.premierinn.domain.ciol.entity.PriceBreakdown
import com.whitbread.premierinn.domain.ciol.repository.PriceBreakdownRepository
import javax.inject.Inject

class UpdatePriceBreakdownUseCase @Inject constructor(
    private val priceBreakdownRepository: PriceBreakdownRepository
) {
    operator fun invoke(priceBreakdown: PriceBreakdown) = priceBreakdownRepository.update(priceBreakdown)
}
