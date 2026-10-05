package com.whitbread.premierinn.hoteldetails.discountcode

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * This data class encapsulates all the necessary information to make
 * a hotel availability request with a discount code.
 */
@Parcelize
data class DiscountCodeInput(
    val hotelCode: String,
    val arrivalDate: String,
    val departureDate: String,
    val roomSearchData: List<RoomSearchData>,
    val channel: String,
    val hotelBrand: String,
    val operaCompanyId: String?,
    val appliedPromoCode: String? = null,
    val appliedPromoMessage: String? = null
) : Parcelable

@Parcelize
data class RoomSearchData(
    val adultsNumber: Int,
    val childrenNumber: Int,
    val cotRequired: Boolean,
    val roomType: String
) : Parcelable