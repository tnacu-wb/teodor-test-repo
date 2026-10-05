package com.whitbread.premierinn.hoteldetails

import android.os.Parcelable
import com.whitbread.premierinn.domain.bathroomselection.entity.BathroomType
import kotlinx.parcelize.Parcelize

@Parcelize
data class AccessibilityFacilities(val hasWetRooms: Boolean, val hasLoweredBaths: Boolean): Parcelable {
    fun offersBathroomType(bathroomType: BathroomType): Boolean {
        return when (bathroomType) {
            BathroomType.WET_ROOM -> hasWetRooms
            BathroomType.LOWERED_BATH -> hasLoweredBaths
        }
    }
}