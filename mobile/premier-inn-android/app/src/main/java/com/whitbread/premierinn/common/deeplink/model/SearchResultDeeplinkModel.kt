package com.whitbread.premierinn.common.deeplink.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import com.whitbread.premierinn.common.analytics.CampaignDataModel

@Parcelize
data class SearchResultDeeplinkModel(
    val placeName: String,
    val placeId: String,
    val day: Int,
    val month: Int,
    val year: Int,
    val nights: Long,
    val children: List<Int>,
    val cots: List<Boolean>,
    val adults: List<Int>,
    val infants: List<Int>,
    val roomsType: List<String>,
    val rooms: Int,
    val campaignModel: CampaignDataModel
) : Parcelable
