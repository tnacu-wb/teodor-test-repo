package com.whitbread.premierinn.reviewbooking

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AdditionalInformation(
    val question: String,
    val answer: String
): Parcelable
