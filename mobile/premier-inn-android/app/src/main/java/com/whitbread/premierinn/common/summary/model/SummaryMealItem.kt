package com.whitbread.premierinn.common.summary.model

import android.os.Parcelable
import com.whitbread.premierinn.data.common.EMPTY_STRING
import kotlinx.parcelize.Parcelize

@Parcelize
data class SummaryMealItem(
    val id: String,
    val name: String,
    val price: SummaryPrice,
    val originalPrice: SummaryPrice? = null,
    val offerTag: String = EMPTY_STRING,
    val description: String,
    val kidsEatFree: Boolean,
    var counter: Int
) : Parcelable
