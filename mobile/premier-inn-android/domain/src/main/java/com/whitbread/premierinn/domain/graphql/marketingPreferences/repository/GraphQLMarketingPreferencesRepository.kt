package com.whitbread.premierinn.domain.graphql.marketingPreferences.repository

import com.whitbread.premierinn.domain.graphql.marketingPreferences.entity.UpdateMarketingPreferencesResponseDomain
import io.reactivex.Single

interface GraphQLMarketingPreferencesRepository {
    fun updateMarketingPreferences(
        email: String,
        optIn: Boolean,
        doubleOptIn: Boolean,
        brandCodes: List<String>,
        isoCountryCode: String
    ): Single<UpdateMarketingPreferencesResponseDomain>
}