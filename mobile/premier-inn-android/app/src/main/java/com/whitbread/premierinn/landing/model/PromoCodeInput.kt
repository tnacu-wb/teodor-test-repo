package com.whitbread.premierinn.landing.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

const val CODE_APP_INCENTIVE = "appIncentive"
const val CODE_FREE_BREAKFAST_INCENTIVE = "PREBF"

@Parcelize
data class PromoCodeInput(
    val promoType: String? = null,
    val promoCode: String? = null
): Parcelable
