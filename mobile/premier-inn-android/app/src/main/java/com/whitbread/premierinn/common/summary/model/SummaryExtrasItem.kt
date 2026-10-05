package com.whitbread.premierinn.common.summary.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SummaryExtrasItem(
    val id: String,
    val name: String? = null,
    val price: SummaryPrice,
    val description: String? = null,
    var selected: Boolean = false
) : Parcelable
