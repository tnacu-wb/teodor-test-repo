package com.whitbread.premierinn.domain.graphql.anonymousNewsletterPreferences.entity

data class AnonymousNewsletterPreferencesDomain(
    val optIn: Boolean,
    val suppressMarketingCheckbox: Boolean
)