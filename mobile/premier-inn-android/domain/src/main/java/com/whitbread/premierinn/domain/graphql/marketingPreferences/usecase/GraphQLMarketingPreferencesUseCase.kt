package com.whitbread.premierinn.domain.graphql.marketingPreferences.usecase

import com.whitbread.premierinn.domain.graphql.marketingPreferences.entity.UpdateMarketingPreferencesResponseDomain
import com.whitbread.premierinn.domain.graphql.marketingPreferences.repository.GraphQLMarketingPreferencesRepository
import io.reactivex.Single
import javax.inject.Inject

class GraphQLMarketingPreferencesUseCase @Inject constructor(
    private val repository: GraphQLMarketingPreferencesRepository
) {
    fun updateMarketingPreferences(
        email: String,
        optIn: Boolean,
        doubleOptIn: Boolean,
        brandCodes: List<String>,
        isoCountryCode: String
    ): Single<UpdateMarketingPreferencesResponseDomain> {
        return repository.updateMarketingPreferences(
            email = email,
            optIn = optIn,
            doubleOptIn = doubleOptIn,
            brandCodes = brandCodes,
            isoCountryCode = isoCountryCode
        )
    }
}