package com.whitbread.premierinn.common

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ParcelablePromoContent(
    val homepageBanner: ParcelableHomepageBanner,
    val srpBanner: ParcelableSrpBanner) : Parcelable

@Parcelize
data class ParcelableHomepageBanner(
    val title: String,
    val datePrefixText: String,
    val date: String,
    val discountAmount: String,
    val discountPercentageSign: String,
    val discountText: String,
    val offerDescription: String,
    val buttonText: String,
    val disclaimer: String,
    val terms: ParcelableTerms) : Parcelable

@Parcelize
data class ParcelableSrpBanner(
    val title: String,
    val date: String,
    val subTitle: String,
    val terms: ParcelableTerms) : Parcelable

@Parcelize
data class ParcelableTerms(
    val text: String,
    val url: String) : Parcelable