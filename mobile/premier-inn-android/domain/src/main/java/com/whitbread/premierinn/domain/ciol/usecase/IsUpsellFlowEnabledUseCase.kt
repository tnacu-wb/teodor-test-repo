package com.whitbread.premierinn.domain.ciol.usecase

import com.whitbread.premierinn.domain.common.usecase.IsFeatureOn
import com.whitbread.premierinn.domain.resource.repository.ContentManagedResourceRepository.Key.FEATURE_CIOL_UPSELLS
import javax.inject.Inject

class IsUpsellFlowEnabledUseCase @Inject constructor(
    private val isFeatureOn: IsFeatureOn,
) {
    operator fun invoke() = isFeatureOn(FEATURE_CIOL_UPSELLS)
}
