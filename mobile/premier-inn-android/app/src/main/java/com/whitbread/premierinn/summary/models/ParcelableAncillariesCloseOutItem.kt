package com.whitbread.premierinn.summary.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ParcelableAncillariesCloseOutItem(
    val startDate: String,
    val endDate: String,
    val upsellCodes: String,
) : Parcelable