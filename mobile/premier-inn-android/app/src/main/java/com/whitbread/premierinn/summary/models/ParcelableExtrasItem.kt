package com.whitbread.premierinn.summary.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ParcelableExtrasItem(
    val id: String,
    val name: String? = null,
    val price: Double? = null,
    val description: String? = null,
    val imageSrc: String? = null,
    val currency: String? = null,
    val order: Int? = null,
    val available: Int? = null
) : Parcelable