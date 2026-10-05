package com.whitbread.premierinn.ciol.analytics

import android.os.Parcelable
import com.whitbread.premierinn.data.common.EMPTY_STRING
import kotlinx.parcelize.Parcelize

@Parcelize
data class CiolCompletionAnalyticsModel(
    val revenue: String = EMPTY_STRING,
    val foodRevenueChange: String = EMPTY_STRING,
    val totalRevenueChange: String = EMPTY_STRING,
    val eciRevenueChange: String = EMPTY_STRING,
    val lcoRevenueChange: String = EMPTY_STRING,
    val wifiRevenueChange: String = EMPTY_STRING,
    val extraCode: String = EMPTY_STRING,
    val extraDescription: String = EMPTY_STRING
) : Parcelable
