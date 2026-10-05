package com.whitbread.premierinn.common.summary.model

import android.os.Parcelable
import com.whitbread.premierinn.domain.common.EMPTY_STRING_DOMAIN
import kotlinx.parcelize.Parcelize

@Parcelize
data class SummaryPrice(
    val amount: Float = 0F,
    val formattedPrice: String = EMPTY_STRING_DOMAIN,
) : Parcelable
