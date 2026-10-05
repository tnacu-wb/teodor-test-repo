package com.whitbread.premierinn.data.remote

import com.google.gson.annotations.SerializedName
data class PromoContent(
    @SerializedName("homepageBanner") val homepageBanner: HomepageBanner,
    @SerializedName("srpBanner") val srpBanner: SrpBanner
)

data class HomepageBanner(
    @SerializedName("title") val title: String,
    @SerializedName("datePrefixText") val datePrefixText: String,
    @SerializedName("date") val date: String,
    @SerializedName("discountAmount") val discountAmount: String,
    @SerializedName("discountPercentageSign") val discountPercentageSign: String,
    @SerializedName("discountText") val discountText: String,
    @SerializedName("offerDescription") val offerDescription: String,
    @SerializedName("buttonText") val buttonText: String,
    @SerializedName("disclaimer") val disclaimer: String,
    @SerializedName("terms") val terms: Terms

)

data class SrpBanner(
    @SerializedName("title") val title: String,
    @SerializedName("date") val date: String,
    @SerializedName("subTitle") val subTitle: String,
    @SerializedName("terms") val terms: Terms,

)

data class Terms(
    @SerializedName("text") val text: String,
    @SerializedName("url") val url: String
)