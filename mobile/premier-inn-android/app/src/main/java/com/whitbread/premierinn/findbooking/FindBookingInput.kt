package com.whitbread.premierinn.findbooking

import android.os.Parcelable
import com.whitbread.premierinn.common.analytics.CampaignDataModel
import kotlinx.parcelize.Parcelize

@Parcelize
data class FindBookingInput(
    val arrivalDate: String,
    val bookingReference: String,
    val lastName: String,
    val campaignModel: CampaignDataModel
) : Parcelable
