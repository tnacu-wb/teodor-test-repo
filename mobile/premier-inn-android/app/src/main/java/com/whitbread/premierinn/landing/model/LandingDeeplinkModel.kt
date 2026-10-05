package com.whitbread.premierinn.landing.model

import android.os.Parcelable
import com.whitbread.premierinn.common.analytics.CampaignDataModel
import kotlinx.android.parcel.Parcelize

@Parcelize
data class LandingDeeplinkModel(
    val promoCodeInput: PromoCodeInput? = null,
    val campaignModel: CampaignDataModel? = null
): Parcelable
