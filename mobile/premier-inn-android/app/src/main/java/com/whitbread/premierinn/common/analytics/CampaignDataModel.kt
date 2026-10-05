package com.whitbread.premierinn.common.analytics

import android.os.Parcelable
import com.whitbread.premierinn.data.common.EMPTY_STRING
import kotlinx.parcelize.Parcelize

@Parcelize
data class CampaignDataModel(
    val campaignId: String = EMPTY_STRING,
    val googleId: String = EMPTY_STRING,
    val microsoftId: String = EMPTY_STRING,
): Parcelable
