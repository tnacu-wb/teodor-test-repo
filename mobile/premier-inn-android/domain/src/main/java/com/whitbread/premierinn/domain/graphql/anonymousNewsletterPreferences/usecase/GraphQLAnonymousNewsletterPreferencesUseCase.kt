package com.whitbread.premierinn.domain.graphql.anonymousNewsletterPreferences.usecase

import com.whitbread.premierinn.domain.graphql.anonymousNewsletterPreferences.entity.AnonymousNewsletterPreferencesDomain
import com.whitbread.premierinn.domain.graphql.anonymousNewsletterPreferences.repository.GraphQLAnonymousNewsletterPreferencesRepository
import io.reactivex.Single
import javax.inject.Inject

class GraphQLAnonymousNewsletterPreferencesUseCase @Inject constructor(
    private val repository: GraphQLAnonymousNewsletterPreferencesRepository
) {
    fun getAnonymousNewsletterPreferences(
        email: String,
        brandCode: String,
        countryOfResidence: String,
        language: String
    ): Single<AnonymousNewsletterPreferencesDomain> {
        return repository.getAnonymousNewsletterPreferences(
            email = email,
            brandCode = brandCode,
            countryOfResidence = countryOfResidence,
            language = language
        )
    }
}