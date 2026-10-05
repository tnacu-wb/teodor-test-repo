package com.whitbread.premierinn.landing.model

import android.os.Parcelable
import com.whitbread.premierinn.api.response.search.SearchItemInput
import com.whitbread.premierinn.common.analytics.CampaignDataModel
import com.whitbread.premierinn.searchresults.SearchResultsInput
import kotlinx.parcelize.Parcelize

@Parcelize
data class LandingInputModel @JvmOverloads constructor(
    val promoCodeInput: PromoCodeInput? = null,
    val searchItem: SearchItemInput? = null,
    val searchResultsInput: SearchResultsInput? = null,
    var campaignModel: CampaignDataModel? = null
) : Parcelable
