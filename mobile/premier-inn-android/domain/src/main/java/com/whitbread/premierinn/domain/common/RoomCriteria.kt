package com.whitbread.premierinn.domain.common

import com.google.gson.annotations.SerializedName
import com.whitbread.premierinn.domain.common.RoomType.DOUBLE

data class RoomCriteria(
    @SerializedName("a") val numberOfAdults: Int,
    @SerializedName("b") val numberOfChildren: Int,
    @SerializedName("c") val numberOfInfants: Int = 0,
    @SerializedName("d") val includeCot: Boolean,
    @SerializedName("e") val roomType: RoomType,
    @SerializedName("f") val roomNumber: Int = 1,
    @SerializedName("g") val roomId: String = EMPTY_STRING_DOMAIN,
    @SerializedName("h") val hotelBrand: String = EMPTY_STRING_DOMAIN
) {
    companion object {
        val ADULTS_RANGE: IntRange = 1..2
        val CHILDREN_RANGE: IntRange = 0..2
        val INFANTS_RANGE: IntRange = 0..1

        @JvmOverloads
        fun createWithDefaults(adults: Int = 1,
                               children: Int = 0,
                               infants: Int = 0,
                               cotIncluded: Boolean = false,
                               roomType: RoomType = DOUBLE,
                               roomNumber: Int = 0,
                               roomId: String = ""): RoomCriteria {

            return RoomCriteria(numberOfAdults = adults, numberOfChildren = children,
                    numberOfInfants = infants, includeCot = cotIncluded,
                    roomType = roomType, roomNumber = roomNumber, roomId = roomId,
                    hotelBrand = EMPTY_STRING_DOMAIN)
        }
    }

}