package com.whitbread.premierinn.data.graphql.mapper

import com.whitbread.premierinn.data.remote.graphql.contracts.AnonymousNewsletterPreferencesGraphQLContract
import com.whitbread.premierinn.domain.graphql.anonymousNewsletterPreferences.entity.AnonymousNewsletterPreferencesDomain

fun AnonymousNewsletterPreferencesGraphQLContract.AnonymousNewsletterPreferencesData.mapToAnonymousNewsletterPreferencesDomain(): AnonymousNewsletterPreferencesDomain {
    return this.data?.anonymousNewsletterPreferences?.let {
        AnonymousNewsletterPreferencesDomain(
            optIn = it.optIn ?: false,
            suppressMarketingCheckbox = it.suppressMarketingCheckbox ?: false
        )
    } ?: AnonymousNewsletterPreferencesDomain(
        optIn = false,
        suppressMarketingCheckbox = false
    )
}