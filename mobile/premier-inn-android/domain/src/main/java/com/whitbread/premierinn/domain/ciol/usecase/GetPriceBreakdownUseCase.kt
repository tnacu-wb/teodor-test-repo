package com.whitbread.premierinn.domain.ciol.usecase

import com.whitbread.premierinn.domain.ciol.repository.PriceBreakdownRepository
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class GetPriceBreakdownUseCase @Inject constructor(
    private val priceBreakdownRepository: PriceBreakdownRepository,
) {

    suspend operator fun invoke() = priceBreakdownRepository.retrieve() as StateFlow
}
