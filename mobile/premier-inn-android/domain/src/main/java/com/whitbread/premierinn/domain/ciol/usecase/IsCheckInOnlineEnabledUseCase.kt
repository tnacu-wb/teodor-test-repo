package com.whitbread.premierinn.domain.ciol.usecase

import com.whitbread.premierinn.domain.common.usecase.IsFeatureOn
import com.whitbread.premierinn.domain.resource.repository.ContentManagedResourceRepository.Key.FEATURE_CIOL
import javax.inject.Inject

/**
 * UseCase which determines if CIOL feature flag is enabled
 */
class IsCheckInOnlineEnabledUseCase @Inject constructor(
    private val isFeatureOn: IsFeatureOn,
) {

    operator fun invoke() = isFeatureOn(FEATURE_CIOL)
}
