package com.whitbread.premierinn.postcodefinder

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class ParcelableAddress(
        val line1: String,
        val line2: String? = null,
        val line3: String? = null,
        val line4: String? = null,
        val line5: String? = null,
        val postcode: String? = null,
        val companyName: String? = null,
        val countryCode: String? = null) : Parcelable