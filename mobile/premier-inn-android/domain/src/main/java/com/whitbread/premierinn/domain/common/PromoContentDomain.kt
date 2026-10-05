package com.whitbread.premierinn.domain.common

data class PromoContentDomain(
    val homepageBanner: HomepageBannerDomain,
    val srpBanner: SrpBannerDomain
)

data class HomepageBannerDomain(
    val title: String,
    val datePrefixText: String,
    val date: String,
    val discountAmount: String,
    val discountPercentageSign: String,
    val discountText: String,
    val offerDescription: String,
    val buttonText: String,
    val disclaimer: String,
    val terms: TermsDomain
)

data class SrpBannerDomain(
    val title: String,
    val date: String,
    val subTitle: String,
    val terms: TermsDomain,
)

data class TermsDomain(
    val text: String,
    val url: String
)
