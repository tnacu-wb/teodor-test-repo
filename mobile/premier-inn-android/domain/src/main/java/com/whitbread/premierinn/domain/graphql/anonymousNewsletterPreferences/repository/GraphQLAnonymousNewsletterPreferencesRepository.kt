package com.whitbread.premierinn.domain.graphql.anonymousNewsletterPreferences.repository

import com.whitbread.premierinn.domain.graphql.anonymousNewsletterPreferences.entity.AnonymousNewsletterPreferencesDomain
import io.reactivex.Single

interface GraphQLAnonymousNewsletterPreferencesRepository {
    fun getAnonymousNewsletterPreferences(
        email: String,
        brandCode: String,
        countryOfResidence: String,
        language: String
    ): Single<AnonymousNewsletterPreferencesDomain>
}