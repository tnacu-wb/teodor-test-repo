package com.whitbread.premierinn.reviewbooking

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ParcelableDonationsDomain(
    val description: String,
    val imageSrc: String,
    val informationBox: String,
    val name: String,
    val donationPackages: List<ParcelableDonationPackageDomain>
) : Parcelable

@Parcelize
data class ParcelableDonationPackageDomain(
    val code: String,
    val currency: String,
    val unitPrice: Float
) : Parcelable