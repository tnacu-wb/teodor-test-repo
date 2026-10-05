package com.whitbread.premierinn.summary.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ParcelableMenuAndAllergyInfo(
    val name: String,
    val menuOrAllergyInfoSrc: String
) : Parcelable