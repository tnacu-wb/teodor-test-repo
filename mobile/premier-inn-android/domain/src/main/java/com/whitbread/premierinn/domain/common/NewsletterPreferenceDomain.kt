package com.whitbread.premierinn.domain.common

data class NewsletterPreferenceDomain(
    val contactChannelId: String,
    val newsletterPermission: List<NewsletterPermissionDomain>
)
